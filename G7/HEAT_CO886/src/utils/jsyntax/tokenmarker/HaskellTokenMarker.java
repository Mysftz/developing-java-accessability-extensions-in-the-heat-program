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
 * @author Olaf Chitil
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
 * @author Olaf Chitil, Chris Olive (Slava Pestov)
 */

public class HaskellTokenMarker extends TokenMarker {

  static private KeywordMap keywords = getKeywords();

  /**
   * no token goes over several lines
   */
  public boolean supportsMultilineTokens() {
    return false;
  }


  static boolean isSymbol(char c) {
    switch (c) {
    case '!':
    case '#':
    case '$':
    case '%':
    case '&':
    case '*':
    case '+':
    case '.':  
    case '/':
    case '<':
    case '=':
    case '>':
    case '?':
    case '@':
    case '\\':
    case '^':
    case '|':
    case '-':
    case '~':
      return true;
    default:
      return false;
    }
  }


  public byte markToken(byte token, Segment line) {

    int start = line.getIndex();

    switch(token) {
    case Token.NULL:
      char c = line.current();
      if (Character.isUpperCase(c)) {
	c = line.next();
        while (Character.isLetterOrDigit(c) || c == '\'') {
          c = line.next();
        }
        addToken(line.getIndex()-start,Token.KEYWORD2);
        return Token.NULL;
      }
      if (Character.isLowerCase(c)) {
        c = line.next();
        while (Character.isLetterOrDigit(c) || c == '\'') {
          c = line.next();
        }
        addToken(line.getIndex()-start,
		 keywords.lookup(line,start,line.getIndex()-start));
        return Token.NULL;
      } 
      if (Character.isDigit(c)) {  /* start recognising a number */
	if (c=='0') {
	  c = line.next();  
          if (c=='x' || c=='X') {
            c = line.next();
            while (Character.digit(c,16)!=-1) {
	      c = line.next();
            }
            addToken(line.getIndex()-start,Token.LITERAL1);
            return Token.NULL;
          }
	  if (c=='o' || c=='O') {
            c = line.next();
            while (Character.digit(c,8)!=-1) {
	      c = line.next();
            }
            addToken(line.getIndex()-start,Token.LITERAL1);
            return Token.NULL;
	  } 
        } else {
	  c = line.next();
	}
        while (Character.isDigit(c)) {
	  c = line.next();
	}
        if (c=='.') {
	  c = line.next();
          if (!Character.isDigit(c)) {
	    line.previous(); /* up to . is a number */
	    addToken(line.getIndex()-start,Token.LITERAL1);
            return Token.NULL;
          }
          while (Character.isDigit(c)) {
	    c = line.next();
          }
          if (c=='e' || c=='E') {
	    c = line.next();
            if (c=='+' || c=='-') {
	      c = line.next();
	    }
            if (!Character.isDigit(c)) {
	      addToken(line.getIndex()-start,Token.INVALID);
              return Token.NULL;
            }
            while (Character.isDigit(c)) {
	      c = line.next();
            }
	  }
        }
	addToken(line.getIndex()-start,Token.LITERAL1);
	return Token.NULL;
      }
      if (isSymbol(c)) {  /* var symbol */
	c = line.next();
        while (isSymbol(c) || c == ':') {
	    c = line.next();
	}
        if (line.getIndex()-start == 2 && 
	    line.array[start] == '-' && line.array[start+1] == '-') {
	  /* start of line comment */
	  line.last();
	  line.next();
	  addToken(line.getIndex()-start,Token.COMMENT1);
	  return Token.NULL;
	}
        addToken(line.getIndex()-start,
		 keywords.lookup(line,start,line.getIndex()-start));
        return Token.NULL;
      }
      switch (c) {
      case '{':
	c = line.next();
        if (c == '-') {  /* start of multi-line comment */
	  char co = line.next();
          c = line.next();
          while (! ((co == '-' && c == '}') || c == Segment.DONE)) {
	    co = c;
	    c = line.next();
	  }
          line.next();
          addToken(line.getIndex()-start,Token.COMMENT2);
	  if (c == Segment.DONE) {
	    return Token.COMMENT2; /* still within comment at end-of-line */
	  } else {
	    return Token.NULL;
	  }
        } else {
	  addToken(line.getIndex()-start,Token.NULL); 
	  return Token.NULL;  
        }
      case ':': /* constructor symbol */
	c = line.next();
        while (isSymbol(c) || c == ':') {
	    c = line.next();
	}
        byte tok = keywords.lookup(line,start,line.getIndex()-start);
        addToken(line.getIndex()-start,
		 tok == Token.NULL ? Token.KEYWORD2 : tok);
	return Token.NULL;
      case '\'': /* character literal; allows too much */
	c = line.next();
        while (c != '\'' && c!=Segment.DONE) {
	    if (c == '\\') {
	      c = line.next();
	    }
	    c = line.next();
	}
	line.next(); /* goto next character after literal */
	addToken(line.getIndex()-start,Token.LITERAL1);
	return Token.NULL;
      case '"': /* string literal; does not cover multi-line literals */
        c = line.next();
        while (c != '"' && c!=Segment.DONE) {
	    if (c == '\\') {
	      c = line.next();
	    }
	    c = line.next();
	}
	line.next(); /* goto next character after literal */
	addToken(line.getIndex()-start,Token.LITERAL1);
	return Token.NULL;
      case '[':  /* consider [ as constructor symbol */
        line.next();
	addToken(line.getIndex()-start,Token.KEYWORD2); 
	return Token.NULL;
      case ']':  /* consider ] as constructor symbol */
        line.next();
	addToken(line.getIndex()-start,Token.KEYWORD2); 
	return Token.NULL;
      }
      /* default: just accept the character */
      line.next();
      addToken(line.getIndex()-start,Token.NULL); 
      return Token.NULL;

    case Token.COMMENT2:  /* does not handle nested comments */
      char co = line.current();
      c = line.next();
      while (! ((co == '-' && c == '}') || c == Segment.DONE)) {
	co = c;
	c = line.next();
      }
      line.next();
      addToken(line.getIndex()-start,Token.COMMENT2);
      if (c == Segment.DONE) {
	return Token.COMMENT2; /* still within comment at end-of-line */
      } else {
	return Token.NULL;
      }
    default:
	throw new InternalError ("Token marker in invalid state: " + token);
    }
  }



  /**
   * Identify and mark all tokens in the given line
   */ 
  public byte markTokensImpl(byte token, Segment line, int lineIndex) {

    line.first();
    while (line.current() != Segment.DONE) {
	token = markToken(token,line);
    }
    line.first();
    return token;
  }
  
  /**
   * Main keyword list. Add more here as necessary
   * @return All current keywords
   */
  public static KeywordMap getKeywords() {

    KeywordMap cKeywords = new KeywordMap(false);

    // Keywords
    cKeywords.add("module", Token.KEYWORD1);
    cKeywords.add("import", Token.KEYWORD1);
    cKeywords.add("as", Token.KEYWORD1);
    cKeywords.add("qualified", Token.KEYWORD1);
    cKeywords.add("hiding", Token.KEYWORD1);
    cKeywords.add("infix", Token.KEYWORD1);
    cKeywords.add("infixl", Token.KEYWORD1);
    cKeywords.add("infixr", Token.KEYWORD1);
    cKeywords.add("class", Token.KEYWORD1);
    cKeywords.add("data", Token.KEYWORD1);
    cKeywords.add("deriving", Token.KEYWORD1);
    cKeywords.add("instance", Token.KEYWORD1);
    cKeywords.add("default", Token.KEYWORD1);
    cKeywords.add("where", Token.KEYWORD1);
    cKeywords.add("type", Token.KEYWORD1);
    cKeywords.add("newtype", Token.KEYWORD1);
    cKeywords.add("do", Token.KEYWORD1);
    cKeywords.add("case", Token.KEYWORD1);
    cKeywords.add("of", Token.KEYWORD1);
    cKeywords.add("let", Token.KEYWORD1);
    cKeywords.add("in", Token.KEYWORD1);
    cKeywords.add("if", Token.KEYWORD1);
    cKeywords.add("then", Token.KEYWORD1);
    cKeywords.add("else", Token.KEYWORD1);
    cKeywords.add("_", Token.KEYWORD1);

    cKeywords.add("..", Token.KEYWORD3);
    cKeywords.add("::", Token.KEYWORD3);
    cKeywords.add("=", Token.KEYWORD3);
    cKeywords.add("\\", Token.KEYWORD3);
    cKeywords.add("|", Token.KEYWORD3);
    cKeywords.add("->", Token.KEYWORD3);
    cKeywords.add("<-", Token.KEYWORD3);
    cKeywords.add("@", Token.KEYWORD3);
    cKeywords.add("~", Token.KEYWORD3);
    cKeywords.add("=>", Token.KEYWORD3);

    return cKeywords;
  }

}
