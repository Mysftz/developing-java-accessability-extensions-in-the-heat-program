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


/**
 * Performs simple search options
 */
public class Search {
  
  public Search() {
  }

  /**
   * Perform a case sensitive search
   * @param searchText The text to find
   * @param text The text to search
   * @param offset The position to start from
   * @return The location at the end of the found item
   */
  public static int searchCaseText(String searchText, String text, int offset) {
    int length = searchText.length();
    int searchLength = text.length();
    char[] letters = searchText.toCharArray();
    int match = 0;
    int j = 0;

    if (searchLength < 1)
      return -1;

    for (int i = offset; i < searchLength; i++) {
      char a = text.charAt(i);
      char b = letters[j];

      if (a == b) {
        match++;
        j++;

        if (match == length)
          return i;
      } else
        match = j = 0;
    }

    return -1;
  }

  /**
   * Perform a non case sensitive search
   * @param searchText The text to find
   * @param text The text to search
   * @param offset The position to start from
   * @return The location at the end of the found item
   */
  public static int searchNoCaseText(String searchText, String text, int offset) {
    String lowerText = text.toLowerCase();
    int length = searchText.length();
    int searchLength = text.length();
    char[] letters = searchText.toLowerCase().toCharArray();
    int match = 0;
    int j = 0;

    if (searchLength < 1)
      return -1;

    for (int i = offset; i < searchLength; i++) {
      char a = lowerText.charAt(i);
      char b = letters[j];

      if (a == b) {
        match++;
        j++;

        if (match == length)
          return i;
      } else
        match = j = 0;
    }

    return -1;
  }
}
