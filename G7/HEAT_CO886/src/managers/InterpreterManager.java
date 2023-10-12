/**
 *
 * Copyright (c) 2005 University of Kent
 * Computing Laboratory, Canterbury, Kent, CT2 7NP, U.K
 *
 * This software is the confidential and proprietary information of the
 * Computing Laboratory of the University of Kent ("Confidential Information").
 * You shall not disclose such confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with
 * the University.
 *
 * @author Chris Olive, Sergei Krot
 *
 */

package managers;

import java.util.logging.Logger;

import utils.*;
import utils.parser.*;
import java.io.File;

import java.lang.ProcessBuilder;
import java.util.LinkedList;

import java.lang.Process;
import java.io.OutputStream;
import java.io.IOException;

/**
 * The manager Class that handles all interaction with the Interpreter process
 */
public class InterpreterManager {
  private static Logger log = Logger.getLogger("heat");
  private static InterpreterManager instance = null;
  /* input channel from the interpreter */
  private InterpreterToConsole in;
  private String INTERPRETER_NOT_LOADED = "An interpreter could not be loaded, please check your settings point to the full path (example C:\\Program Files\\ghc7.03\\ghci.exe)";  
 
  private WindowManager wm = WindowManager.getInstance();
  private SettingsManager sm = SettingsManager.getInstance();
  private Process proc;
  
  private OutputStream os;
  final private char startOfPrompt = '\1';
  final private char endOfPrompt = '\2';
  
  
  public boolean isStartOfPrompt(char c) {
	  return c == startOfPrompt;
  }
  
  public boolean isEndOfPrompt(char c) {
	  return c == endOfPrompt;
  }
  
  public boolean isDisabledCommand(String cmd) {
	  cmd = cmd.trim().toLowerCase();
	  return (cmd.startsWith(":q"));
  }
  
  
  /* Check that the given line is part of an error message
   * that started on an earlier line.
   * Here: line is empty or starts with indentation of at least 4 spaces.
   */
  public boolean checkForErrorContinuation(String line) {
      return (line.trim().equals("") || line.indexOf("    ") == 0);
  }
  
  /* Check that the given line starts an error message.
   * Also obtain line number of error message and highlight it in editor.
   */
  public boolean checkForError(String line, boolean focusOnError) {
      // expect format  <filename> : <line> : <column> :
      // and no other colons
      FileManager fm = FileManager.getInstance();
      int fstColon = line.indexOf(":");
      if (fstColon == -1) return false;
      int sndColon = line.indexOf(":",fstColon+1);
      if (sndColon == -1) return false;
      int thdColon = line.indexOf(":",sndColon+1);
      if (thdColon == -1) return false;
      String filename = line.substring(0,fstColon).trim();
      if (!(filename.equals("<interactive>") || 
              new java.io.File(fm.getFilePathOnly(),filename).exists())) 
          return false;
      try {
          int lineNo = Integer.parseInt(line.substring(fstColon+1,sndColon));
          int colNo = Integer.parseInt(line.substring(sndColon+1,thdColon));
          if (filename.equals(fm.getFileNameOnly()) && focusOnError && lineNo >= 0) {
              wm.getEditorWindow().setLineMark(lineNo);
              wm.getEditorWindow().focusLine(lineNo);
          }
          return true;
      } catch (NumberFormatException e) {
          log.warning("[InterpreterManager] Could not parse line number.");
      }
      return false;
   }
  
  public void send(String txt) {	  
	  try {
		  os.write(txt.getBytes());
		  os.flush();
		  log.warning("send: " + txt);
	  } catch(IOException e) {
    	  e.printStackTrace();
      }
      
  }
  
  
  protected InterpreterManager() {
    /* Exists to prevent instantiation */
  }

  public static InterpreterManager getInstance() {
    if (instance == null)
      instance = new InterpreterManager();
    return instance;
  }
     
     /**
     * Reloads the currently open file
     */
    public void compile() {
        FileManager fm = FileManager.getInstance();
        String filename = fm.getFileNameOnly();
        String filepath = fm.getFilePathOnly();
        send(":cd " + filepath + "\n");  // no quotes, spaces are accepted
     	send(":load \"" + filename + "\"\n");  // need quotes for spaces to work
    }
    
    public void unload() {
    	send(":load\n");  // no file name
    }
  
  /**
   * Start the interpreter process and create the connections to it
   */
  public void startProcess(boolean withCompilation) {
    try {
      SettingsManager sm = SettingsManager.getInstance();
      FileManager fm = FileManager.getInstance();
      String sep = System.getProperty("file.separator");
      LinkedList<String> command = new LinkedList<String>();
      /* Path to Interpreter */
      command.add(sm.getSetting(Settings.INTERPRETER_PATH)); 
      String options = sm.getSetting(Settings.INTERPRETER_OPTS).trim();
      if (!options.isEmpty()) {
        String[] interpreterOptions = options.split("\\s+");
        command.addAll(java.util.Arrays.asList(interpreterOptions));
      }
      String libraryPath = sm.getSetting(Settings.LIBRARY_PATH).trim();
      if (!libraryPath.isEmpty()) {
          command.add("-i" + libraryPath);
      }
      //if (fm.isFileOpen())  // if a file is open, then add its path to interpreter search path 
      //   command.add("-P" + fm.getFileDirPath() + (sep.equals("\\")?";":":")); // path concatenated differently on Windows
      /* set the prompt */
      // command.add("-p" + startOfPrompt + "%s>" + endOfPrompt);  // + getNonDefaultInputString()); 
      /* the actual Haskell file being interpreted */
      // command.add(sm.getSetting(Settings.LIBRARY_PATH) + sep + sm.getSetting(Settings.TEMPORARY_FILE));
      /* Start interpreter */
      ProcessBuilder pb = new ProcessBuilder(command);
      log.warning("Start interpreter: " + command);
      pb.redirectErrorStream(true);
      /* set working directory */
      // pb.directory(new File(libraryPath.isEmpty()?".":libraryPath));
      proc = pb.start();
      in = new InterpreterToConsole(proc.getInputStream(), "OP");
      os = proc.getOutputStream();
      in.start();
      if (withCompilation) {
          compile();
      }
      send(":set prompt " + startOfPrompt + "%s>" + endOfPrompt + "\n");
      log.warning("InterpreterManager: Started Interpreter (" + pb.command() + ")");
    } catch (Exception e) {
      wm.getConsoleWindow().outputError(INTERPRETER_NOT_LOADED);
      log.severe("InterpreterManager: Failed to start interpreter:" +
        e.getMessage());
    }
    wm.setStatusEvaluating();
  }
    
  public void stopInterpreter() {
	  log.warning("stopInterpreter");
         try {
               if (in != null) {
                  // log.info("Abort input stream.");
                  // in.abort();
                  log.info("Interrupt input stream.");
                  in.interrupt(); // in.abort();
                  // log.info("Join input stream.");
                  // in.join();
                  // log.info("Joined.");
               }
               if (proc != null) {
                  log.info("Destroy process.");
                  proc.destroy();
                  // Unfortunately destroying sometimes fails
                  // (experienced when interrupting interactive programs
                  // like noughts and crosses)
                  // Waiting would then just make Heat hang.
                  // So we just let the interpreter process continue running...
                  // It seems to be very active.
                  // log.info("Wait for process.");
                  // proc.waitFor();
                  // int exit = proc.exitValue();
                  // log.warning("Interpreter exited with value " + exit);
               }
          } catch (Exception e) {
              log.severe("Failed destroying the interpreter process.");
          }
  }
  
  public void restartInterpreter() {
          wm.restoreStatus();
          boolean withCompilation = wm.isCompiledCorrect();
	  // wm.setStatusNotCompiled();
	  log.warning("restartInterpreter, compilation " + withCompilation);
	  startProcess(withCompilation);
  }

}
