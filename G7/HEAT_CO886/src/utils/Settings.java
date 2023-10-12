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

/**
 * Contains the static identifiers for the SettingsManager
 */
public class Settings {

  /**
   * The location of the desired Haskell interpreter 
   */
  public static final String INTERPRETER_PATH = "GHCI_PATH";
  
  /**
   * Command line options for the Haskell interpreter
   */
  
  public static final String INTERPRETER_OPTS = "GHCI_OPTS";

  /**
   * The location used to store temporary files
   */
  public static final String LIBRARY_PATH = "LIBRARY_PATH";

  /**
   * The font size used in the output window
   */
  public static final String OUTPUT_FONT_SIZE = "OUTPUT_FONT_SIZE";

  /**
   * The font size used in the display window
   */
  public static final String CODE_FONT_SIZE = "CODE_FONT_SIZE";

  /*
   * For Boolean unit tests or QuickCheck
   */
  public static final String TEST_FUNCTION = "TEST_FUNCTION";
  
  public static final String TEST_POSITIVE = "TEST_POSITIVE";
  
}
