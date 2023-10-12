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
 * @author Chris Olive
 *
 */

package utils;

import managers.WindowManager;

import java.util.logging.Logger;

import java.io.*;



/**
 * InterpreterToConsole
 * Handles input from an external process. This connects to both the stdout or
 * stderr output channels of the external process
 */
public class InterpreterToConsole extends Thread {
  /* InputStream handling the input */
  private InputStream in;
  private Logger log = Logger.getLogger("heat");
  private view.windows.ConsoleWindow cw = WindowManager.getInstance().getConsoleWindow();
  
  /**
   * Constructor
   * @param in The input from the external process
   * @param type A String description of the output type (stdout or stderr)
   */
  public InterpreterToConsole(InputStream in, String type) {
    this.in = in;
  }
  
  public void abort() {
      try {
          in.close();
      } catch (IOException e) {
          log.warning("Closing input stream raises IOException.");
      }
  }
  
  /**
   * Run method invoked by start() on the process.
   */
  public void run() {
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      int letter;
        try {
		  while (!isInterrupted() && (letter = br.read()) != -1) {
			  final char character = (char) letter;
                          //if (Thread.currentThread().isInterrupted()) {
                          //  throw new InterruptedException("Stopped");
                          //}
   		          javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
                            public void run() {
				  cw.charFromInterpreter(character);
                            }
			  });
			  // yield(); // give GUI chance to print the character
		  }
	  } catch (IOException ioe) {
		log.warning("Error getting stream from interpreter.");
                // throw new RuntimeException("Interrupted",ioe);
	  } catch (java.lang.InterruptedException e) {
              // used to stop the thread
		log.warning("InterpreterToConsole interrupted by exception.");
                //throw new RuntimeException("Interrupted",e);
          }  catch (java.lang.reflect.InvocationTargetException e) {
                log.warning("InterpreterToConsole InvocationTargetException.");
		e.printStackTrace();
	  }	
  }
  
}
