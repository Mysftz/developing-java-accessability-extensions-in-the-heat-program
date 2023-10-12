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
 * @author Dean Ashton, Chris Olive, John Travers
 *
 */

package managers;

import java.util.logging.Logger;

import utils.HaskellFilter;
import utils.Resources;
import utils.Settings;
import utils.InterpreterParser;

import view.dialogs.SystemDialogs;
import view.windows.*;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.Toolkit;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 * The manager Class responsible for all GUI action commands
 */
public class ActionManager {
  private static Logger log = Logger.getLogger("heat");
  private static ActionManager instance = null;
  private File selectedFile = null;

  /* Static instantiation of our custom Actions */
  
  // file/program actions
  private ExitProgramAction exitProgramAction = new ExitProgramAction("Quit",
      Resources.getIcon("exit16"), "Quit HEAT", new Integer(KeyEvent.VK_Q),
      KeyStroke.getKeyStroke(KeyEvent.VK_Q, java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
  private ExitProgramAction toolbarExitProgramAction = new ExitProgramAction(null,
      Resources.getIcon("exit22"), "Quit HEAT", new Integer(KeyEvent.VK_Q),
      KeyStroke.getKeyStroke(KeyEvent.VK_Q, java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
  private OpenFileAction openFileAction = new OpenFileAction("Open..",
      Resources.getIcon("fileopen16"), "Open an existing or new file in the editor",
      new Integer(KeyEvent.VK_O),
      KeyStroke.getKeyStroke(KeyEvent.VK_O, java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
  private OpenFileAction toolbarOpenFileAction = new OpenFileAction(null,
      Resources.getIcon("fileopen22"), "Open an existing or new file in the editor",
      new Integer(KeyEvent.VK_O),
      KeyStroke.getKeyStroke(KeyEvent.VK_O, java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
  private CloseFileAction closeFileAction = new CloseFileAction("Close",
	      Resources.getIcon("fileclose16"), "Save file and close editor", null, null);
  private CloseFileAction toolbarCloseFileAction = new CloseFileAction(null,
	      Resources.getIcon("fileclose22"), "Save file and close editor", null, null);
  private PrintFileAction printFileAction = new PrintFileAction("Print",
		  Resources.getIcon("fileprint16"), "Print editor content or interpreter console",
	      new Integer(KeyEvent.VK_P),
	      KeyStroke.getKeyStroke(KeyEvent.VK_P, java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
  private ShowOptionsAction showOptionsAction = new ShowOptionsAction("Options",
	      Resources.getIcon("list16"), "Change HEAT Options",
	      new Integer(KeyEvent.VK_D),
	      KeyStroke.getKeyStroke(KeyEvent.VK_D, java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));

  // editing actions
  private UndoAction undoAction = new UndoAction("Undo", Resources.getIcon("undo16"),
	      "Undo last change", new Integer(KeyEvent.VK_Z),
	      KeyStroke.getKeyStroke(KeyEvent.VK_Z, java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
  private UndoAction toolbarUndoAction = new UndoAction(null, Resources.getIcon("undo22"),
	      "Undo last change", new Integer(KeyEvent.VK_Z),
	      KeyStroke.getKeyStroke(KeyEvent.VK_Z, java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
  private RedoAction redoAction = new RedoAction("Redo", Resources.getIcon("redo16"),
	      "Redo last change", new Integer(KeyEvent.VK_Y),
	      KeyStroke.getKeyStroke(KeyEvent.VK_Y, java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
  private RedoAction toolbarRedoAction = new RedoAction(null, Resources.getIcon("redo22"),
	      "Redo last change", new Integer(KeyEvent.VK_Y),
	      KeyStroke.getKeyStroke(KeyEvent.VK_Y, java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
  private ShowSearchAction showSearchAction = new ShowSearchAction("Find",
	      Resources.getIcon("filefind16"), "Find text in the program",
	      new Integer(KeyEvent.VK_F),
	      KeyStroke.getKeyStroke(KeyEvent.VK_F, java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
  private ShowSearchAction toolbarSearchAction = new ShowSearchAction(null,
	      Resources.getIcon("filefind22"), "Find text in the program",
	      new Integer(KeyEvent.VK_F),
	      KeyStroke.getKeyStroke(KeyEvent.VK_F, java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
  private EditCutAction editCutAction = new EditCutAction("Cut",
	      Resources.getIcon("editcut16"), "Cut selected text", new Integer(KeyEvent.VK_X),
	      KeyStroke.getKeyStroke(KeyEvent.VK_X, java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
  private EditCutAction toolbarEditCutAction = new EditCutAction(null,
	      Resources.getIcon("editcut22"), "Cut selected text", new Integer(KeyEvent.VK_X),
	      KeyStroke.getKeyStroke(KeyEvent.VK_X, java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
  private EditCopyAction editCopyAction = new EditCopyAction("Copy",
	      Resources.getIcon("editcopy16"), "Copy selected text", new Integer(KeyEvent.VK_C),
	      KeyStroke.getKeyStroke(KeyEvent.VK_C, java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
  private EditCopyAction toolbarEditCopyAction = new EditCopyAction(null,
	      Resources.getIcon("editcopy22"), "Copy selected text", new Integer(KeyEvent.VK_C),
	      KeyStroke.getKeyStroke(KeyEvent.VK_C, java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
  private EditPasteAction editPasteAction = new EditPasteAction("Paste",
	      Resources.getIcon("editpaste16"), "Paste selected text", new Integer(KeyEvent.VK_V),
	      KeyStroke.getKeyStroke(KeyEvent.VK_V, java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
  private EditPasteAction toolbarEditPasteAction = new EditPasteAction(null,
	      Resources.getIcon("editpaste22"), "Paste selected text", new Integer(KeyEvent.VK_V),
	      KeyStroke.getKeyStroke(KeyEvent.VK_V, java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));

  // run actions
  private CompileAction compileAction = new CompileAction(null,
		  	Resources.getIcon("reload16"), "Load & compile program", new Integer(KeyEvent.VK_L),
		    KeyStroke.getKeyStroke(KeyEvent.VK_L, java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
  private CompileAction toolbarCompileAction = new CompileAction(null,
		  	Resources.getIcon("reload22"), "Load program into interpreter and compile it", new Integer(KeyEvent.VK_L),
		    KeyStroke.getKeyStroke(KeyEvent.VK_L, java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
  private InterruptAction interruptAction = new InterruptAction(null, 
		  	Resources.getIcon("stop16"), "Interrupt interpreter", new Integer(KeyEvent.VK_I),
		    KeyStroke.getKeyStroke(KeyEvent.VK_I, java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
  private InterruptAction toolbarInterruptAction = new InterruptAction(null, 
		  	Resources.getIcon("stop22"), "Interrupt interpreter", new Integer(KeyEvent.VK_I),
		    KeyStroke.getKeyStroke(KeyEvent.VK_I, java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
  private TestAction testAction = new TestAction(null, 
		  	Resources.getIcon("debug16"), "Check properties", new Integer(KeyEvent.VK_T),
		    KeyStroke.getKeyStroke(KeyEvent.VK_T, java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
  private TestAction toolbarTestAction = new TestAction(null, 
		  	Resources.getIcon("debug22"), "Check properties", new Integer(KeyEvent.VK_T),
		    KeyStroke.getKeyStroke(KeyEvent.VK_T, java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));

  // help actions
  private ShowHelpAction showHelpAction = new ShowHelpAction("Help",
      Resources.getIcon("help16"), "Display help", new Integer(KeyEvent.VK_L),
      KeyStroke.getKeyStroke(KeyEvent.VK_H, java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
  private ShowAboutAction showAboutAction = new ShowAboutAction("About",
      Resources.getIcon("info16"), "Display about information", null, null);
  

  private RefreshTreeAction refreshTreeAction = new RefreshTreeAction("", Resources.getIcon("reload16"),
           "Refresh overview");
  private ExpandTreeAction expandTreeAction = new ExpandTreeAction("", Resources.getIcon("expandTreeWindow16"),
           "Expand all overview elements");
  private CollapseTreeAction collapseTreeAction = new CollapseTreeAction("", Resources.getIcon("collapseTreeWindow16"),
           "Collapse all overview elements");
  private ToggleTreeAction toggleTreeAction = new ToggleTreeAction(null, Resources.getIcon("tree_window_22"),
      "Show/hide overview");
  private ToggleConsoleAction toggleOutputAction = new ToggleConsoleAction(null, Resources.getIcon("output_window_22"),
      "Show/hide interpreter console");
  
  // for the console window:
  private SendEvaluationAction sendEvaluationAction = new SendEvaluationAction("Send",
      Resources.getIcon("effect16"), "Sends Evaluation to Interpreter",
      new Integer(KeyEvent.VK_E),
      KeyStroke.getKeyStroke(KeyEvent.VK_E, java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
  private GoToPastConsoleHistory goToPastConsoleHistory =new GoToPastConsoleHistory();
  private GoToRecentConsoleHistory goToRecentConsoleHistory=new GoToRecentConsoleHistory();
  
  private SaveOptionsAction saveOptionsAction = new SaveOptionsAction("Apply",
	      Resources.getIcon(""), "Apply options",
	      new Integer(KeyEvent.VK_S),
	      KeyStroke.getKeyStroke(KeyEvent.VK_S, java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
  private SaveWizardAction saveWizardAction = new SaveWizardAction("Continue",
	      Resources.getIcon(""), "Save path and continue", new Integer(KeyEvent.VK_S),
	      KeyStroke.getKeyStroke(KeyEvent.VK_S, java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));

  

    
  protected ActionManager() {
    /* Prevent instantiation */
  }
  
  public File getSelectedFile(){
	  return selectedFile;
  }
  /**
   * Returns an instance of the singleton class ActionManager
   * @return ActionManager instance
   */
  public static ActionManager getInstance() {
    if (instance == null)
      instance = new ActionManager();

    return instance;
  }

  /* Getters for the Action objects */
    
  public ActionManager.OpenFileAction getOpenFileAction() {
    return openFileAction;
  }

  public ActionManager.CloseFileAction getCloseFileAction() {
    return closeFileAction;
  }

  public ActionManager.CloseFileAction getToolbarCloseFileAction() {
	    return toolbarCloseFileAction;
  }

  public ActionManager.EditCopyAction getEditCopyAction() {
    return editCopyAction;
  }
  
  public ActionManager.EditCopyAction getToolbarEditCopyAction() {
	    return toolbarEditCopyAction;
  }

  public ActionManager.EditCutAction getEditCutAction() {
    return editCutAction;
  }
  
  public ActionManager.EditCutAction getToolbarEditCutAction() {
	    return toolbarEditCutAction;
  }

  public ActionManager.EditPasteAction getEditPasteAction() {
    return editPasteAction;
  }
  
  public ActionManager.EditPasteAction getToolbarEditPasteAction() {
	    return toolbarEditPasteAction;
  }

  public ActionManager.ExitProgramAction getExitProgramAction() {
    return exitProgramAction;
  }

  public ActionManager.PrintFileAction getPrintFileAction() {
    return printFileAction;
  }

  public ActionManager.CompileAction getCompileAction() {
    return compileAction;
  }
  
  public ActionManager.CompileAction getToolbarCompileAction() {
	    return toolbarCompileAction;
	  }

  public ActionManager.SaveOptionsAction getSaveOptionsAction() {
    return saveOptionsAction;
  }

  public ActionManager.SaveWizardAction getSaveWizardAction() {
    return saveWizardAction;
  }

  public ActionManager.SendEvaluationAction getSendEvaluationAction() {
    return sendEvaluationAction;
  }

  public ActionManager.ShowAboutAction getShowAboutAction() {
    return showAboutAction;
  }

  public ActionManager.ShowHelpAction getShowHelpAction() {
    return showHelpAction;
  }

  public ActionManager.ShowOptionsAction getShowOptionsAction() {
    return showOptionsAction;
  }

  public ActionManager.ExitProgramAction getToolbarExitProgramAction() {
    return toolbarExitProgramAction;
  }

  public ActionManager.OpenFileAction getToolbarOpenFileAction() {
    return toolbarOpenFileAction;
  }

  public ActionManager.UndoAction getUndoAction() {
    return undoAction;
  }

  public ActionManager.UndoAction getToolbarUndoAction() {
	    return toolbarUndoAction;
	  }

  public ActionManager.RedoAction getRedoAction() {
    return redoAction;
  }

  public ActionManager.RedoAction getToolbarRedoAction() {
	    return toolbarRedoAction;
	  }

  public ActionManager.ShowSearchAction getSearchAction() {
    return showSearchAction;
  }

  public ActionManager.ShowSearchAction getToolbarSearchAction() {
    return toolbarSearchAction;
  }

  public ActionManager.RefreshTreeAction getRefreshTreeAction()
  {
      return refreshTreeAction;
  }

  public ActionManager.ExpandTreeAction getExpandTreeAction()
  {
      return expandTreeAction;
  }

  public ActionManager.CollapseTreeAction getCollapseTreeAction()
  {
      return collapseTreeAction;
  }

  public ActionManager.ToggleTreeAction getToggleTreeAction()
  {
      return toggleTreeAction;
  }

  public ActionManager.ToggleConsoleAction getToggleOutputAction()
  {
      return toggleOutputAction;
  }

  public ActionManager.TestAction getTestAction(){
	  return testAction;
  }
  
  public ActionManager.TestAction getToolbarTestAction(){
	  return toolbarTestAction;
  }
  
  public ActionManager.InterruptAction getInterruptAction(){
	return interruptAction;
  }
  
  public ActionManager.InterruptAction getToolbarInterruptAction(){
	  return toolbarInterruptAction;
  }
  
  public ActionManager.GoToPastConsoleHistory getGoToPastConsoleHistory(){
	  return goToPastConsoleHistory;
  }
	  
  public ActionManager.GoToRecentConsoleHistory getGoToRecentConsoleHistory(){
	  return goToRecentConsoleHistory;
  }

    /* The Action SubClasses Follow  */
  /*
    protected class NewFileAction extends AbstractAction {
        public NewFileAction(String text, ImageIcon icon, String desc,
        Integer mnemonic, KeyStroke accelerator) {
            super(text, icon);
            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
            putValue(ACCELERATOR_KEY, accelerator);
        }

        public void actionPerformed(ActionEvent e) {
            WindowManager wm = WindowManager.getInstance();
            InterpreterManager im = InterpreterManager.getInstance();

            if (wm.getEditorWindow().getSavedModStatus())
            if (SystemDialogs.getInstance().confirmSave())
              saveFileAction.actionPerformed(e);

            wm.getEditorWindow().clearLineMark();
            wm.getEditorWindow().setEditorContent("");

            FileManager fm = FileManager.getInstance();
            // fm.saveTemporary();
            fm.setCurrentFile(null);
            wm.setTitleFileName(null);
            wm.getEditorWindow().setModifiedStatus(false);
            wm.getEditorWindow().setSavedModStatus(false);
            wm.setSaveEnabled(false);
            wm.setCloseEnabled(false);
            //wm.setDecButtonsEnabled(true);
            wm.getEditorWindow().changeTokenMarker(false);

            wm.getConsoleWindow().restart();

            ParserManager.getInstance().refresh();
            wm.getTreeWindow().refreshTree();
            wm.hideTree();

            //clear the undo/redo manager
            UndoManager.getInstance().discardAllEdits();
            getUndoAction().updateUndoState();
            getRedoAction().updateRedoState();
            getToolbarUndoAction().updateUndoState();
            getToolbarRedoAction().updateRedoState();
        }
    }  end NewAction */

  /**
   * The action that is invoked by the Run Properties button
   * 
   * @author ii23
   *
   */
  protected class TestAction extends AbstractAction {
	public TestAction(String text, ImageIcon icon, String desc, 
			Integer mnemonic, KeyStroke accelerator){
	  super(text, icon); 
	  putValue(SHORT_DESCRIPTION, desc);
	  putValue(MNEMONIC_KEY, mnemonic);
          putValue(ACCELERATOR_KEY, accelerator);
	}
	public void actionPerformed(ActionEvent arg0) {
 	  WindowManager wm = WindowManager.getInstance();
          if (!wm.isTestEnabled()) {
            Toolkit.getDefaultToolkit().beep();
            return;
          }
 
	  InterpreterManager im=InterpreterManager.getInstance();
	  InterpreterParser ip = InterpreterParser.getInstance();
	  ParserManager pm = ParserManager.getInstance();
          if (pm.getParser().getTests().size()>0){
    	    wm.getTreeWindow().runTests();
          }
	}
  }
  
  protected class ExitProgramAction extends AbstractAction {
    public ExitProgramAction(String text, ImageIcon icon, String desc,
      Integer mnemonic, KeyStroke accelerator) {
      super(text, icon);
      putValue(SHORT_DESCRIPTION, desc);
      putValue(MNEMONIC_KEY, mnemonic);
      putValue(ACCELERATOR_KEY, accelerator);
    }

    public void actionPerformed(ActionEvent e) {
      FileManager fm = FileManager.getInstance();
      fm.ensureSaved();
      System.exit(0);
      /*  
      // FileManager.getInstance().deleteTemporaryFile();
      WindowManager wm = WindowManager.getInstance();
      SettingsManager sm = SettingsManager.getInstance();
      InterpreterManager im = InterpreterManager.getInstance();
      if (sm.isHaveChanges())
        sm.saveSettings();
      if (wm.getEditorWindow().getSavedModStatus()) {
        int result = SystemDialogs.getInstance().showExit();
        if (result == 0) {
          saveFileAction.actionPerformed(e);
          System.exit(0);
        }
        if (result == 1)
          System.exit(0);
      } else
        System.exit(0);
       * 
       */
    }
  } /* end ExitProgramAction */
  
  protected class OpenFileAction extends AbstractAction {
    public OpenFileAction(String text, ImageIcon icon, String desc,
      Integer mnemonic, KeyStroke accelerator) {
      super(text, icon);
      putValue(SHORT_DESCRIPTION, desc);
      putValue(MNEMONIC_KEY, mnemonic);
      putValue(ACCELERATOR_KEY, accelerator);
    }

    public void actionPerformed(ActionEvent e) {
      WindowManager wm = WindowManager.getInstance();
      FileManager fm = FileManager.getInstance();
      
      fm.ensureSaved();
      /*
      if (wm.getEditorWindow().getSavedModStatus())
        if (SystemDialogs.getInstance().confirmSave())
          saveFileAction.actionPerformed(e);
       */

      HaskellFilter haskellFilter = new HaskellFilter();

      JFileChooser jfc = new JFileChooser();
      jfc.setDialogTitle("Open an existing or create a new Haskell File");
      jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
      jfc.setCurrentDirectory(fm.getOpenDirectory());
      jfc.setAcceptAllFileFilterUsed(false);
      jfc.setFileFilter(haskellFilter);

      int result = jfc.showDialog(wm.getMainScreenFrame(),"Open/Create");

      if (result == JFileChooser.CANCEL_OPTION)
        return;
      else if (result == JFileChooser.APPROVE_OPTION) {
        fm.setOpenDirectory(jfc.getCurrentDirectory());
        wm.openFile(jfc.getSelectedFile());
      } else
    	  JOptionPane.showMessageDialog(null, "Error opening file!",
    			  "File Open Error", JOptionPane.ERROR_MESSAGE);
    }

  } /* end OpenFileAction */
  
  /*
  protected class SaveAsFileAction extends AbstractAction {
    public SaveAsFileAction(String text, ImageIcon icon, String desc,
      Integer mnemonic, KeyStroke accelerator) {
      super(text, icon);
      putValue(SHORT_DESCRIPTION, desc);
      putValue(MNEMONIC_KEY, mnemonic);
      putValue(ACCELERATOR_KEY, accelerator);
    }

    public void actionPerformed(ActionEvent e) {
      WindowManager wm = WindowManager.getInstance();
      HaskellFilter haskellFilter = new HaskellFilter();
      JFileChooser jfcs = new JFileChooser();
      jfcs.setDialogTitle("Save File");
      jfcs.setFileSelectionMode(JFileChooser.FILES_ONLY);
      jfcs.setCurrentDirectory(new File("."));
      jfcs.setFileFilter(haskellFilter);

      File file = null;
      String path = null;
      boolean fileConfirmed = false;

      // Loop in case the file exists, and they don't want to overwrite  
      do {
        int result = jfcs.showSaveDialog(wm.getMainScreenFrame());

        if (result == JFileChooser.CANCEL_OPTION)
          return;
        else if (result == JFileChooser.APPROVE_OPTION) {
          file = jfcs.getSelectedFile();
          path = file.getAbsolutePath();

          // File already exists 
          if (file.exists()) {
            JOptionPane jop = new JOptionPane();

            result = jop.showConfirmDialog(wm.getMainScreenFrame(),
                "The file " + file + " already exists, Overwrite ?", "Warning",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (result == JOptionPane.CANCEL_OPTION)
              return;

            if (result == JOptionPane.YES_OPTION)
              fileConfirmed = true;

            if (result == JOptionPane.NO_OPTION)
              fileConfirmed = false;
          } else
            fileConfirmed = true;
        }
      } while (!fileConfirmed);

      // File has been chosen, and confirmed continue with write 
      if (!(path.endsWith(".hs") || path.endsWith(".lhs") ||
          path.endsWith(".HS") || path.endsWith(".LHS"))) {
        path += ".hs";
        file = new File(path);
      }

      String content = wm.getEditorWindow().getEditorContent();
      FileManager fm = FileManager.getInstance();
      fm.writeFile(file, content);
      fm.setCurrentFile(file);
      wm.getEditorWindow().setModifiedStatus(false);
      wm.getEditorWindow().setSavedModStatus(false);
      wm.setSaveEnabled(false);
      wm.setCloseEnabled(true);
      fm.setCurrentFile(file);
      wm.setTitleFileName(file.getAbsolutePath());

      if (fm.getFileType())
        wm.getEditorWindow().changeTokenMarker(true);
      else
        wm.getEditorWindow().changeTokenMarker(false);
    }
  }  end SaveAsFileAction */
  
  /*
  protected class SaveFileAction extends AbstractAction {
    public SaveFileAction(String text, ImageIcon icon, String desc,
      Integer mnemonic, KeyStroke accelerator) {
      super(text, icon);
      putValue(SHORT_DESCRIPTION, desc);
      putValue(MNEMONIC_KEY, mnemonic);
      putValue(ACCELERATOR_KEY, accelerator);
    }

    public void actionPerformed(ActionEvent e) {
      FileManager fm = FileManager.getInstance();
      File currentFile = fm.getCurrentFile();
      WindowManager wm = WindowManager.getInstance();

      if (currentFile != null) {
        String content = wm.getEditorWindow().getEditorContent();
        fm.writeFile(currentFile, content);
        wm.getEditorWindow().setModifiedStatus(false);
        wm.getEditorWindow().setSavedModStatus(false);
        wm.setSaveEnabled(false);
      } else
        saveAsFileAction.actionPerformed(e);
    }
  }  end SaveFileAction */
  
  /**
   * SendEvaluationAction is called whenever Enter is keyed in the 
   * console window. Thus it is used for sending an expression/command
   * to the interpreter and when sending input to an interactive Haskell program.
   */
  
  protected class SendEvaluationAction extends AbstractAction {
    public SendEvaluationAction(String text, ImageIcon icon, String desc,
      Integer mnemonic, KeyStroke accelerator) {
      super(text, icon);
      putValue(SHORT_DESCRIPTION, desc);
      putValue(MNEMONIC_KEY, mnemonic);
      putValue(ACCELERATOR_KEY, accelerator);
    }

    public void actionPerformed(ActionEvent e) {
      WindowManager wm = WindowManager.getInstance();
      if (wm.getConsoleWindow().isEnabled()) {
    	  String command = wm.getConsoleWindow().getCommand();
    	  log.info("Send Command: " + command);
    	  if (!wm.isEvaluating()) {
              wm.setStatusEvaluating();
          }
    	  wm.getConsoleWindow().outputInput('\n',true);
    	  wm.getConsoleWindow().evalCommand(command);
      } else
    	  java.awt.Toolkit.getDefaultToolkit().beep();
    }
  }

  protected class ShowOptionsAction extends AbstractAction {
    public ShowOptionsAction(String text, ImageIcon icon, String desc,
      Integer mnemonic, KeyStroke accelerator) {
      super(text, icon);
      putValue(SHORT_DESCRIPTION, desc);
      putValue(MNEMONIC_KEY, mnemonic);
      putValue(ACCELERATOR_KEY, accelerator);
    }

    public void actionPerformed(ActionEvent e) {
      WindowManager wm = WindowManager.getInstance();
      wm.showOptionsWindow();
    }
  } /* end ShowOptionsAction */
  protected class ShowSearchAction extends AbstractAction {
    public ShowSearchAction(String text, ImageIcon icon, String desc,
      Integer mnemonic, KeyStroke accelerator) {
      super(text, icon);
      putValue(SHORT_DESCRIPTION, desc);
      putValue(MNEMONIC_KEY, mnemonic);
      putValue(ACCELERATOR_KEY, accelerator);
    }

    public void actionPerformed(ActionEvent e) {
      WindowManager wm = WindowManager.getInstance();
      wm.showSearchWindow();
    }
  } /* end ShowOptionsAction */
  protected class ShowAboutAction extends AbstractAction {
    public ShowAboutAction(String text, ImageIcon icon, String desc,
      Integer mnemonic, KeyStroke accelerator) {
      super(text, icon);
      putValue(SHORT_DESCRIPTION, desc);
      putValue(MNEMONIC_KEY, mnemonic);
      putValue(ACCELERATOR_KEY, accelerator);
    }

    public void actionPerformed(ActionEvent e) {
      WindowManager wm = WindowManager.getInstance();
      wm.showAboutWindow();
    }
  } /* end ShowAboutAction */
  protected class SaveOptionsAction extends AbstractAction {
    public SaveOptionsAction(String text, ImageIcon icon, String desc,
      Integer mnemonic, KeyStroke accelerator) {
      super(text, icon);
      putValue(SHORT_DESCRIPTION, desc);
      putValue(MNEMONIC_KEY, mnemonic);
      putValue(ACCELERATOR_KEY, accelerator);
    }

    public void actionPerformed(ActionEvent e) {
      WindowManager wm = WindowManager.getInstance();
      boolean essentialChange = false;

      String interpreterPath = wm.getOptionsWindow().getInterpreterPath();
      String interpreterOpts = wm.getOptionsWindow().getInterpreterOpts();
      String libraryPath = wm.getOptionsWindow().getLibraryPath();
      String outputFontSize = wm.getOptionsWindow().getOuputFontSize();
      String codeFontSize = wm.getOptionsWindow().getCodeFontSize();
      SettingsManager sm = SettingsManager.getInstance();
      InterpreterManager im = InterpreterManager.getInstance();

      if (!(sm.getSetting(Settings.INTERPRETER_PATH).equals(interpreterPath)
              && sm.getSetting(Settings.INTERPRETER_OPTS).equals(interpreterOpts)
              && sm.getSetting(Settings.LIBRARY_PATH).equals(libraryPath))) {
        sm.setSetting(Settings.INTERPRETER_PATH, interpreterPath);
        sm.setSetting(Settings.INTERPRETER_OPTS, interpreterOpts);
        sm.setSetting(Settings.LIBRARY_PATH, libraryPath);
        essentialChange = true;
      } 
      
      sm.setSetting(Settings.TEST_FUNCTION, wm.getOptionsWindow().getTestFunction().trim());
      sm.setSetting(Settings.TEST_POSITIVE, wm.getOptionsWindow().getTestPositive().trim());

      /* Perform any font updates */
      try {
        int outputFontsize = Integer.parseInt(outputFontSize);
        wm.getConsoleWindow().setFontSize(outputFontsize);
        sm.setSetting(Settings.OUTPUT_FONT_SIZE, outputFontSize);
      } catch (NumberFormatException nfe) {
        log.warning("[ActionManager] - Failed to parse " +
          Settings.OUTPUT_FONT_SIZE + " setting from options window");
      }

      try {
        int codeFontsize = Integer.parseInt(codeFontSize);
        wm.getEditorWindow().setFontSize(codeFontsize);
        sm.setSetting(Settings.CODE_FONT_SIZE, codeFontSize);
      } catch (NumberFormatException nfe) {
        log.warning("[ActionManager] - Failed to parse " +
          Settings.CODE_FONT_SIZE + " setting from options window");
      }
    
      wm.getOptionsWindow().close();
      sm.saveSettings();
      
      if (essentialChange) {
        // wm.createGUI();
        // wm.getConsoleWindow().outputInfo("Settings changes applied.\n");
          wm.getConsoleWindow().restart();
      } else {
          wm.repaintAll();
      }
    }
  } /* end SaveOptionsAction */
  
  /*
   * Save Action of Wizard Window for Settings
   */
  protected class SaveWizardAction extends AbstractAction {
    public SaveWizardAction(String text, ImageIcon icon, String desc,
      Integer mnemonic, KeyStroke accelerator) {
      super(text, icon);
      putValue(SHORT_DESCRIPTION, desc);
      putValue(MNEMONIC_KEY, mnemonic);
      putValue(ACCELERATOR_KEY, accelerator);
    }

    public void actionPerformed(ActionEvent e) {
      WindowManager wm = WindowManager.getInstance();
      String interpreterPath = wm.getWizardWindow().getInterpreterPath();
      SettingsManager sm = SettingsManager.getInstance();
      InterpreterManager im = InterpreterManager.getInstance();

      wm.getWizardWindow().close();

      sm.setSetting(Settings.INTERPRETER_PATH, interpreterPath);
      sm.saveSettings();
      sm.loadSettings();

      // FileManager fm = FileManager.getInstance();
      // fm.saveTemporary();
      im.startProcess(false);
    }
  } /* end SaveWizardAction */
  
 
  protected class CloseFileAction extends AbstractAction {
    public CloseFileAction(String text, ImageIcon icon, String desc,
      Integer mnemonic, KeyStroke accelerator) {
      super(text, icon);
      putValue(SHORT_DESCRIPTION, desc);
      putValue(MNEMONIC_KEY, mnemonic);
      putValue(ACCELERATOR_KEY, accelerator);
    }

    public void actionPerformed(ActionEvent e) {
      FileManager fm = FileManager.getInstance();
      WindowManager wm = WindowManager.getInstance();
      
      fm.closeCurrentFile();
      wm.setCloseEnabled(false);
      wm.setStatusNoProgram();
      wm.setStatusEvaluating();
      
      wm.setTitleFileName(null);
      wm.getEditorWindow().setEnabled(false);
      wm.getEditorWindow().setEditorContent("Use menu to open an existing or create a new program in the editor.");
      
      UndoManager.getInstance().reset();
     
      ParserManager.getInstance().refresh();
      wm.getTreeWindow().refreshTree();


      wm.onlyConsole();
      wm.getConsoleWindow().unload();
    }
  } /* end CloseFileAction */
  
  
  protected class PrintFileAction extends AbstractAction {
    public PrintFileAction(String text, ImageIcon icon, String desc,
      Integer mnemonic, KeyStroke accelerator) {
      super(text, icon);
      putValue(SHORT_DESCRIPTION, desc);
      putValue(MNEMONIC_KEY, mnemonic);
      putValue(ACCELERATOR_KEY, accelerator);
    }

    public void actionPerformed(ActionEvent e) {
      WindowManager wm = WindowManager.getInstance();
      wm.showPrintWindow();
    }
  } /* end PrintFileAction */
  protected class ShowHelpAction extends AbstractAction {
    public ShowHelpAction(String text, ImageIcon icon, String desc,
      Integer mnemonic, KeyStroke accelerator) {
      super(text, icon);
      putValue(SHORT_DESCRIPTION, desc);
      putValue(MNEMONIC_KEY, mnemonic);
      putValue(ACCELERATOR_KEY, accelerator);
    }

    public void actionPerformed(ActionEvent e) {
      WindowManager wm = WindowManager.getInstance();
      wm.showHelpWindow();
    }
  } /* end ShowHelpAction */
  protected class EditCopyAction extends AbstractAction {
    public EditCopyAction(String text, ImageIcon icon, String desc,
      Integer mnemonic, KeyStroke accelerator) {
      super(text, icon);
      putValue(SHORT_DESCRIPTION, desc);
      putValue(MNEMONIC_KEY, mnemonic);
      putValue(ACCELERATOR_KEY, accelerator);
    }

    public void actionPerformed(ActionEvent e) {
      WindowManager wm = WindowManager.getInstance();
      wm.getEditorWindow().copy();
      // wm.copySelected();
    }
  } /* end EditCopyAction */
  protected class EditCutAction extends AbstractAction {
    public EditCutAction(String text, ImageIcon icon, String desc,
      Integer mnemonic, KeyStroke accelerator) {
      super(text, icon);
      putValue(SHORT_DESCRIPTION, desc);
      putValue(MNEMONIC_KEY, mnemonic);
      putValue(ACCELERATOR_KEY, accelerator);
    }

    public void actionPerformed(ActionEvent e) {
      WindowManager wm = WindowManager.getInstance();
      wm.getEditorWindow().cut();
    }
  } /* end EditCutAction */
  protected class EditPasteAction extends AbstractAction {
    public EditPasteAction(String text, ImageIcon icon, String desc,
      Integer mnemonic, KeyStroke accelerator) {
      super(text, icon);
      putValue(SHORT_DESCRIPTION, desc);
      putValue(MNEMONIC_KEY, mnemonic);
      putValue(ACCELERATOR_KEY, accelerator);
    }

    public void actionPerformed(ActionEvent e) {
      WindowManager wm = WindowManager.getInstance();
      wm.getEditorWindow().paste();
    }
  } /* end EditPasteAction */
  protected class CompileAction extends AbstractAction {
    public CompileAction(String text, ImageIcon icon,
      String desc, Integer mnemonic, KeyStroke accelerator) {
      super(text, icon);
      putValue(SHORT_DESCRIPTION, desc);
      putValue(MNEMONIC_KEY, mnemonic);
      putValue(ACCELERATOR_KEY, accelerator);
    }

    public void actionPerformed(ActionEvent e) {
      WindowManager wm = WindowManager.getInstance();
      if (!wm.isCompileEnabled()) {
          Toolkit.getDefaultToolkit().beep();
          return;
      }
      wm.getEditorWindow().clearLineMark();
      //wm.getOuputWindow().appendOutput(System.getProperty("line.separator"));

      FileManager fm = FileManager.getInstance();
      InterpreterManager im = InterpreterManager.getInstance();

      fm.ensureSaved();

      //InterpreterManager.compile();  
      
      wm.setStatusEvaluating();
      wm.getConsoleWindow().compile();
      //im.compile();
      //im.breakInterpreter();
      //wm.getTreeWindow().refreshTree(wm.getEditorWindow().getEditorContent());
      ParserManager.getInstance().refresh();
      wm.getTreeWindow().refreshTree();
      //wm.showTree(true);
    }
  } /* end CompileAction */
  public class UndoAction extends AbstractAction {
    public UndoAction(String text, ImageIcon icon, String desc,
      Integer mnemonic, KeyStroke accelerator) {
      super(text, icon);
      setEnabled(false);
      putValue(SHORT_DESCRIPTION, desc);
      putValue(MNEMONIC_KEY, mnemonic);
      putValue(ACCELERATOR_KEY, accelerator);
    }

    public void actionPerformed(ActionEvent e) {
      UndoManager um = UndoManager.getInstance();
      
      if (!um.canUndo()) {
          Toolkit.getDefaultToolkit().beep();
          return;
      }

      try {
        um.undo();
      } catch (CannotUndoException ex) {
        log.warning("ActionManager: Unable to undo " + ex);
      }

      WindowManager.getInstance().getMainMenu().updateUndoRedo();
    }

    public void updateUndoState() {
      UndoManager um = UndoManager.getInstance();

      if (um.canUndo()) {
        setEnabled(true);
        //putValue(Action.NAME, "Undo");
      } else {
        setEnabled(false);
        //putValue(Action.NAME, "Undo");
      }
    }
  } /* end UndoAction */
  
  public class RedoAction extends AbstractAction {
    public RedoAction(String text, ImageIcon icon, String desc,
      Integer mnemonic, KeyStroke accelerator) {
      super(text, icon);
      setEnabled(false);
      putValue(SHORT_DESCRIPTION, desc);
      putValue(MNEMONIC_KEY, mnemonic);
      putValue(ACCELERATOR_KEY, accelerator);
    }

    public void actionPerformed(ActionEvent e) {
      UndoManager um = UndoManager.getInstance();
      
      if (!um.canRedo()) {
          Toolkit.getDefaultToolkit().beep();
          return;
      }

      try {
        um.redo();
      } catch (CannotRedoException ex) {
        log.warning("ActionManager: Unable to redo " + ex);
      }

      WindowManager.getInstance().getMainMenu().updateUndoRedo();
    }

    public void updateRedoState() {
      UndoManager um = UndoManager.getInstance();

      if (um.canRedo()) {
        setEnabled(true);
        //putValue(Action.NAME, "Redo");
      } else {
        setEnabled(false);
        //putValue(Action.NAME, "Redo");
      }
    }
  } /* end UndoAction */

    protected class RefreshTreeAction extends AbstractAction
    {
        public RefreshTreeAction(String text, ImageIcon icon, String desc)
        {
            super(text, icon);
            setEnabled(true);
            putValue(SHORT_DESCRIPTION, desc);
            //putValue(MNEMONIC_KEY, mnemonic);
            //putValue(ACCELERATOR_KEY, accelerator);
        }

        public void actionPerformed(ActionEvent e)
        {
            //WindowManager wm = WindowManager.getInstance();
            //wm.getTreeWindow().refreshTree(wm.getEditorWindow().getEditorContent());
            ParserManager.getInstance().refresh();
            WindowManager.getInstance().getTreeWindow().refreshTree();
        }
    }

    protected class ExpandTreeAction extends AbstractAction
    {
        public ExpandTreeAction(String text, ImageIcon icon, String desc)
        {
            super(text, icon);
            //setEnabled(true);
            putValue(SHORT_DESCRIPTION, desc);
            //putValue(MNEMONIC_KEY, mnemonic);
            //putValue(ACCELERATOR_KEY, accelerator);
        }

        public void actionPerformed(ActionEvent e)
        {
            WindowManager wm = WindowManager.getInstance();
            wm.getTreeWindow().refreshTree();
            wm.getTreeWindow().expandTree();
        }
    }

    protected class CollapseTreeAction extends AbstractAction
    {
        public CollapseTreeAction(String text, ImageIcon icon, String desc)
        {
            super(text, icon);
            //setEnabled(true);
            putValue(SHORT_DESCRIPTION, desc);
            //putValue(MNEMONIC_KEY, mnemonic);
            //putValue(ACCELERATOR_KEY, accelerator);
        }

        public void actionPerformed(ActionEvent e)
        {
            WindowManager wm = WindowManager.getInstance();
            wm.getTreeWindow().refreshTree();
            wm.getTreeWindow().collapseTree();
        }
    }

    protected class ToggleTreeAction extends AbstractAction
    {
        public ToggleTreeAction(String text, ImageIcon icon, String desc)
        {
            super(text, icon);
            //setEnabled(true);
            putValue(SHORT_DESCRIPTION, desc);
            //putValue(MNEMONIC_KEY, mnemonic);
            //putValue(ACCELERATOR_KEY, accelerator);
        }

        public void actionPerformed(ActionEvent e)
        {
            WindowManager.getInstance().toggleTree();
        }
    }

    protected class ToggleConsoleAction extends AbstractAction
    {
        public ToggleConsoleAction(String text, ImageIcon icon, String desc)
        {
            super(text, icon);
            //setEnabled(true);
            putValue(SHORT_DESCRIPTION, desc);
            //putValue(MNEMONIC_KEY, mnemonic);
            //putValue(ACCELERATOR_KEY, accelerator);
        }

        public void actionPerformed(ActionEvent e)
        {
            WindowManager.getInstance().toggleConsole();
        }
    }


    /**
     * Action classes for going through the console history
     * 
     */
   protected class GoToPastConsoleHistory extends AbstractAction{
      public void actionPerformed(ActionEvent arg0) {
    	  ConsoleWindow console = WindowManager.getInstance().getConsoleWindow();
    	  if (console.isEnabled())
    		  console.commandHistoryBackwards();
    	  else
    		  java.awt.Toolkit.getDefaultToolkit().beep();
      }  
   }
    
   protected class GoToRecentConsoleHistory extends AbstractAction{
  	  public void actionPerformed(ActionEvent arg0) {
    	  ConsoleWindow console = WindowManager.getInstance().getConsoleWindow();
    	  if (console.isEnabled())
    		  console.commandHistoryForwards();
    	  else
    		  java.awt.Toolkit.getDefaultToolkit().beep();
  	  }
   }
    
  
    /**
     * Restarts the interpreter and stops any code or property evaluation running
     * @author ii23
     *
     */
    protected class InterruptAction extends AbstractAction  {
    	public InterruptAction(String text, ImageIcon icon, String desc,
    		      Integer mnemonic, KeyStroke accelerator){
          super(text, icon);
          putValue(SHORT_DESCRIPTION, desc);
          putValue(MNEMONIC_KEY, mnemonic);
          putValue(ACCELERATOR_KEY, accelerator);
    	}
    	
	public void actionPerformed(ActionEvent arg0) {
          WindowManager wm = WindowManager.getInstance();
          if (!wm.isInterruptEnabled()) {
            Toolkit.getDefaultToolkit().beep();
            return;
          }
 
	  wm.getConsoleWindow().interrupt();
        }
      
    }
    
    
    
    
} /* end ActionManger */
