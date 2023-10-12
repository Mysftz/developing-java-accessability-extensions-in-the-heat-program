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

package view.dialogs;

import managers.WindowManager;

import java.io.File;

import java.util.*;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;


/**
 * File dialogs used by HEAT
 */
public class FileDialogs {
  private static FileDialogs instance;
  private WindowManager wm = WindowManager.getInstance();

  protected FileDialogs() {
    /* Prevent instantiation */
  }

  public static FileDialogs getInstance() {
    if (instance == null)
      instance = new FileDialogs();

    return instance;
  }
  
  /**
   * Shows the file chooser dialogs
   * @return
   */
  public File showDefaultFileChooser(File start) {
    JFileChooser jfc = new JFileChooser();
    jfc.setDialogTitle("Select File");
    jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
    jfc.setCurrentDirectory(start);

    int result = jfc.showOpenDialog(wm.getMainScreenFrame());

    if (result == JFileChooser.CANCEL_OPTION)
      return null;
    else if (result == JFileChooser.APPROVE_OPTION)
      return jfc.getSelectedFile();
    else {
      JOptionPane.showMessageDialog(null, "Error selecting file!",
        "File Selection Error", JOptionPane.ERROR_MESSAGE);

      return null;
    }
  }
  
  /**
   * Directory chooser for choosing paths (eg for the user directory)
   * @return null if invalid choice
   */
  public File showDefaultDirChooser(File start) {
    JFileChooser jfc = new JFileChooser();
    jfc.setDialogTitle("Select Directory");
    jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    jfc.setCurrentDirectory(start);

    int result = jfc.showOpenDialog(wm.getMainScreenFrame());

    if (result == JFileChooser.CANCEL_OPTION)
      return null;
    else if (result == JFileChooser.APPROVE_OPTION)
      return jfc.getSelectedFile();
    else {
      JOptionPane.showMessageDialog(null, "Error selecting directory!",
        "Directory Selection Error", JOptionPane.ERROR_MESSAGE);

      return null;
    }
  }
}