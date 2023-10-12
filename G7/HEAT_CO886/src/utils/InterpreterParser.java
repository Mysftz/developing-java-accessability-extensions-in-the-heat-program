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

import managers.InterpreterManager;
import managers.FileManager;
import managers.ParserManager;
import managers.SettingsManager;
import managers.WindowManager;

// import org.jdom.*;
// import org.jdom.input.SAXBuilder;
import java.util.logging.Logger;

import java.io.File;
import java.io.IOException;

import java.lang.*;

import java.util.*;

import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;

import utils.parser.ParsedTest;

/**
 * Takes output from Hugs and checks it for error
 * messages. Will NOT function properly on other interpreter output.
 */
public class InterpreterParser {
  SettingsManager sm = SettingsManager.getInstance();
  private static InterpreterParser instance = null;
  private final String HUGS_ERROR = "ERROR";
  private final String HUGS_ERROR_EXPLANATION = "***";
  private final String OK = "Type :?";
  private String ERROR_SEPERATOR = "-";
  private String LINENUMBER_SPLIT = ":";
  private String errorDescription;
  private String bufferedLine = "";
    // private org.jdom.Document doc;
  private boolean xmlChecked = false;
  private boolean xmlBroken = false;
  private boolean match = false;
  private int errorFound = 0;
  private String propEval="";
  private String line = "";
  private int charsRead=0;
  private boolean errorNo;
  private int charCount = 0;
  
  private String lineNumber = "";
  private int lineNum;
  private WindowManager wm = WindowManager.getInstance();
  private InterpreterManager im = InterpreterManager.getInstance();
  private static Logger log = Logger.getLogger("heat");
  // to provide way to interpret test output
  private String currentDebugEval = ""; 
  protected InterpreterParser() {
    /* Exists to prevent instantiation */
  }
  
  /**
   * Get an instance of an InterpreterParser
   * @return The current or a new instance if one is not running
   */
  public static InterpreterParser getInstance() {
    if (instance == null)
      instance = new InterpreterParser();
    return instance;
  }
  
    /**
     * Checks the output with expected results for the properties. 
     * 
     * @param String output The array of strings with containing the results of the properties.
     * @param int How many results have been collected.
     */
   public void parseTestResults(String []output, int countResult){
	    ArrayList tests=ParserManager.getInstance().getParser().getTests();
	    for (int i=0;i<countResult;i++){
	      if (tests.size()>0){
            String myResult = output[i].trim();
            if (!myResult.contains("ERROR") 
                && !myResult.contains("Program error") 
                && myResult.contains("True"))
            {
            /*TutorialWindow.changeText("<h3>TEST RESULT: <span style=\"color: green\">PASSED</span></h3>Test: <b>" + currentTest.getName() + "</b><br>Expected: <b style=\"color:green\">" + currentTest.getName() + "</b><br>Result: <b style=\"color:green\">" + myResult + "</b>");
            wm.showTutorial();*/
            ((ParsedTest) ParserManager.getInstance().getParser().getTests()
              .get(i)).setState("testPassed");
            
            } else {
            // check if parser produced an error
              if(myResult.startsWith("ERROR") || myResult.startsWith("Program error"))
              {
                myResult = "Syntax error in the test line";
              }
              ((ParsedTest) ParserManager.getInstance().getParser().getTests()
                .get(i)).setState("testFailed");
              /*TutorialWindow.changeText("<h3>TEST RESULT: <span style=\"color: red\">FAILED</span></h3>Test: <b>" + currentTest.getName() + "</b><br>Expected: <b style=\"color:green\">" + currentTest.getName() + "</b><br>Result: <b style=\"color:red\">" + myResult + "</b>");
              wm.showTutorial();*/
            }
          }
	    }
        // parse the rest of the output as normal
    }
  
  public void setCurrentDebugEvaluation(String eval)
  {
      currentDebugEval = eval;
  }
  
  public void setErrorStatus(int status)
  {
      errorFound = status;
  }
  
  /**
   * Main method that parses the output from the interpreter. This checks for 
   * the known error text within the String and if found looks up a definition 
   * for this in the xml error file.
   *
   * Most of this method is now pointless. However, it still finds the
   * error location within the error message.
   * 
   * @param output The String (from the interpreter) to be parsed.
   */
  /*
  public void parseOutput(String output) {
    if ((output == null) || output.trim().equals(""))
      return;
    else if (wm.getEvaluationWindow().isDebugMode()) //thisIsAlongAndAlmostRandomfunctionNAmeE1
    {
        if (output.matches("^(.*)\\s?thisIsAlongAndAlmostRandomfunctionNAmeE(.*)\\s?$"))
            //System.out.println("Found error to replace");
            output = output.replaceFirst("thisIsAlongAndAlmostRandomfunctionNAmeE",currentDebugEval);
    }

    //System linebreak character 
    String lineBreak = System.getProperty("line.separator");
    // Check for error, display relevant graphic in evaluation window 
    if (output.indexOf(HUGS_ERROR) != -1) {
      errorFound = 1;
      wm.getEvaluationWindow().setCompileStatus(0);
      wm.showOutput();
      // Split point containing the error description 
      int split = output.indexOf(ERROR_SEPERATOR);
      // Location of the colon before the linenumber 
      int numberStart = output.lastIndexOf(LINENUMBER_SPLIT, split);
      // Strings containing the linenumber and error description 
      if (numberStart > 0) {
        String lineNumber = output.substring(numberStart + 1, split).trim();
        // Convert String linenumber to int 
        try {
          lineNum = Integer.parseInt(lineNumber);
          wm.getEditorWindow().setLineMark(lineNum);
          wm.getEditorWindow().focusLine(lineNum);
        } catch (Exception e) {
          log.warn("Could not convert error linenumber");
        }
      }
      // Trim down to just error message //
      if (split > 0)
        errorDescription = output.substring(split + 1, output.length()).trim();
      // Check that the XML file has been loaded ok 
      if (!xmlBroken) {
        // get <error>'s from xml 
    	Element root = null;
    	try{
          root = doc.getRootElement();
    	}
    	catch(Exception e){
    		System.out.println("HM");
    	}
        List children = root.getChildren();
        Iterator iterator = children.iterator();
        match = false;

        String htmlOutput = "<html><body><table width=\"100%\" border=\"0\" cellspacing=\"1\" cellpadding=\"1\" STYLE=\"font-family: Arial; font-size: 9.5px; color: #000000;\"><tr><td width=\"98%\" bgcolor=\"#E8E8E8\"><strong>Possible solutions</strong></td><td width=\"2%\"></td></tr><tr><td>";

        while (iterator.hasNext()) {
          Element err = (Element) iterator.next();
          String desc = err.getChild("description").getText();
          // Skip if error does not match 
          if (!errorDescription.startsWith(desc))
            continue;
          // <hr> between errors if > 1 
          if (match)
            htmlOutput += "<hr>";
          // Shows we have found a match 
          match = true;
          // 
          // Gets the various parts of the error (the description, example, 
          // solution) and adds these to the html code being built up
          String longDesc = err.getChild("longDescription").getText();
          longDesc = trimAndSpace(longDesc);
          htmlOutput += ("<b>" + longDesc + "</b><br><br>");
          String example = err.getChild("example").getText();
          example = trimAndSpace(example);
          htmlOutput += ("<font face=\"Courier New, Courier, mono\">" +
          example + "</font><br><br>");
          String solution = err.getChild("solution").getText();
          solution = trimAndSpace(solution);
          htmlOutput += solution;
        }
        if (!match)
          htmlOutput += "No help with this error could be found";
        // Finish off html and display 
        htmlOutput += "</td><td></td></tr></table></body></html>";
      } else {
        // Error loading xml file 
        String errorMessage = "<html><body>An XML file could not be loaded or is incorrectly formatted. Please see the help for more information</body></html>";
      }
    } else if ((output.indexOf(HUGS_ERROR_EXPLANATION) != -1))
      wm.getEvaluationWindow().setCompileStatus(0);
    else {
      // Error fixed 
      wm.getEditorWindow().clearLineMark();
      wm.getEvaluationWindow().setCompileStatus(1);
      errorFound = 0;
    }
    
    InterpreterManager.getInstance().gotOutput();

    // Add line break after module>, but not in type definitions 
    // Makes display window look nicer
    try {
      int index = output.lastIndexOf(">");
      if (index != -1)
        if (output.charAt(index - 2) != ' ')
        {
          //output = (output.substring(0, index + 1) + lineBreak +
          //  output.substring(index + 2, output.length()));
          //get rid of "prelude>" thing, or similar
          //and output result only
          output = output.substring(index + 2, output.length());
        }
    } catch (Exception e) {
      log.warn("Could not format display properly");
    }
    wm.getOutputWindow().outputNormal(output + lineBreak);
  }
  */
  
  /**
   * Parses a character at a time to retrieve the line number.
   * @param letter The input (int) character to parse.
   */
  /*
  public void parseChars(int letter) {
	wm.getEvaluationWindow().setCompileStatus(1);	
    char c;
    c = (char) letter;
    System.out.println("char:" + c + ":");
    bufferedLine+=c;
    String sas = "";
    boolean print = false;
    if (errorNo) {
      if (Character.isDigit(c)) {
        lineNumber += c;
        charCount++;
      } else if (charCount > 0) {
        try {
          lineNum = Integer.parseInt(lineNumber);
          wm.getEvaluationWindow().setCompileStatus(0);
          wm.getEditorWindow().setLineMark(lineNum);
          wm.getEditorWindow().focusLine(lineNum);
          lineNumber = "";
          charCount = 0;
          errorNo = false;
        } catch (Exception e) { }
      }
      else if (c == '?') {
    	System.out.println("Character "+c);
        wm.getEditorWindow().clearLineMark();
        wm.getEvaluationWindow().setCompileStatus(1);
        errorNo = false;
        charCount = 0;
      }
      else {
        errorNo = false;
        charCount = 0;
      }
    }
    if (c == ':' && !errorNo) {
      errorNo = true;  
    }
    //Text highlighting of the console
    line+=c;
    
    if(!im.isProgramRunning()){
      //Text highlight when there is no code running in the interpreter
      charsRead=0;
      if (c=='\n'){
        if (line.contains("ERROR")){
          sas="error";
        }
        else
          if (wm.getEvaluationWindow().getCompileStatus()==0)
        	sas="error";
          else
    	    sas="";
        print = true;
      }
      else if(line.endsWith("\1")){
    	if (line.contains("\n")){
          wm.getOutputWindow().(
        	line.substring(0,line.lastIndexOf("\n")),wm.getOutputWindow().getStyle("")); 
          line=line.substring(line.lastIndexOf("\n"),line.length());
    	}
    	wm.getOutputWindow().appendOutput(
    	  line.substring(0,line.indexOf("\1")),wm.getOutputWindow().getStyle("inputString"));
    	wm.getOutputWindow().appendOutput(
    	  line.substring(line.indexOf("\1")),wm.getOutputWindow().getStyle(""));
    	line="";
      }
    
      if (print){
        wm.getOutputWindow().appendOutput(line,wm.getOutputWindow().getStyle(sas));
        print=false;
        line = "";
  	  }
      
    }
    else {
      // Highlighting when a code is running in the interpreter
      charsRead++;
      if (getErrorStatus()==1 && charsRead>=5){
		wm.getOutputWindow().appendOutput(line,wm.getOutputWindow().getStyle("error"));
    	line="";
      }
      else if(charsRead >= 11){
        wm.getOutputWindow().appendOutput(line,wm.getOutputWindow().getStyle(""));
        line="";
      }
    }
    
  }
*/  

/**
   * Removes the '_' and "NEWLINE" defined in the xml file (for space and 
   * newlines) and replaces them
   * 
   * @param s The String to be formatted
   * @return The formatted String
   */
  public String trimAndSpace(String s) {
    s = s.replaceAll("NEWLINE", "<br>");
    s = s.replaceAll("_", " ");
    return s;
  }
  
  /**
   * Return the error found status
   * @return True if an error is found, false otherwise
   */
  public int getErrorStatus() {
    return errorFound;
  }
  
  /**
   * Gets the line number an error has been reported on
   * @return The line number
   */
  public int getErrorLine() {
    return lineNum;
  }

  /**
   * Checks that the xml error file exists and can be loaded
   */
  /*
  public void checkXML() {
    String sep = System.getProperty("file.separator");
    String xmlName = "lib" + sep + "HUGSerrors.xml";
    File file = new File(xmlName);
    String path = file.getAbsolutePath();
    SAXBuilder builder = new SAXBuilder();
    try {
      if (file.exists()){
    	System.out.println(file.getAbsolutePath()+" exists");
        doc = builder.build(path);
      }
      else {
    	System.out.println(file.getAbsolutePath()+" does not exist");
        path = Resources.getXMLFilePath("HUGSerrors");
        System.out.println("Pathname to HUGSerrors.xml: "+path);
        doc = builder.build(path);
        
            
      }
    } catch (JDOMException e) { // Improperly formatted
      xmlBroken = true;
      log.warn("XML incorrectly formatted");
    } catch (IOException e) { // File not found
      xmlBroken = true;
      log.warn("XML file not found");
    }
  }
   */
  
  public void setErrorNo(boolean b){
	  errorNo = b;
  }
}
