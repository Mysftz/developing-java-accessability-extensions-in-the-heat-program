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

package utils;

import java.io.*;

import javax.swing.*;

/**
 * File filter for Haskell files (only .hs)
 */
public class HaskellFilter extends javax.swing.filechooser.FileFilter {
  public boolean accept(File f) {
    return f.getName().toLowerCase().endsWith(".hs") || f.isDirectory();
  }

  public String getDescription() {
    return "Haskell files (*.hs)";
  }
}