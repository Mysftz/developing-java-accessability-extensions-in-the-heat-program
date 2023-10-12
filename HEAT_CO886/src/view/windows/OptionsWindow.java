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
 * @author Olaf Chitil
 * 
 * Handles the Option Dialog for users to change several settings,
 * in particular information about the Haskell interpreter and font sizes.
 *
 */

package view.windows;

import managers.ActionManager;
import managers.SettingsManager;
import managers.WindowManager;

import utils.Settings;

import view.dialogs.FileDialogs;

import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;


/**
 * Window for altering HEATs settings
 */
public class OptionsWindow {

  private JPanel panelOptions;
  private JTextField jTextFieldInterpreterPath; 
  private JTextField jTextFieldOptions;
  private JTextField jTextFieldLibraryPath;
  private JTextField jTextFieldTestFunction;
  private JTextField jTextFieldTestPositive;
  private JComboBox jcbOutputFontSize;
  private JComboBox jcbCodeFontSize;
  private JDialog dialog;

  private SettingsManager sm = SettingsManager.getInstance();
  private WindowManager wm = WindowManager.getInstance();
  
  
  /**
   * Creates a new OptionsWindow object.
   */
  public OptionsWindow() {
    try {
      jbInit();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  
  private void jbInit() throws Exception {
    
    // panel for interpreter options:
    JPanel panelInterpreter = new JPanel(new GridLayout(0,1));
    JButton browse = new JButton("Browse");
    browse.setToolTipText("Browse for file");
    browse.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          interpreterPath_actionPerformed();
        }
      });
    JPanel panelInterpreterPath = new JPanel();
    panelInterpreterPath.add(new JLabel("Full path of the Haskell interpreter ghci (not ghc or winghci!): "));
    panelInterpreterPath.add(browse);
    panelInterpreter.add(panelInterpreterPath);
    jTextFieldInterpreterPath = new JTextField();
    panelInterpreter.add(jTextFieldInterpreterPath);
    panelInterpreter.add(new JSeparator(SwingConstants.HORIZONTAL));
    // panelInterpreter.add(new JLabel("")); // some vertical space
    JPanel panelOptionsInfo = new JPanel();
    panelOptionsInfo.add(new JLabel("Command line options for the Haskell interpreter:"));
    panelInterpreter.add(panelOptionsInfo);
    jTextFieldOptions = new JTextField();
    panelInterpreter.add(jTextFieldOptions);
    panelInterpreter.add(new JSeparator(SwingConstants.HORIZONTAL));
    // panelInterpreter.add(new JLabel("")); // some vertical space
    JButton browseL = new JButton("Browse");
    browseL.setToolTipText("Browse for directory");
    browseL.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          libraryPath_actionPerformed();
        }
      });
    JPanel panelLibraryPath = new JPanel();
    panelLibraryPath.add(new JLabel("Directory for additional Haskell libraries: "));
    panelLibraryPath.add(browseL);
    panelInterpreter.add(panelLibraryPath);
    jTextFieldLibraryPath = new JTextField();
    panelInterpreter.add(jTextFieldLibraryPath);
    
    // panel for test settings
    JPanel panelTest = new JPanel(new GridLayout(0,1));
    JPanel testFunction = new JPanel(new GridLayout(0,1));
    testFunction.add(new JLabel("Test function applied to a test property"));
    testFunction.add(new JLabel("(e.g. \"quickCheck\" for QuickCheck or \"\" (nothing) for Boolean properties)"));
    jTextFieldTestFunction = new JTextField();
    testFunction.add(jTextFieldTestFunction);
    panelTest.add(testFunction);
    // panelTest.add(new JLabel(""));  // some vertical space
    panelTest.add(new JSeparator(SwingConstants.HORIZONTAL));
    JPanel testPositive = new JPanel(new GridLayout(0,1));
    testPositive.add(new JLabel("String that appears in successful test output"));
    testPositive.add(new JLabel("(e.g. \"+++ OK, passed\" for QuickCheck or \"True\" for Boolean properties)"));
    jTextFieldTestPositive = new JTextField();
    testPositive.add(jTextFieldTestPositive);
    panelTest.add(testPositive);
    
    // panel for setting font sizes
    JPanel panelFontSizes = new JPanel(new GridLayout(0,1));
    jcbOutputFontSize = new JComboBox();
    jcbCodeFontSize = new JComboBox();
 /* Populate the font size combo boxes */
    for (int i = 10; i < 25; i++) {
      jcbOutputFontSize.addItem(String.valueOf(i));
      jcbCodeFontSize.addItem(String.valueOf(i));
    }
    JPanel editorFontSize = new JPanel();
    editorFontSize.add(new JLabel("Editor font size: "));
    editorFontSize.add(jcbCodeFontSize);
    JPanel interpreterFontSize = new JPanel();
    interpreterFontSize.add(new JLabel("Interpreter font size:"));
    interpreterFontSize.add(jcbOutputFontSize);
    panelFontSizes.add(editorFontSize);
    panelFontSizes.add(interpreterFontSize);
    
    // combine panels on tabbed pane
    JTabbedPane tabOptions = new JTabbedPane();
    tabOptions.addTab("Haskell Interpreter", panelInterpreter);
    tabOptions.addTab("Property Tests", panelTest);
    tabOptions.addTab("Font Sizes", panelFontSizes);
    
    // buttons for applying options and cancellation
    JButton buttonApply = new JButton("Apply");
    buttonApply.setAction(ActionManager.getInstance().getSaveOptionsAction());
    JButton buttonCancel = new JButton("Cancel");
    buttonCancel.setToolTipText("Close options dialog without applying any changes");
    buttonCancel.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          close();
        }
      });
    JPanel panelButtons = new JPanel();
    panelButtons.add(buttonApply);
    panelButtons.add(buttonCancel);
    
    // put all together
    panelOptions = new JPanel(new BorderLayout());
    panelOptions.add(tabOptions,BorderLayout.CENTER);
    panelOptions.add(panelButtons,BorderLayout.PAGE_END);
  }

 

  /**
   * Displays the options window
   */
  public void show() {
    getProperties();

    dialog = new JDialog(wm.getMainScreenFrame(), "Options");
    dialog.setModal(true);
    dialog.getContentPane().add(panelOptions);      //(jTabbedPane1);
    dialog.setMinimumSize(new Dimension(500,350));
    dialog.setSize(600, 400);
    dialog.setLocationRelativeTo(wm.getMainScreenFrame());
    dialog.setVisible(true);
  }

  /**
   * Closes the options window
   */
  public void close() {
    if (dialog != null)
      dialog.dispose();
  }

  /**
   * Gets the properties from the properties file, and sets them in the display
   */
  public void getProperties() {
    jTextFieldInterpreterPath.setText(sm.getSetting(Settings.INTERPRETER_PATH));
    jTextFieldOptions.setText(sm.getSetting(Settings.INTERPRETER_OPTS));
    jTextFieldLibraryPath.setText(sm.getSetting(Settings.LIBRARY_PATH));
    jTextFieldTestFunction.setText(sm.getSetting(Settings.TEST_FUNCTION));
    jTextFieldTestPositive.setText(sm.getSetting(Settings.TEST_POSITIVE));
    jcbOutputFontSize.setSelectedItem(sm.getSetting(Settings.OUTPUT_FONT_SIZE));
    jcbCodeFontSize.setSelectedItem(sm.getSetting(Settings.CODE_FONT_SIZE));
  }

 
  /**
   * Returns the current Interpreter path
   *
   * @return the Interpreter path
   */
  public String getInterpreterPath() {
    return jTextFieldInterpreterPath.getText();
  }
  
  /**
   * Returns the Interpreter options
   *
   * @return the Interpreter options
   */
  public String getInterpreterOpts() {
    return jTextFieldOptions.getText();
  }

  /**
   * Returns the location for temporary files
   *
   * @return the location for temporary files
   */
  public String getLibraryPath() {
    return jTextFieldLibraryPath.getText();
  }
  
  public String getTestFunction() {
      return jTextFieldTestFunction.getText();
  }

  public String getTestPositive() {
      return jTextFieldTestPositive.getText();
  }
  
  /**
   * Returns the desired font size for output window
   *
   * @return the output window font size
   */
  public String getOuputFontSize() {
    return (String) jcbOutputFontSize.getSelectedItem();
  }

  /**
   * Returns the desired font size for display window
   *
   * @return the display window font size
   */
  public String getCodeFontSize() {
    return (String) jcbCodeFontSize.getSelectedItem();
  }

  private void jButton2_actionPerformed(ActionEvent e) {
    close();
  }

//  private void jbUpdate_actionPerformed(ActionEvent e) {
//    close();
//  }

  
  /**
   * Browse for an interpreter file with full path
   */
  private void interpreterPath_actionPerformed() {
    File selectedFile = FileDialogs.getInstance().showDefaultFileChooser(
            new File(jTextFieldInterpreterPath.getText()));

    if (selectedFile != null) {
      jTextFieldInterpreterPath.setText(selectedFile.getAbsolutePath());
    }
  }

  /**
   * Browse for a library path
   */
  private void libraryPath_actionPerformed() {
    File selectedFile = FileDialogs.getInstance().showDefaultDirChooser(
            new File(jTextFieldLibraryPath.getText()));

    if (selectedFile != null)
      jTextFieldLibraryPath.setText(selectedFile.getAbsolutePath());
  }
}
