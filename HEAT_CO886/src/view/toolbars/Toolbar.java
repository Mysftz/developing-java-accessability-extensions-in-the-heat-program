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
 * @author Dean Ashton, Sergei Krot
 *
 */

package view.toolbars;

import managers.ActionManager;

import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.ImageIcon;
import utils.Resources;


/**
 * The toolbar used within HEAT
 */
public class Toolbar {
  /* The toolbar */
  private JToolBar toolBar = new JToolBar();
  private ActionManager am = ActionManager.getInstance();
  
  /* some icons */
  private ImageIcon iiCompileSuccess = Resources.getIcon("buttonok22");
  private ImageIcon iiCompileUnknown = Resources.getIcon("buttonquestion22");
  private ImageIcon iiCompileFail = Resources.getIcon("buttoncancel22");
  private ImageIcon iiWorking = Resources.getIcon("effect22");

  /* The buttons in use */
  private JButton openButton = new JButton(am.getToolbarOpenFileAction());
  private JButton closeButton = new JButton(am.getToolbarCloseFileAction());
  private JButton undoButton = new JButton(am.getToolbarUndoAction());
  private JButton redoButton = new JButton (am.getToolbarRedoAction());
  private JButton cutButton = new JButton(am.getToolbarEditCutAction());
  private JButton copyButton = new JButton(am.getToolbarEditCopyAction());
  private JButton pasteButton = new JButton(am.getToolbarEditPasteAction());
  private JButton searchButton = new JButton(am.getToolbarSearchAction());
  private JButton compileButton = new JButton(am.getToolbarCompileAction());
  private JButton interruptButton = new JButton(am.getToolbarInterruptAction());
  private JButton testButton = new JButton(am.getToolbarTestAction());
  private JButton treeWindowButton = new JButton(am.getToggleTreeAction());
  private JButton outputWindowButton = new JButton(am.getToggleOutputAction());
  private JButton statusButton = new JButton();

  /**
   * Creates a new Toolbar object.
   */
  public Toolbar() {
    try {
      createToolbar();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Creates the toolbar component
   *
   * @throws Exception If a button could not be added
   */
  public void createToolbar() throws Exception {
    toolBar.setFloatable(false);
    toolBar.add(openButton);
    toolBar.add(closeButton);
    toolBar.addSeparator();
    toolBar.add(undoButton);
    toolBar.add(redoButton);
    toolBar.addSeparator();
    toolBar.add(cutButton);
    toolBar.add(copyButton);
    toolBar.add(pasteButton);
    toolBar.add(searchButton);
    toolBar.addSeparator();
    toolBar.add(compileButton);
    toolBar.add(interruptButton);
    toolBar.add(testButton);
    toolBar.addSeparator();
    toolBar.add(treeWindowButton);
    toolBar.add(outputWindowButton);
    toolBar.add(javax.swing.Box.createHorizontalGlue());
    toolBar.add(statusButton);
    statusButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    statusButton.setFocusable(false);
    statusButton.setBorderPainted(false);
    statusButton.setContentAreaFilled(false);
    statusButton.setText("Status");
    statusButton.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
    setCompileStatus(1);
  }

  /**
   * Returns the toolbar
   *
   * @return the toolbar object
   */
  public JToolBar getToolBar() {
    return toolBar;
  }
  
  public void setInterruptEnabled(boolean enabled) {
    interruptButton.setEnabled(enabled);
  }
  
  public void setTestEnabled(boolean enabled) {
    testButton.setEnabled(enabled);
  }
  
  public void setCompileEnabled(boolean enabled) {
    compileButton.setEnabled(enabled);
  }
  
  public void setCloseEnabled(boolean enabled) {
    closeButton.setEnabled(enabled);
  }
  /**
   * Sets the compile status icon
   *
   * @param status compilation status, 0=Fail, 1=Success, 2=Unknown
   */
  public void setCompileStatus(int status) {
	  switch (status) {
	  	case 0: statusButton.setIcon(iiCompileFail); break;
	  	case 1: statusButton.setIcon(iiCompileSuccess); break;
	  	case 2: statusButton.setIcon(iiCompileUnknown); break;
	  	case 3: statusButton.setIcon(iiWorking); break;
	  }
  } 
  
}
