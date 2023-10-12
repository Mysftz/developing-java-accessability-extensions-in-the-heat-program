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

package managers;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.JOptionPane;
import java.awt.Dimension;

import utils.Resources;
import view.toolbars.MainMenu;
import view.toolbars.Toolbar;
import view.windows.AboutWindow;
import view.windows.EditorWindow;
import view.windows.HelpWindow;
import view.windows.OptionsWindow;
import view.windows.ConsoleWindow;
import view.windows.PrintWindow;
import view.windows.SearchDialog;
import view.windows.WizardWindow;
import view.windows.TreeWindow;



/**
 * The manager class responsible for all GUI components
 */
public class WindowManager {
  private static Logger log = Logger.getLogger("heat");
  private static WindowManager instance = null;

  /* for use in main screen  */
  private JFrame mainScreenFrame;
  private JSplitPane jSplitMain;
  private JSplitPane jSplitTree;

  /* windows */
  private EditorWindow displayWindow;
  private OptionsWindow optionsWindow;
  private AboutWindow aboutWindow;
  private WizardWindow wizardWindow;
  private HelpWindow helpWindow;
  private ConsoleWindow consoleWindow;
  private PrintWindow printwindow;
  private SearchDialog searchWindow;
  private TreeWindow treeWindow;
 

  /* toolbars */
  private MainMenu mainMenu;
  private Toolbar toolbar;
  
  private final int COMPILEDERROR = 0;
  private final int COMPILEDCORRECT = 1;
  private final int UNCOMPILED = 2;
  private final int EVALUATING = 3;
  private final int NOPROGRAM = 4;
  private int savedStatus = -1;
  private int currentStatus = -1;
  private boolean compileEnabled = false;
  private boolean testEnabled = false;
  private boolean interruptEnabled = false;
  
  public boolean isCompiledCorrect() {
      return currentStatus == COMPILEDCORRECT;
  }
  
  public boolean isEvaluating() {
      return currentStatus == EVALUATING;
  }
  
  public void safeStatus() {
      savedStatus = currentStatus;
  }
  
  public void restoreStatus() {
      setStatus(savedStatus);
  }
  
  private void setStatus(int status) {
      currentStatus = status;
      switch (currentStatus) {
          case COMPILEDERROR: 
            log.info("[WindowManager]: setStatusCompiledError");
            toolbar.setCompileStatus(0);
            consoleWindow.setEnabled(false);
            displayWindow.setEnabled(true);
	    setCompileEnabled(false);
	    setTestEnabled(false);
	    setInterruptEnabled(false);
            break;
          case COMPILEDCORRECT:
            log.info("[WindowManager]: setStatusCompiledCorrect");
            toolbar.setCompileStatus(1);
            consoleWindow.setEnabled(true);
            displayWindow.setEnabled(true);
            setCompileEnabled(false);
            setTestEnabled(ParserManager.getParser().hasUncheckedTests());
            setInterruptEnabled(false);
            break;
          case UNCOMPILED:
            log.info("[WindowManager]: setStatusUncompiled");
            toolbar.setCompileStatus(2);
            consoleWindow.setEnabled(false);
            displayWindow.setEnabled(true);
            setCompileEnabled(true);
            setTestEnabled(false);
            setInterruptEnabled(false);
            break; 
          case EVALUATING:
            log.info("[WindowManager]: setStatusEvaluating");
            toolbar.setCompileStatus(3);
            consoleWindow.setEnabled(true);
            displayWindow.setEnabled(false); 
            setCompileEnabled(false);
            setTestEnabled(false);
            setInterruptEnabled(true);
            break; 
          case NOPROGRAM:
            log.info("[WindowManager]: setStatusNoProgram");
            toolbar.setCompileStatus(1);
            consoleWindow.setEnabled(true);
            displayWindow.setEnabled(false);
            setCompileEnabled(false);
            setTestEnabled(false);
            setInterruptEnabled(false);
            break;
          default:
            log.warning("[WindowManager]: set to unkown status");
    } 
  }
  
  public void setStatusNotCompiled() {
      if (FileManager.getInstance().getCurrentFile() != null) {
          setStatusUncompiled();
      } else {
          setStatusNoProgram();
      }
  }
  
  public void setStatusUncompiled() {
      // check to speed up this frequently called method
      if (currentStatus != UNCOMPILED) {
        setStatus(UNCOMPILED);
      }
  }
  
  public void setStatusCompiledError() {
      setStatus(COMPILEDERROR);
  }
  
  public void setStatusCorrect() {
      if (FileManager.getInstance().getCurrentFile() != null) {
          setStatusCompiledCorrect();
      } else {
          setStatusNoProgram();
      }
  }
  
  public void setStatusNoProgram() {
      setStatus(NOPROGRAM);
   }
  
  public void setStatusCompiledCorrect() {
      setStatus(COMPILEDCORRECT);
  }
  
  public void setStatusEvaluating() {
      safeStatus();
      setStatus(EVALUATING);
  }
  
  public void setTestEnabled(boolean on) {
    testEnabled = on;
    mainMenu.setTestEnabled(on);
    toolbar.setTestEnabled(on);
  }
  
  public boolean isTestEnabled() {
      return testEnabled;
  }
  
  public void setCompileEnabled(boolean on) {
    compileEnabled = on;
    mainMenu.setCompileEnabled(on);
    toolbar.setCompileEnabled(on);
  }
  
  public boolean isCompileEnabled() {
      return compileEnabled;
  }
  
  public void setInterruptEnabled(boolean on) {
    interruptEnabled = on;
    mainMenu.setInterruptEnabled(on);
    toolbar.setInterruptEnabled(on);
  }
  
  public boolean isInterruptEnabled() {
      return interruptEnabled;
  }

  protected WindowManager() {
    /* Exists to prevent instantiation */
  }

  /**
   * Returns a single consistent instance of WindowManager
   *
   * @return an instance of WindowManager
   */
  public static WindowManager getInstance() {
    if (instance == null)
      instance = new WindowManager();

    return instance;
  }

  /**
   * Returns the {@link EditorWindow} used in GUI
   *
   * @return the {@link EditorWindow} object from GUI
   */
  public EditorWindow getEditorWindow() {
    return displayWindow;
  }

  /**
   * Returns the {@link ConsoleWindow} used in GUI
   *
   * @return the {@link ConsoleWindow} object from GUI
   */
  public ConsoleWindow getConsoleWindow() {
    return consoleWindow;
  }

  /**
   * Returns the {@link OptionsWindow} used in GUI
   *
   * @return the {@link OptionsWindow} object from GUI
   */
  public OptionsWindow getOptionsWindow() {
    return optionsWindow;
  }

  /**
   * Returns the {@link WizardWindow} used in GUI
   *
   * @return the {@link WizardWindow} object from GUI
   */
  public WizardWindow getWizardWindow() {
    return wizardWindow;
  }


  /**
   * Returns the {@link HelpWindow} used in GUI
   *
   * @return the {@link HelpWindow} object from GUI
   */
  public HelpWindow getHelpWindow() {
    return helpWindow;
  }

  /**
   * Returns the {@link AboutWindow} used in GUI
   *
   * @return the {@link AboutWindow} object from GUI
   */
  public AboutWindow getAboutWindow() {
    return aboutWindow;
  }

  /**
   * Returns the {@link PrintWindow} used in GUI
   *
   * @return the {@link PrintWindow} object from GUI
   */
  public PrintWindow getPrintWindow() {
    return printwindow;
  }

  /**
   * Returns the {@link MainMenu} used in GUI
   *
   * @return the {@link MainMenu} object from GUI
   */
  public MainMenu getMainMenu() {
    return mainMenu;
  }

  /**
   * Returns the {@link SearchDialog} used in GUI
   *
   * @return the {@link SearchDialog} object from GUI
   */
  public SearchDialog getSearchWindow() {
    return searchWindow;
  }
  /**
   * Returns the {@link TreeWindow} used in GUI
   *
   *@return the {@link TreeWindow} object from GUI
   */
  public TreeWindow getTreeWindow(){
      return treeWindow;
  }

  /**
   * Creates the main program GUI, then shows it in the center of the screen
   */
  public void createGUI() {

    if (mainScreenFrame!=null)
	  mainScreenFrame.setVisible(false);
    mainScreenFrame = new JFrame();
    mainScreenFrame.setTitle("HEAT - Haskell Educational Advancement Tool");
    Image icon = Resources.getIcon("logo").getImage();
    mainScreenFrame.setIconImage(icon);
    
    // BorderLayout borderLayout1 = new BorderLayout();

    /* create windows and toolbars */
    displayWindow = new EditorWindow();
    consoleWindow = new ConsoleWindow();
    optionsWindow = new OptionsWindow();
    helpWindow = new HelpWindow();
    aboutWindow = new AboutWindow();
    wizardWindow = new WizardWindow();
    searchWindow = new SearchDialog();
    printwindow = new PrintWindow();
    treeWindow = new TreeWindow();

    mainMenu = new MainMenu();
    toolbar = new Toolbar();

    /* setup main container components */
    // JPanel mainScreenPanel = new JPanel();
    
    jSplitMain = new JSplitPane(JSplitPane.VERTICAL_SPLIT, 
            displayWindow.getJTextPane(), consoleWindow.getWindowPanel());
    jSplitTree = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            treeWindow.getWindowPanel(), jSplitMain);
	
    displayWindow.getJTextPane().setMinimumSize(new Dimension(200,0));
    jSplitMain.setResizeWeight(0.6);
	
    jSplitMain.setOneTouchExpandable(true);
    jSplitTree.setOneTouchExpandable(true);
   

    try {
      /* handle closing screen */
      mainScreenFrame.addWindowListener(new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            JButton jb = new JButton();
            jb.setAction(ActionManager.getInstance().getExitProgramAction());
            jb.doClick();
          }
        });
      mainScreenFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

      /* add windows to the main split panes */
      // jSplitMain.setOrientation(JSplitPane.VERTICAL_SPLIT);
      // jSplitTree.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
     

      // jSplitMain.add(consoleWindow.getWindowPanel(), JSplitPane.BOTTOM);
      // jSplitMain.add(jSplitSub, JSplitPane.TOP);

      // jSplitTree.add(treeWindow.getWindowPanel(), JSplitPane.LEFT);
      // jSplitTree.add(jSplitMain, JSplitPane.RIGHT);
     
      /* add menu and toolbar */
      mainScreenFrame.setJMenuBar(mainMenu.getToolBar());
      mainScreenFrame.getContentPane().add(toolbar.getToolBar(),
        BorderLayout.NORTH);

      /* add splitpane and evaluation window to main frame */
      mainScreenFrame.getContentPane().add(jSplitTree, BorderLayout.CENTER);
      
      mainScreenFrame.setMinimumSize(new Dimension(550,300));
      mainScreenFrame.setSize(620,400);
      mainScreenFrame.pack();
      // java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
      // mainScreenFrame.setSize(java.lang.Math.min(screenSize.width,800)-20, 
      //		  java.lang.Math.min(screenSize.height,600)-20);

      /* centers frame */
      mainScreenFrame.setLocationRelativeTo(null);

      
      setStatusNotCompiled();
      
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
 /* show the main frame */
 public void setVisible() {
      mainScreenFrame.setVisible(true);
  }

  /**
   * Resize subwindows so that only console is visible.
   */
  public void onlyConsole() {
      hideTree();
      jSplitMain.setDividerLocation(jSplitMain.getInsets().top);
  }
  
  /**
   * Resize subwindows so that all are well visible.
   */
  public void showAll() {
      showTree();
      showOutput();
  }
    
    /**
     *  Resize the tree window so it can't be seen
     */
    public void hideTree()
    {
        jSplitTree.setDividerLocation(jSplitTree.getInsets().left);
     //   if (jSplitTree.getDividerLocation() != jSplitTree.getMinimumDividerLocation())
     //       jSplitTree.setDividerLocation(jSplitTree.getMinimumDividerLocation());
    }

    /**
     * Resets the Tree window to default size
     */
    public void showTree()
    {
        jSplitTree.setDividerLocation(jSplitTree.getInsets().left + 
                jSplitTree.getLeftComponent().getPreferredSize().width);
   
     //   if (jSplitTree.getDividerLocation() <= jSplitTree.getMinimumDividerLocation())
     //       if (jSplitTree.getLastDividerLocation() > jSplitTree.getMinimumDividerLocation())
     //           jSplitTree.setDividerLocation(jSplitTree.getLastDividerLocation());
     //       else
     //           jSplitTree.setDividerLocation(jSplitTree.getLeftComponent().getPreferredSize().width);
    }

    /**
     *  Show/Hide Tree Window
     */
    public void toggleTree()
    {
        if (jSplitTree.getDividerLocation() > jSplitTree.getMinimumDividerLocation())
            hideTree();
        else
            showTree();
    }
    
    /**
     *  Hide interpreter output window and show editor full size
     */
    public void hideOutput()
    {
        log.info("hideOutput");
        jSplitMain.setDividerLocation(jSplitMain.getSize().height -
                jSplitMain.getInsets().bottom -
                jSplitMain.getDividerSize());
        // if (jSplitMain.getDividerLocation() != jSplitMain.getMaximumDividerLocation())
        //    jSplitMain.setDividerLocation(jSplitMain.getMaximumDividerLocation());
    }
    
    /**
     *  Show interpreter output window
     */
    public void showOutput()
    {
        log.info("showOutput");
        jSplitMain.setDividerLocation(0.6); 
        // jSplitMain.setDividerLocation(jSplitTree.getInsets().top + 
        //        jSplitMain.getTopComponent().getPreferredSize().height);
  
        //if (jSplitMain.getDividerLocation() >= jSplitMain.getMaximumDividerLocation()) // if its closed
        //    if (jSplitMain.getLastDividerLocation() < jSplitMain.getMaximumDividerLocation()) // &&
        //            // (jSplitMain.getMaximumDividerLocation()-jSplitMain.getLastDividerLocation()) > 20) // if sensible user size
        //        jSplitMain.setDividerLocation(jSplitMain.getLastDividerLocation());
        //    else
        //        jSplitMain.setDividerLocation(jSplitMain.getMaximumDividerLocation()-
        //            jSplitMain.getBottomComponent().getPreferredSize().height);
    }
    
    /**
     *  Show/Hide interpreter output window
     */
    public void toggleConsole()
    {
        int max = jSplitMain.getSize().height - jSplitMain.getInsets().bottom - 
                jSplitMain.getDividerSize();
        if (jSplitMain.getDividerLocation() < max) {
            hideOutput();
        } else {
            showOutput();
        }
        
        //if (jSplitMain.getDividerLocation() < jSplitMain.getMaximumDividerLocation())
        //    hideOutput();
        //else
        //    showOutput();
    }

  /**
   * Repaints the main frame
   */
  public void repaintAll() {
    mainScreenFrame.repaint();
  }

  /**
   * Returns the main screen frame used in GUI
   *
   * @return the main screen frame from GUI
   */
  public JFrame getMainScreenFrame() {
    return mainScreenFrame;
  }

  /* Methods to display extra windows */
  public void showOptionsWindow() {
    getOptionsWindow().show();
  }

  /**
   * Displays the {@link SearchWindow}
   */
  public void showSearchWindow() {
    getSearchWindow().show();
  }

  /**
   * Displays the {@link WizardWindow}
   */
  public void showWizardWindow() {
    getWizardWindow().show();
  }

  /**
   * Displays the {@link HelpWindow}
   */
  public void showHelpWindow() {
    getHelpWindow().show();
  }

  /**
   * Displays the {@link AboutWindow}
   */
  public void showAboutWindow() {
    getAboutWindow().show();
  }

  /**
   * Displays the {@link PrintWindow}
   */
  public void showPrintWindow() {
    getPrintWindow().show();
  }

  /**
   * Sets the default display font sizes
   *
   * @param ptSize the desired font size
   */
  public void setDefaultFontSize(int ptSize) {
    getConsoleWindow().setFontSize(ptSize);
  }

  /**
   * Enables or disables the menu close action
   *
   * @param enabled close command enabled state
   */
  public void setCloseEnabled(boolean enabled) {
    mainMenu.setCloseEnabled(enabled);
    toolbar.setCloseEnabled(enabled);
  }

  /**
   * Copies the selected text from the display or output window
   */
  public void copySelected() {
    /* Using the isFocused methods doesn't work, so I'm just doing a
     * getSelectedText to decide in what window the selection lies */
    String displayWinSelected = displayWindow.getSelectedText();
    String outputWinSelected = consoleWindow.getSelectedText();

    if ((displayWinSelected != null) && !displayWinSelected.equals(""))
      displayWindow.copy();
    else if ((outputWinSelected != null) && !outputWinSelected.equals(""))
      consoleWindow.copy();
  }

  /**
   * Sets the look and feel for HEAT to use
   *
   * @param lnfString Look and Feel classname of desired lnf
   */
  public void setLNF(String lnfString) {
    try {
      UIManager.setLookAndFeel(lnfString);
      SwingUtilities.updateComponentTreeUI(getMainScreenFrame());
      optionsWindow = new OptionsWindow();
      helpWindow = new HelpWindow();
      aboutWindow = new AboutWindow();
      wizardWindow = new WizardWindow();
      printwindow = new PrintWindow();
      searchWindow = new SearchDialog();
    } catch (Exception ex) {
      log.severe("[OptionsWindow] Error setting lnf:" + lnfString);
    }
  }
  
  /**
   * Sets the filename to display in HEAT titlebar
   * 
   * @param fileName the filename to display in titlebar
   */
  public void setTitleFileName(String fileName) {
    if(fileName == null || fileName.trim().equals(""))
      getMainScreenFrame().setTitle("HEAT - Haskell Educational Advancement Tool");
    else
      getMainScreenFrame().setTitle("HEAT - "+fileName);
  }
  
  
  public void openFile(java.io.File file) {
	  
	  FileManager fm = FileManager.getInstance();
          
          if (!fm.openFile(file)) {
            JOptionPane.showMessageDialog(mainScreenFrame, 
                "Error creating new file "+file.getAbsolutePath(),
                "File Creation Error", JOptionPane.ERROR_MESSAGE);
            return;
          }
	  String contents = fm.readFile();

	  getEditorWindow().clearLineMark();
	  getEditorWindow().setEditorContent(contents);
	  // getEditorWindow().setModifiedStatus(false);
          getEditorWindow().setEnabled(true);
	  setCloseEnabled(true);
	  setTitleFileName(fm.getFilePath());

	  // fm.saveTemporary();
	  setStatusUncompiled();
	  //  refresh tree window
	  ParserManager.getInstance().refresh();
	  getTreeWindow().refreshTree();
	  //  and display updated treeWindow
	  showAll();
          
          UndoManager.getInstance().reset();
}

  /**
   * Sets the look and feel for the program
   */
  public static void setLookAndFeel() {
    try {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      log.warning("[WindowManager] Unable to set look and feel");
    }
  }

} // end WindowManager
