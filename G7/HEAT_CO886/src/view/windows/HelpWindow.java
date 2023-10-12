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
 * @author John Travers
 *
 */

package view.windows;

import managers.WindowManager;

import java.util.logging.Logger;

import utils.Resources;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.IOException;

import java.net.URL;

import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;


public class HelpWindow {
  private static boolean DEBUG = false;
  private static Logger log = Logger.getLogger("heat");
  private JPanel jPanel0 = new JPanel();
  private Icon iconHome = new ImageIcon();
  private Icon iconBack = new ImageIcon();
  private Icon iconForward = new ImageIcon();
  private JEditorPane htmlPane;
  private URL helpURL;
  private ArrayList nodeforward = new ArrayList();
  private ArrayList nodeback = new ArrayList();
  private boolean addNode = true;
  JFrame frame = new JFrame("HEAT Help");
  private JPanel jPanel2 = new JPanel();
  private FlowLayout flowLayout1 = new FlowLayout();
  private JButton jBack = new JButton();
  private JButton jForward = new JButton();
  private JButton jHome = new JButton();
  private JSplitPane splitPane = new JSplitPane();
  private BorderLayout borderLayout1 = new BorderLayout();
  private JTree tree;

  public HelpWindow() {
    try {
      jbInit();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    nodeback.clear();
    nodeforward.clear();

    iconHome = Resources.getIcon("home16");
    iconBack = Resources.getIcon("back16");
    iconForward = Resources.getIcon("forward16");

    jPanel0.setSize(new Dimension(670, 400));

    DefaultMutableTreeNode top = new DefaultMutableTreeNode("HEAT Help");
    createNodes(top);

    tree = new JTree(top);
    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

    JScrollPane treeView = new JScrollPane(tree);
    htmlPane = new JEditorPane();
    htmlPane.setEditable(false);
    htmlPane.addHyperlinkListener(new Hyperactive());
    initHelp();

    JScrollPane htmlView = new JScrollPane(htmlPane);

    Dimension minimumSize = new Dimension(100, 50);
    jPanel0.setLayout(borderLayout1);
    htmlView.setMinimumSize(minimumSize);
    splitPane.setTopComponent(treeView);
    splitPane.setBottomComponent(htmlView);
    splitPane.setDividerLocation(180);
    splitPane.setPreferredSize(new Dimension(630, 350));
    jPanel2.setLayout(flowLayout1);
    jPanel2.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 170));
    flowLayout1.setAlignment(0);
    flowLayout1.setHgap(0);

    jBack.setText("Back");
    jBack.setPreferredSize(new Dimension(100, 25));
    jBack.setSize(new Dimension(110, 25));
    jBack.setIcon(iconBack);
    jBack.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          jBack_actionPerformed(e);
        }
      });
    jForward.setText("Forward");
    jForward.setPreferredSize(new Dimension(100, 25));
    jForward.setSize(new Dimension(110, 25));
    jForward.setIcon(iconForward);
    jForward.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          jForward_actionPerformed(e);
        }
      });
    jHome.setText("Home");
    jHome.setPreferredSize(new Dimension(100, 25));
    jHome.setSize(new Dimension(110, 25));
    jHome.setIcon(iconHome);
    jHome.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          jHome_actionPerformed(e);
        }
      });
    treeView.setMinimumSize(minimumSize);

    jPanel2.add(jBack, BorderLayout.EAST);
    jPanel2.add(jForward, null);
    jPanel2.add(jHome, null);

    jPanel0.add(splitPane, BorderLayout.CENTER);
    jPanel0.add(jPanel2, BorderLayout.NORTH);

    // listen for when the selection changes.
    tree.addTreeSelectionListener(new TreeSelectionListener() {
        public void valueChanged(TreeSelectionEvent e) {
          DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

          if (node == null)
            return;

          Object nodeInfo = node.getUserObject();

          if (node.isLeaf()) {
            BookInfo book = (BookInfo) nodeInfo;
            displayURL(book.bookURL);

            if (addNode)
              nodeback.add(tree.getSelectionPath());

            addNode = true;

            if (DEBUG)
              log.warning("[HelpWindow] - " + book.bookURL + ":  \n    ");
          } else
            displayURL(helpURL);

          if (DEBUG)
            log.warning("[HelpWindow] - " + nodeInfo.toString());
        }
      });
  }

  private void initHelp() {
    String s = Resources.getHelpFilePath("index");

    try {
      helpURL = new URL(s);
      displayURL(helpURL);
    } catch (Exception e) {
      log.warning("[HelpWindow] - Couldn't create help URL: " + s);
    }
  }

  private void home() {
    addNode = false;
    collapseAll(tree.getPathForRow(0));
    tree.expandRow(0);
    tree.setSelectionRow(0);
  }

  public void collapseAll(TreePath path) {
    Object node = path.getLastPathComponent();
    TreeModel model = tree.getModel();

    if (model.isLeaf(node))
      return;

    int num = model.getChildCount(node);

    for (int i = 0; i < num; i++) {
      collapseAll(path.pathByAddingChild(model.getChild(node, i)));
    }

    tree.collapsePath(path);
  }

  private void displayURL(URL url) {
    try {
      htmlPane.setPage(url);
      log.warning("[HelpWindow] - displayURL:setPage " + url);
    } catch (IOException e) {
      log.warning("[HelpWindow] - Attempted to read a bad URL: " + url);
    }
  }

  // all help selections
  private void createNodes(DefaultMutableTreeNode top) {
    top.add(new DefaultMutableTreeNode(new BookInfo("Welcome to HEAT","index")));  
    top.add(new DefaultMutableTreeNode(new BookInfo("Status Indicator","status")));
    top.add(new DefaultMutableTreeNode(new BookInfo("Haskell Programs","files")));
    top.add(new DefaultMutableTreeNode(new BookInfo("Checking Properties","properties")));
    top.add(new DefaultMutableTreeNode(new BookInfo("Haskell Interpreter","interpreter")));
    top.add(new DefaultMutableTreeNode(new BookInfo("Limitations ... bugs","problems")));
  }

  public void show() {
    frame.getContentPane().add(jPanel0);
    frame.setSize(670, 435);
    frame.setLocationRelativeTo(WindowManager.getInstance().getMainScreenFrame());
    frame.setVisible(true);
  }

  private void jClose_actionPerformed(ActionEvent e) {
    close();
  }

  private void jHome_actionPerformed(ActionEvent e) {
    home();
  }

  private void jBack_actionPerformed(ActionEvent e) {
    if (nodeback.size() > 1) {
      int currentBook = nodeback.size() - 1;
      nodeforward.add(nodeback.get(currentBook)); // add current book to forward

      TreePath currentpath = (TreePath) nodeback.get(currentBook);

      nodeback.remove(currentBook);

      int lastBook = nodeback.size() - 1;

      TreePath nextpath = (TreePath) nodeback.get(lastBook);

      if (!(nextpath.getParentPath().equals(currentpath.getParentPath())))
        tree.collapsePath(currentpath.getParentPath());

      addNode = false; // flag not to re-add selected path in listener
      tree.setSelectionPath(nextpath);
    }
  }

  private void jForward_actionPerformed(ActionEvent e) {
    if (nodeforward.size() > 0) {
      int currentBook = nodeforward.size() - 1;
      nodeback.add(nodeforward.get(currentBook)); // add current book to back

      TreePath currentpath = (TreePath) nodeforward.get(currentBook);

      addNode = false;
      tree.setSelectionPath(currentpath);

      nodeforward.remove(nodeforward.size() - 1);
    }
  }

  public void close() {
    nodeback.clear();
    nodeforward.clear();
    frame.dispose();
  }

  private class BookInfo {
    public String bookName;
    public URL bookURL;

    public BookInfo(String book, String filename) {
      String s = Resources.getHelpFilePath(filename);
      bookName = book;

      try {
        bookURL = new URL(s);
      } catch (java.net.MalformedURLException exc) {
        log.warning(
          "[HelpWindow] - Attempted to create a BookInfo with a bad URL: " +
          bookURL);
        bookURL = null;
      }
    }

    public String toString() {
      return bookName;
    }
  }
  
  /*
   * Enable use of hyperlinks in help documents.
   */
  private class Hyperactive implements HyperlinkListener {
 
         public void hyperlinkUpdate(HyperlinkEvent e) {
             if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                 JEditorPane pane = (JEditorPane) e.getSource();
                 if (e instanceof HTMLFrameHyperlinkEvent) {
                     HTMLFrameHyperlinkEvent  evt = (HTMLFrameHyperlinkEvent)e;
                     HTMLDocument doc = (HTMLDocument)pane.getDocument();
                     doc.processHTMLFrameHyperlinkEvent(evt);
                 } else {
                     try {
                         pane.setPage(e.getURL());
                     } catch (Throwable t) {
                         t.printStackTrace();
                     }
                 }
             }
         }
     }
  
} // end HelpWindow
