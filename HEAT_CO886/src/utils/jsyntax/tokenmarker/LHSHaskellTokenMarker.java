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
 * @author Slava Pestov (JEdit Syntax)
 *
 */
package utils.jsyntax.tokenmarker;

import utils.jsyntax.*;

import javax.swing.text.Segment;


/**
 * Haskell token marker.
 *
 * @author Chris Olive (Slava Pestov)
 */
public class LHSHaskellTokenMarker extends TokenMarker {
  // private members
  private static KeywordMap cKeywords;
  private KeywordMap keywords;
  private int lastOffset;
  private int lastKeyword;

  public LHSHaskellTokenMarker() {
    this(getKeywords());
  }

  public LHSHaskellTokenMarker(KeywordMap keywords) {
    this.keywords = keywords;
  }

  public byte markTokensImpl(byte token, Segment line, int lineIndex) {
    char[] array = line.array;
    int offset = line.offset;
    lastOffset = offset;
    lastKeyword = offset;

    int length = line.count + offset;
    boolean backslash = false;

loop: 
    for (int i = offset; i < length; i++) {
      int i1 = (i + 1);

      char c = array[i];

      if (c == '\\') {
        backslash = !backslash;

        continue;
      }

      switch (token) {
      case Token.NULL:

        switch (c) {
        case '>':
          addToken(i - lastOffset, token);
          addToken(length - i, Token.OPERATOR);
          lastOffset = lastKeyword = length;

          break loop;

        default:
          backslash = false;

          if (!Character.isLetterOrDigit(c) && (c != '_'))
            doKeyword(line, i, c);

          break;
        }

        break;

      case Token.COMMENT1:
      case Token.COMMENT2:
        backslash = false;

        if ((c == '-') && ((length - i) > 1)) {
          if (array[i1] == '}') {
            i++;
            addToken((i + 1) - lastOffset, token);
            token = Token.NULL;
            lastOffset = lastKeyword = i + 1;
          }
        }

        break;

      case Token.LITERAL1:

        if (backslash)
          backslash = false;
        else if (c == '"') {
          addToken(i1 - lastOffset, token);
          token = Token.NULL;
          lastOffset = lastKeyword = i1;
        }

        break;

      case Token.LITERAL2:

        if (backslash)
          backslash = false;
        else if (c == '\'') {
          addToken(i1 - lastOffset, Token.LITERAL1);
          token = Token.NULL;
          lastOffset = lastKeyword = i1;
        }

        break;

      default:
        throw new InternalError("Invalid state: " + token);
      }
    }

    if (token == Token.NULL)
      doKeyword(line, length, '\0');

    switch (token) {
    case Token.LITERAL1:
    case Token.LITERAL2:
      addToken(length - lastOffset, Token.INVALID);
      token = Token.NULL;

      break;

    case Token.KEYWORD2:
      addToken(length - lastOffset, token);

      if (!backslash)
        token = Token.NULL;

    default:
      addToken(length - lastOffset, Token.COMMENT1);

      break;
    }

    return token;
  }

  /**
   * Main keyword list. Add more here as necessary
   * @return All current keywords
   */
  public static KeywordMap getKeywords() {
    if (cKeywords == null)
      cKeywords = new KeywordMap(false);

    return cKeywords;
  }

  private boolean doKeyword(Segment line, int i, char c) {
    int i1 = i + 1;

    int len = i - lastKeyword;
    byte id = keywords.lookup(line, lastKeyword, len);

    if (id != Token.NULL) {
      if (lastKeyword != lastOffset)
        addToken(lastKeyword - lastOffset, Token.NULL);

      addToken(len, id);
      lastOffset = i;
    }

    lastKeyword = i1;

    return false;
  }
}
