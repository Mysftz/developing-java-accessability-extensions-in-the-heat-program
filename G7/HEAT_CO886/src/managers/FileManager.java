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
 * @author Dean Ashton
 *
 */

package managers;

import java.util.logging.Logger;
import utils.Settings;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * The manager Class responsible all I/O with file systems
 */
public class FileManager {
  private static Logger log = Logger.getLogger("heat");

  private static FileManager instance = null;

  /** The file name of currently loaded file, or null if a file isn't loaded. */
  private File currentFile;
  
  // directory used in the open file dialog.
  private File openDirectory = new File (System.getProperty("user.home"));

  protected FileManager() {
    /* Exists to prevent instantiation */
  }

  /**
   * Returns an instance of FileManager
   *
   * @return FileManager instance
   */
  public static FileManager getInstance() {
    if (instance == null)
      instance = new FileManager();

    return instance;
  }
  
  public File getOpenDirectory() {
      return openDirectory;
  }
  
  public void setOpenDirectory(File dir) {
      openDirectory = dir;
  }

  /**
   * Returns the currently loaded {@link File}
   *
   * @return the currently loaded {@link File}
   */
  public File getCurrentFile() {
    return currentFile;
  }
  
  public Boolean isWriteable() {
      return currentFile != null && currentFile.canWrite();
  }

  /**
   * Sets the currently loaded {@link File}
   *
   * @param file the desired current file
   */
  private void setCurrentFile(File file) {
    ensureSaved();
    currentFile = file;
    }

  public String readFile() {
      return readFile(getCurrentFile());
  }
  
  /**
   * Reads file from filesystem using a BufferedReader.
   * @param fileToRead 
   * @return String contents of the file, with system specific line separators.
   */
  public String readFile(File fileToRead) {
   
    StringBuffer content = new StringBuffer();
    BufferedReader input = null;

    try {
      input = new BufferedReader(new FileReader(fileToRead));

      String line = null;

      while ((line = input.readLine()) != null) {
        content.append(line);
        content.append(System.getProperty("line.separator"));
      }
    } catch (IOException ex) {
      log.warning("[FileManager]: readFile failed " + fileToRead.getAbsolutePath());
      // view.dialogs.ErrorDialogs.showIOError();
    } finally {
      try {
        if (input != null)
          input.close();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }

    return content.toString();
  }

  /**
   * Writes file to filesystem using a BufferedWriter.
   *
   * @param fileToWrite File to write to on filesystem
   * @param contents Content to write to file.
   */
  public void writeFile(File fileToWrite, String contents) {
    BufferedWriter output = null;

    try {
      output = new BufferedWriter(new FileWriter(fileToWrite));
      output.write(contents);
    } catch (IOException ex) {
      //view.dialogs.ErrorDialogs.showIOError();
    } finally {
      try {
        if (output != null)
          output.close();
      } catch (IOException ex) {
        //
      }
    }
  }
  
  /* Open the given file and make it the current file, if no errors occur.
   * @ return whether an error occurred.
   * 
   */
  public boolean openFile(File openFile) {
    File file = ensureHaskellFileType(openFile);
       
    try {
        if (!file.canRead() && !file.createNewFile()) {
            return false;
        }
        if (file.canWrite()) {
            backupFile(file);
        }
        setCurrentFile(file);
        return true;
    } catch (IOException e) {
        return false;
    }
  }
  
  /*
   * Close the current file, ensuring that it is saved.
   */
  public void closeCurrentFile() {
      setCurrentFile(null);
  }
  
  /* Make a copy with ~ at end of filename
   * 
   */
  public void backupFile(File file) {
    String contents = readFile(file);
    File backupFile = new File(file.getAbsolutePath()+"~");
    writeFile(backupFile,contents);
  }
  
  /**
   * Add suffix .hs if necessary
   * @param file
   * @return 
   */
  public File ensureHaskellFileType(File file) {
      String path = file.getAbsolutePath();
      if (path.endsWith(".hs")) {
          return file;
      } else {
          return new File(path+".hs");
      }
  }

  /**
   * Returns the path to the currently loaded file
   *
   * @return filePath the path to the file
   */
  public String getFilePath() {
    return currentFile.getAbsolutePath();
  }
  
  /**
   * This needs to always work, even when there is no current file.
   * @return 
   */
  public String getFileNameOnly() {
    return currentFile==null?"":currentFile.getName();
  }
  
  /**
   * This needs to always work, even when there is no current file.
   * @return path
   */
  public String getFilePathOnly() {
    if (currentFile==null) {
        return "";
    }
    String path = currentFile.getParent();
    return (path==null?"":path);
  }
  

    /*
     * Save editor content if there is an opened file
     */
  public void ensureSaved() {
      File currentFile = getCurrentFile();
      WindowManager wm = WindowManager.getInstance();

      if (currentFile != null && currentFile.canWrite()) {
        String content = wm.getEditorWindow().getEditorContent();
        writeFile(currentFile, content);
        // wm.getEditorWindow().setModifiedStatus(false);
        log.info("[FileManager]: ensureSaved " + currentFile.getAbsolutePath());
      } 
  }
  
}

