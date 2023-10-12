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
 * @author Ivan Ivanovski
 *
 */

package utils;

import managers.InterpreterManager;
import managers.WindowManager;

/**
 * Class InterpreterTesting
 * Runs the tests when it receives a notify on the waitOnError object.
 *  
 * @author ii23
 *
 */
public class InterpreterTesting extends Thread {
	
  private WindowManager wm = WindowManager.getInstance();
  private InterpreterManager im = InterpreterManager.getInstance();
  private Object waitOnError = new Object();
  
  /**
   * Notifies InterpreterTesting thread if it is safe to start testing
   * 
   */
  public void notifyWaitOnError(){
	synchronized(waitOnError){
	  waitOnError.notify();
	}
  }
  
  /**
   * Invoked by start().
   */
  public void run(){
//	while(true){
//	  /*Integers used for the animation on the Evaluation Window*/
//	  int i = 3;
//	  int j=0;
//	  try {
//        synchronized(waitOnError){
//          waitOnError.wait();
//        } 	 
//        im.transmitCommand(":s +p"+im.getRunningString());
//        wm.getTreeWindow().runTests();
//	    while (im.getMode()==0){
//	      /*The while runs until the data from the executed tests is fully processed, 
//	       * i.e., the interpreter input enters a new mode of execution*/
//		  wm.setCompileEnabled(false);
//		  wm.setInterruptEnabled(true);
//		  wm.setCompileStatus(i);
//		  j++;
//		  /*Animation*/
//		  if (j==500){
//		    if (i == 7)
//			  i = 3;
//		    else
//		      i++;
//		    j=0;
//		  }
//		}
//		wm.setCompileEnabled(true);
//		wm.setCompileStatus(1);
//		wm.getTreeWindow().refreshTree();
//	  } catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//	  }
//	}  
  }
  
}
