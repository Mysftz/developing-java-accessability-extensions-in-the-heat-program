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
 * @author Dean Ashton, Chris Olive
 *
 */

package view.dialogs;

import managers.WindowManager;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Various system dialogs used within HEAT
 */
public class SystemDialogs {
  private static SystemDialogs instance = null;
  private JFrame frame = new JFrame();
  private String message = new String("");
  private WindowManager wm = WindowManager.getInstance();

  protected SystemDialogs() {
    /* Exists to prevent instantiation */
  }

  public static SystemDialogs getInstance() {
    if (instance == null)
      instance = new SystemDialogs();

    return instance;
  }
  
  /**
   * Shows the exit program command
   * @return The resultant choice from the user
   */
  public int showExit() {
    message = "There are unsaved changes, would you like to save your changes before leaving?";

    int result = JOptionPane.showConfirmDialog(wm.getMainScreenFrame(),
        message, "Confirm Exit", JOptionPane.YES_NO_CANCEL_OPTION,
        JOptionPane.QUESTION_MESSAGE);

    return result;
  }
  
  /**
   * Dialog for when the interpreter needs to be reloaded
   * @return The choice from the user
   */
  public boolean confirmReload() {
    message = "Interpreter needs to be reloaded before changes can be evaluated. Would you like to reload now?";

    int result = JOptionPane.showConfirmDialog(wm.getMainScreenFrame(),
        message, "Reload File", JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE);

    return ((result == 0) ? true : false);
  }
  
  /**
   * Confirm a file needs saving
   * @return The users choice, yes or no
   */
  public boolean confirmSave() {
    message = "There are unsaved changes, would you like to save your changes?";

    int result = JOptionPane.showConfirmDialog(wm.getMainScreenFrame(),
        message, "Save File", JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE);

    return ((result == 0) ? true : false);
  }
}
