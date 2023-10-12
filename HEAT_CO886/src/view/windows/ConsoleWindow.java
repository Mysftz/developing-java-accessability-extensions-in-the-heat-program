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

package view.windows;

import managers.ActionManager;
import managers.InterpreterManager;
import managers.SettingsManager;
import managers.WindowManager;

import java.util.logging.Logger;

import utils.InterpreterParser;
import utils.Settings;
import utils.parser.ParsedTest;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;


 /**
  * Window that displays the output from Interpreter
  *
  */
public class ConsoleWindow {
  private static Logger log = Logger.getLogger("heat");
  private SettingsManager sm = SettingsManager.getInstance();
  private InterpreterManager im = InterpreterManager.getInstance();
  private WindowManager wm = WindowManager.getInstance();
  private JScrollPane jspMain = new JScrollPane();
  /*Object for JTextpane*/
  private JTextPane jtaInterpreterOutput = new JTextPane();
  private Document jtaIODoc = jtaInterpreterOutput.getDocument();
  private SimpleAttributeSet errorText = new SimpleAttributeSet();
  private SimpleAttributeSet infoText = new SimpleAttributeSet();
  private SimpleAttributeSet normalText = new SimpleAttributeSet();
  private SimpleAttributeSet inputText = new SimpleAttributeSet();
  private SimpleAttributeSet promptText = new SimpleAttributeSet();
  
  private Font displayFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);
  
  private boolean commandEditing = true;
  private boolean withinPrompt = false;
  private boolean testing = false;
  private int currentTest = 0;
  private StringBuilder buffer = new StringBuilder();
  private int fixedContentEnd = 0;
  private boolean fixedProtected = true;
  private java.util.ArrayList<String> history = new java.util.ArrayList<String>();
  private java.util.ListIterator<String> historyIterator = history.listIterator();
  private int currentLineStart = 0;
  private boolean error = false;
  private int errorCount = 0;  // interpreter may produce more than one error message
  private boolean compiling = false;  // currently compiling
  private boolean interrupted = false;  // was interrupted; ignore interpreter header until prompt
  private boolean enabled = true;  // console window can currently be used
  private boolean hideCommand = false;  // ignore result of current command

  
  /**
   * Creates a new ConsoleWindow object.
   * 
   */
  public ConsoleWindow() {
    try {
      jbInit();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    jspMain.setMinimumSize(new Dimension(0, 0));
    jspMain.setPreferredSize(new Dimension(100,150));
    jspMain.setAutoscrolls(true);
    StyleConstants.setForeground(normalText, Color.BLACK);
    StyleConstants.setForeground(errorText,Color.RED);
    StyleConstants.setForeground(infoText,Color.BLUE);
    StyleConstants.setForeground(inputText,Color.DARK_GRAY);
    StyleConstants.setForeground(promptText,new Color(0,150,0));
    StyleConstants.setBold(inputText,true);
    jtaInterpreterOutput.setEditable(true);
    /*Adding action map to the jtaInterpreterOutput*/
    jtaInterpreterOutput.getInputMap(JComponent.WHEN_FOCUSED)
    	.put(KeyStroke.getKeyStroke("ENTER"), "Evaluate");
    jtaInterpreterOutput.getActionMap().put("Evaluate", ActionManager
    		.getInstance().getSendEvaluationAction());
    jtaInterpreterOutput.getInputMap(JComponent.WHEN_FOCUSED)
	  	.put(KeyStroke.getKeyStroke("UP"), "pressed Up");
    jtaInterpreterOutput.getActionMap().put("pressed Up",ActionManager
    		.getInstance().getGoToPastConsoleHistory());
    jtaInterpreterOutput.getInputMap(JComponent.WHEN_FOCUSED)
	   	.put(KeyStroke.getKeyStroke("DOWN"), "pressed Down");
  jtaInterpreterOutput.getActionMap().put("pressed Down",ActionManager
		  	.getInstance().getGoToRecentConsoleHistory());
    
    /* Use font size from settings if it exists */
    String fontSize = sm.getSetting(Settings.OUTPUT_FONT_SIZE);

    if ((fontSize != null) && (fontSize != "")) {
      try {
        int size = Integer.parseInt(fontSize);
        displayFont = new Font(Font.MONOSPACED, Font.PLAIN, size);
      } catch (NumberFormatException nfe) {
        log.warning("[ConsoleWindow] - Failed to parse " +
          Settings.OUTPUT_FONT_SIZE + " setting, check value in settings file");
      }
    }

    jtaInterpreterOutput.setFont(displayFont);
    
    /* This document filter ensures that the fixed content of the console, 
     * i.e. the initial content up to fixedContentEnd, cannot be modified.
     */
    class DocumentFixedFilter extends javax.swing.text.DocumentFilter {
    	private ConsoleWindow console;
    	
    	public DocumentFixedFilter(ConsoleWindow c) {
    		console = c;
    	}
    	
    	public void insertString(FilterBypass fb, int offs, String str, javax.swing.text.AttributeSet a)
    		throws BadLocationException {
    	if (offs >= console.fixedContentEnd || !console.fixedProtected)
    		super.insertString(fb, offs, str, a);
        else
        	java.awt.Toolkit.getDefaultToolkit().beep();
    	}
    	
    	public void replace(FilterBypass fb, int offs, int length, String str, javax.swing.text.AttributeSet a)
    		throws BadLocationException {
        if (offs >= console.fixedContentEnd || !console.fixedProtected)
        	super.replace(fb, offs, length, str, a);
    	else
    		java.awt.Toolkit.getDefaultToolkit().beep();
    	}
    	
    	public void remove(FilterBypass fb, int offs, int length)
			throws BadLocationException {
        if (offs >= console.fixedContentEnd || !console.fixedProtected)
        	super.remove(fb, offs, length);
        else if (offs+length >= console.fixedContentEnd)
        	super.remove(fb, console.fixedContentEnd, length - (console.fixedContentEnd-offs));
        else
        	java.awt.Toolkit.getDefaultToolkit().beep();
    	}
    }
    
    if (jtaIODoc instanceof javax.swing.text.AbstractDocument) {
        javax.swing.text.AbstractDocument doc = (javax.swing.text.AbstractDocument)jtaIODoc;
        doc.setDocumentFilter(new DocumentFixedFilter(this));
    } 
    
    /* Use a CaretListener to ensure that the caret can only appear after the fixed content.
     * Use a listener instead of a filter, so that still any text (including fixed) can be marked for 
     * copying.
     */
    class ConsoleCaretListener implements javax.swing.event.CaretListener {
    	public void caretUpdate(javax.swing.event.CaretEvent e) {
    		if (fixedProtected && e.getDot() < fixedContentEnd)
    			setCaretToEnd();
    	}
    	
    }
    
    jtaInterpreterOutput.addCaretListener(new ConsoleCaretListener());
    
    jtaInterpreterOutput.addKeyListener(new KeyAdapter(){
		public void keyTyped(KeyEvent e) { 
			getKeyEvent(e);
		} 
    });
   
    jspMain.getViewport().add(jtaInterpreterOutput, null);
  }
  
  /* Set the command in the console; given string shall not contain \n 
   * assumes commandEditing = True
   */
  private void setCommand(String command) {
	  clearCommand();
	  for (int i = 0; i < command.length(); i++) {
		  outputInput(command.charAt(i),false);
	  }
  }
  
  public void commandHistoryBackwards() {
	  if (commandEditing) {
		  if (historyIterator.hasPrevious()) 
			  setCommand(historyIterator.previous());
		  else
			  java.awt.Toolkit.getDefaultToolkit().beep();
	  } else 
		  log.warning("Try to go backwards in command history when not editing command.");
  }
  
  /* Note that historyIterator always points too backwards in list, have to skip one element.
   * That is because a ListIterator points between elements, not at an element as needed here.
   */
  public void commandHistoryForwards() {
	  if (commandEditing) {
		  if (historyIterator.hasNext()) {
			  historyIterator.next();
			  if (historyIterator.hasNext()) {
				  setCommand(historyIterator.next());
				  historyIterator.previous();
			  } else
				  setCommand("");
		  } else
			  java.awt.Toolkit.getDefaultToolkit().beep();
	  } else 
		  log.warning("Try to go forwards in command history when not editing command.");  
  }
  
  /* Assumes that command contains no special character such as \n 
   * 
   */
  public void commandHistoryAdd(String command) {
	  history.add(command);
      historyIterator = history.listIterator(history.size());  
  }
  

  /**
   * Returns the selected text, if any
   *
   * @return selected text
   */
  public String getSelectedText() {
    return jtaInterpreterOutput.getSelectedText();
  }

  /**
   * Sets the font size
   *
   * @param ptSize desired font size
   */
  public void setFontSize(int ptSize) {
    jtaInterpreterOutput.setFont(new Font(Font.MONOSPACED, Font.PLAIN, ptSize));
    jtaInterpreterOutput.repaint();
  }

  /**
   * Copies selected text
   */
  public void copy() {
    jtaInterpreterOutput.copy();
  }

  /**
   * Returns the main scrollpane, needed to assemble whole window
   *
   * @return the main window scrollpane
   */
  public JScrollPane getWindowPanel() {
    return jspMain;
  }
  
  /**
   * Get command string currently in console command; i.e. all text after last prompt.
   * @return command string
   */
  public String getCommand() {
	  String command = "";
	  if (commandEditing)
		  try {
		  	command = jtaIODoc.getText(fixedContentEnd,jtaIODoc.getLength()-fixedContentEnd);
		  } catch (BadLocationException e) {
			  e.printStackTrace();
		  }
	  else 
		  command = ""; // so just send the \n character
	  return command;
  }
  
  /* turn protection of fixed console content on/off */
  public void fixed(boolean on) {
	  fixedProtected = on;
  }
 
  public void setCaretToEnd() {
	  jtaInterpreterOutput.setCaretPosition(jtaIODoc.getLength());
  }
  
  /**
   * Add character to end of console output, doing syntax-highlighting of possible error message
   * @param c	the added character
   * @param attr
   */
  public void outputAdd(char c, SimpleAttributeSet attr) {
	  try {
		  // if (error) 
		//	  attr = errorText; 
                  if (c == '\b') {
                      fixed(false);
                      jtaIODoc.remove(jtaIODoc.getLength()-1,1);
                      fixed(true);
                      return;
                  }
		  jtaIODoc.insertString(jtaIODoc.getLength(),Character.toString(c),normalText);
		  if (c == '\n') {
			  String line = jtaIODoc.getText(currentLineStart,jtaIODoc.getLength()-currentLineStart);
                          if (im.checkForError(line,errorCount==0)) {
                              error = true;
                              errorCount+=1;
                          } else if (!im.checkForErrorContinuation(line)) {
                              error = false;
                          }
                          if (error) {
				  fixed(false);
				  jtaIODoc.remove(currentLineStart,jtaIODoc.getLength()-currentLineStart);
                                  if (errorCount==1) {
                                    jtaIODoc.insertString(currentLineStart,line,errorText);
                                  }
				  fixed(true);
			  }
			  currentLineStart = jtaIODoc.getLength();
                          // log.info("[ConsoleWindow] currentLineStart " + currentLineStart);
		  }
	  } catch (BadLocationException e) {
		  e.printStackTrace();
	  }
  }
  
  public void simpleOutputAdd(char c, SimpleAttributeSet attr) {
	  try {
		  jtaIODoc.insertString(jtaIODoc.getLength(),Character.toString(c),attr);
		  if (c == '\n') {			  
			  currentLineStart = jtaIODoc.getLength();
		  }
	  } catch (BadLocationException e) {
		  e.printStackTrace();
	  }
  }
  
  public void outputError(String txt) {
	  for (int i = 0; i < txt.length();i++) {
		  simpleOutputAdd(txt.charAt(i),errorText);
	  }
	  fixedContentEnd = jtaIODoc.getLength();
	  setCaretToEnd();
  }
  
  public void outputInfo(String txt) {
	  for (int i = 0; i < txt.length();i++) {
		  simpleOutputAdd(txt.charAt(i),infoText);
	  }
	  fixedContentEnd = jtaIODoc.getLength();
	  setCaretToEnd();
  }	
	  
  public void outputNormal(char c) {
 	  outputAdd(c,normalText);
	  fixedContentEnd = jtaIODoc.getLength();
	  setCaretToEnd();
  }	
  
  public void outputInput(char c, boolean fixed) {
	  simpleOutputAdd(c,inputText);
	  if (fixed)
	    fixedContentEnd = jtaIODoc.getLength();
	  setCaretToEnd();
  }	
  
  public void outputPrompt(char c) {
	  simpleOutputAdd(c,promptText);
	  fixedContentEnd = jtaIODoc.getLength();
	  setCaretToEnd();
  }	
  
  public void interrupt() {
	  log.warning("Interrupt interpreter.");
	  im.stopInterpreter();
	  outputError("{Interrupted!}\n");
	  testing = false;
	  commandEditing = true;
	  compiling = false;
	  withinPrompt = false;
	  interrupted = true;
          error = false;
          errorCount = 0;
	  im.restartInterpreter();
  }
  
  public void restart() {
	  im.stopInterpreter();
	  outputInfo("(Restart)\n");
	  testing = false;
	  commandEditing = true;
	  compiling = false;
	  withinPrompt = false;
	  interrupted = true;
          error = false;
          errorCount = 0;
	  im.restartInterpreter();	  
  }
  
  /**
   *  
   *
   *  @param ParsedTest test Test to run
   */
  public void runTest(ParsedTest test) {
      im.send(sm.getSetting(Settings.TEST_FUNCTION) + ' ' + test.getName()+"\n");
      log.warning("Test sent " + sm.getSetting(Settings.TEST_FUNCTION) + ' ' + test.getName());
  }


  public void error(String msg) {
	  outputError(msg+"\n");
	  evalCommand("");
  }
  
  
  public void commandEditing(boolean on) {
	  commandEditing = on;
  }
  
  private void getKeyEvent(KeyEvent e) {
	  char c = e.getKeyChar();
	  if (Character.isISOControl(c)) {
		  e.consume();
	  } else if (!enabled) {
		  java.awt.Toolkit.getDefaultToolkit().beep();
	  } else if (!commandEditing) {
		  outputInput(c,true);
		  e.consume();
		  if (c != KeyEvent.CHAR_UNDEFINED) 
			  im.send(Character.toString(c));
	  }
  } 
  
  public void charFromInterpreter(char c) {
            if (hideCommand) {
                if (im.isEndOfPrompt(c)) {
                    hideCommand = false;
                }
            } else if (interrupted) {
	    	if (im.isStartOfPrompt(c)) {
	    		interrupted = false;
	    		withinPrompt = true;
	    	}
	    } else if (testing) {
	    	if (im.isStartOfPrompt(c)) {
	    		// record test result
	    		log.warning("[ConsoleWindow] testing: record test result " + currentTest);
	    		utils.parser.ParsedTest test = (utils.parser.ParsedTest) managers.ParserManager.getParser().getTests().get(currentTest);
	    		if (buffer.indexOf(sm.getSetting(Settings.TEST_POSITIVE)) != -1) {
	    			test.setState("testPassed");
	    		} else {
	    			test.setState("testFailed");	    			
	    		}
	    		wm.getTreeWindow().repaintProperties();
	    	} else if (im.isEndOfPrompt(c)) {
	    		// move to next test or finish testing
	    		log.warning("[ConsoleWindow] testing: move to next or finish");
	    		buffer = new StringBuilder();
	    		currentTest++;
	    		if (currentTest >= managers.ParserManager.getParser().getTests().size()) {
                            testing = false;
		    	    wm.restoreStatus();
	    		}
	    	} else {
	    		if (c == '\b') {
                            buffer.deleteCharAt(buffer.length()-1);
                        } else { 
                            buffer.append(c);
                        }
	    	}
	    } else if (im.isStartOfPrompt(c)) {
		withinPrompt = true;
                // Remove anything before the prompt in the current line
                // Needed at interpreter startup when prompt is changed but 
                // old prompt still appears.
                try {
                    fixed(false);
                    jtaIODoc.remove(currentLineStart,jtaIODoc.getLength()-currentLineStart); 
                    fixedContentEnd = jtaIODoc.getLength();
                    fixed(true);
                } catch (BadLocationException e) {
                    log.warning("[ConsoleWindow]: Could not remove text before prompt.");
                    e.printStackTrace();
                }
            } else if (im.isEndOfPrompt(c)) {
	    	withinPrompt = false;
	    	outputInput(' ', true);
	    	commandEditing(true);
                if (compiling) {
                    if (errorCount>0) {
                        wm.setStatusCompiledError();
                    } else {
                        wm.setStatusCompiledCorrect();
                        getFocus();
                    }
                } else {
                    wm.restoreStatus();
                }
	    	compiling = false;
	    	error = false;
                errorCount = 0;
	    } else if (withinPrompt) {
	    	outputPrompt(c);
	    } else {
	    	outputNormal(c);
	    }
	  }
	  
/**
 * Prepare for receiving test results.
 * Assumes state CompiledCorrect.
 *
 */
  public void readyToReceiveTestResults() {
 	  testing = true;
	  currentTest = 0;
	  wm.setStatusEvaluating();
  }
  
  public void evalCommand(String cmd) {
	  if (im.isDisabledCommand(cmd)) {
		  error("This command has been disabled.");
	  } else {
		  commandEditing = false;
		  commandHistoryAdd(cmd);
		  im.send(cmd+"\n");
	  }
  }
    
  /**
   * 
   * assumes interpreter is currently not evaluating anything else
   */
  public void compile() {
	  if (commandEditing) {
                  hideCommand = true; // hide output of :cd
		  compiling = true;
		  clearCommand();
		  outputInfo("(compiling)\n");
		  im.compile();
	  } else 
		  log.warning("Tried to compile when interpreter was not ready.");
  }
  
  public void unload() {
	  clearCommand();
	  outputInfo("(Forgetting current program)\n");
	  im.unload();
  }
  
  /**
   * remove any partial command 
   */
  public void clearCommand() {
	  try {
		  jtaIODoc.remove(fixedContentEnd,jtaIODoc.getLength()-fixedContentEnd);
	  } catch (BadLocationException e) {
		  e.printStackTrace();
	  }
	  fixedContentEnd = jtaIODoc.getLength();
	  setCaretToEnd();
  }
  
   /**
   * The output window gets the focus.
   * 
   */
  public void getFocus(){
	jtaInterpreterOutput.requestFocus(); 
  }
  
  public JTextPane getTextArea() {
	  return jtaInterpreterOutput;
  }
  
  /**
   * Changes the editability of the output console
   * 
   * @param b Boolean which regulates the editability of the console
   */
  public void setEnabled(boolean b) {
	// do not disable GUI component, because that would make console output close to invisible,
	// just disable functionality
	enabled = b;
	jtaInterpreterOutput.setEditable(b);
  }
  
  public boolean isEnabled() {
	  return enabled;
  }
}
  
