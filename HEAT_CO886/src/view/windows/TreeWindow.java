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
 * @author Sergei Krot
 *
 */

package view.windows;

import utils.parser.*;
import utils.Resources;
import managers.ActionManager;
import managers.WindowManager;
import managers.ParserManager;

import java.util.ArrayList;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeModel;
import javax.swing.tree.DefaultTreeCellRenderer;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.JPopupMenu;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.BoxLayout;
import javax.swing.ToolTipManager;

public class TreeWindow
{
    private JPanel treePanel;
    private JButton collapseButton;
    private JButton expandButton;
    private JScrollPane treeScrollPane;
    private JButton refreshButton;
    private JToolBar toolbar;
    private JTree tree;
    private JPopupMenu popMenu;
    private JMenuItem jMenuItemGoTo;
    private JMenu jMenuTests;
    private ActionManager am;

    private TreePath path;
    
    private int treeChildProperties = 0;
    private int treeChildFunctions = 1;
    private int treeChildAlgebraicTypes = 2;
    private int treeChildTypeSynonyms = 3;
    
    /** Creates new form TreeWindow */
    public TreeWindow()
    {
        try
        {
             initComponents();
        }
        catch (Exception e)
        {
             e.printStackTrace();
        }
    }

    private void initComponents()
    {
        am = ActionManager.getInstance();
        treePanel = new JPanel();
        toolbar = new JToolBar();
        expandButton = new JButton(am.getExpandTreeAction());
        collapseButton = new JButton(am.getCollapseTreeAction());
        refreshButton = new JButton(am.getRefreshTreeAction());
        treeScrollPane = new JScrollPane();

        treePanel.setMinimumSize(new Dimension(0, 10));
        treePanel.setPreferredSize(new Dimension(150, 10));

        // TREE
        DefaultMutableTreeNode top = new DefaultMutableTreeNode("Summary");
    	createNodes(top);
    	tree = new JTree(top);
        tree.setRowHeight(18);
        tree.setRootVisible(false);
        ToolTipManager.sharedInstance().registerComponent(tree);
        tree.setCellRenderer(new MyRenderer());

        /*
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setOpenIcon(Resources.getIcon("fileopen16"));
        renderer.setClosedIcon(Resources.getIcon("fileopen16"));
        renderer.setLeafIcon(Resources.getIcon("effect16"));
        tree.setCellRenderer(renderer);
        */
        //  popups
        popMenu = new JPopupMenu();
        jMenuItemGoTo = new JMenuItem("Go to definition");
        //jMenuItemDebug = new JMenuItem("Debug");
        jMenuTests = new JMenu("Run Tests");
        tree.add(popMenu);
        popMenu.add(jMenuItemGoTo);
        //popMenu.add(jMenuItemDebug);
        popMenu.add(jMenuTests);
        jMenuTests.setEnabled(false);
        jMenuItemGoTo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                goGetLine(path);}});


        dealWithMouseActions();

        // TOOLBAR
        toolbar.setFloatable(false);

        //  add buttons to the toolbar
        toolbar.add(expandButton);
        toolbar.add(collapseButton);
        toolbar.add(refreshButton);

        //  add tree to the scrollpane
        treeScrollPane.setViewportView(tree);

        //  make them layout nicely
        treePanel.setLayout(new BoxLayout(treePanel, BoxLayout.Y_AXIS));
        toolbar.setAlignmentX(Component.LEFT_ALIGNMENT);
        treeScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        treePanel.add(toolbar);
        treePanel.add(treeScrollPane);
    }

    public JPanel getWindowPanel()
    {
        return treePanel;
    }

    private void createNodes(DefaultMutableTreeNode top)
    {
        top.add(new DefaultMutableTreeNode("Properties"));
        top.add(new DefaultMutableTreeNode("Functions / constants"));
        top.add(new DefaultMutableTreeNode("Algebraic data types"));
        top.add(new DefaultMutableTreeNode("Type synonyms"));
    }
    
    /**
     * Repaint properties of tree as soon as possible
     *
     */
    public void repaintProperties() {
    	tree.repaint();
    }

    /**
     *  Reloads the tree
     */
    public void refreshTree()
    {
    	java.util.logging.Logger.getLogger("heat").warning("refresh tree");
        update(ParserManager.getParser());
    }

    public void update(Parser parser)
    {
        updateFunctions(parser.getElements());
        updateDatatypes(parser.getDataTypes());
        updateTypes(parser.getTypes());
        updateProps(parser.getTests());
        this.tree.updateUI();
    }
    
    private void updateProps(ArrayList tests){
      TreeModel tree = this.tree.getModel();
      DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getRoot();
      DefaultMutableTreeNode testsNode = (DefaultMutableTreeNode) root.getChildAt(treeChildProperties);
      testsNode.removeAllChildren();
      for (int i=0; i<tests.size(); i++ ){
        ParsedTest test=(ParsedTest)tests.get(i);
        testsNode.add(new DefaultMutableTreeNode(test));
      }
    }
    
    private void updateFunctions(ArrayList elements)
    {
        TreeModel tree = this.tree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getRoot();
        DefaultMutableTreeNode elementsNode = (DefaultMutableTreeNode) root.getChildAt(treeChildFunctions);
        elementsNode.removeAllChildren();

        for (int i=0 ; i<elements.size(); i++ )
        {
            ParsedFunction element = (ParsedFunction) elements.get(i);
            elementsNode.add(new DefaultMutableTreeNode (element));
        }
    }


    private void updateDatatypes(ArrayList datatypes)
    {
        TreeModel tree = this.tree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getRoot();

        DefaultMutableTreeNode datatypesNode = (DefaultMutableTreeNode) root.getChildAt(treeChildAlgebraicTypes);
        datatypesNode.removeAllChildren();

        for (int i=0 ; i<datatypes.size(); i++ )
        {
            ParsedDatatype datatype = (ParsedDatatype) datatypes.get(i);
            datatypesNode.add(new DefaultMutableTreeNode (datatype));
        }
    }

    private void updateTypes(ArrayList types)
    {
        TreeModel tree = this.tree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getRoot();

        DefaultMutableTreeNode typesNode = (DefaultMutableTreeNode) root.getChildAt(treeChildTypeSynonyms);
        typesNode.removeAllChildren();

        for (int i=0 ; i<types.size(); i++ )
        {
            ParsedType type = (ParsedType) types.get(i);
            typesNode.add(new DefaultMutableTreeNode (type));
        }
    }

    /**
     *  Expands all branches of the tree
     */
    public void expandTree()
    {
        if (tree.isRootVisible())
            expandAll(tree.getPathForRow(0));
        else
        {
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
            expandAll(new TreePath(root.getPath()));
        }
    }

    /**
     *  A recursive "helping" function for expandTree()
     */
    private void expandAll(TreePath path)
    {
        Object node = path.getLastPathComponent();
        TreeModel model = tree.getModel();

        if (model.isLeaf(node))
            return;

        int num = model.getChildCount(node);

        for (int i = 0; i < num; i++)
        {
            expandAll(path.pathByAddingChild(model.getChild(node, i)));
        }

        tree.expandPath(path);

    }

    /**
     *  Collaps all branches of  the tree
     */
    public void collapseTree()
    {
        if (tree.isRootVisible())
        {
            collapseAll(tree.getPathForRow(0));
            tree.expandRow(0);
        }
        else
        {
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
            collapseAll(new TreePath(root.getPath()));
            tree.expandPath(new TreePath(root.getPath()));
        }
    }

    /**
     *  Recursive private function for "helping" collapseTree()
     */
    private void collapseAll(TreePath path)
    {
        Object node = path.getLastPathComponent();
        TreeModel model = tree.getModel();

        if (model.isLeaf(node))
            return;

        int num = model.getChildCount(node);
        //System.out.println(num);

        for (int i = 0; i < num; i++)
        {
            collapseAll(path.pathByAddingChild(model.getChild(node, i)));
        }

        tree.collapsePath(path);

    }

    public void goGetLine(TreePath selPath)
    {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
        getObjectDefinitionLocation(node);
    }

    public void runTest()
    {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        if (node.isLeaf() && path.getPathComponent(1)=="Properties")
        {
            ParsedFunction element = (ParsedFunction)node.getUserObject();
            ArrayList tests = element.getTests();
            ParsedTest test = (ParsedTest)tests.get(0);
            WindowManager.getInstance().getConsoleWindow().runTest(test);
        }
    }


    private void getObjectDefinitionLocation(DefaultMutableTreeNode node)
    {
            ParsedComponent component = getSelectedObject(node);
            if(component != null)
                WindowManager.getInstance().getEditorWindow().focusLine(component.getLocation()+1);
    }

    private ParsedComponent getSelectedObject(DefaultMutableTreeNode node)
    {
        if (node == null)
            return null;

        Object nodeInfo = node.getUserObject();
        return (ParsedComponent)nodeInfo;
    }

    private void dealWithMouseActions()
    {
        MouseListener ml = new MouseAdapter()
        {
            private ArrayList tests;
            public void mousePressed(MouseEvent e)
            {
                int selRow = tree.getRowForLocation(e.getX(), e.getY());
                TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());

                if(selRow != -1)
                {
                    Object object = ((DefaultMutableTreeNode)selPath.getLastPathComponent()).getUserObject();
                    if (e.getClickCount() == 1)
                    {
                      if (e.getButton() == MouseEvent.BUTTON3)
                      {
                        tree.setSelectionPath(selPath);
                        if (!(object.getClass().getName().equals("java.lang.String")))
                        {
                          if ((object.getClass().getName().equals("utils.parser.ParsedFunction")))
                          {
                            if (((ParsedFunction)object).hasTests())
                            {
//                              jMenuTests.removeAll();
//                              jMenuTests.setEnabled(true);
//                              tests = ((ParsedFunction)object).getTests();
//                              for (int i=0; i<tests.size(); i++)
//                                jMenuTests.add(new JMenuItem(am.getRunTestAction(
//                                  ((ParsedTest)tests.get(i)).getName(),(ParsedTest) tests.get(i))));
                            }
                            else
                            {
                              jMenuTests.setEnabled(false);
                              jMenuTests.removeAll();
                             }
                           }
                           path = selPath;
                        } 
                      }
                    }
                    else if(e.getClickCount() == 2)
                    {
                      if (e.getButton() == MouseEvent.BUTTON1){
                        if (!(object.getClass().getName().equals("java.lang.String")))
                          goGetLine(selPath);
                      }	
                    }
                }
               
            }
        };
        tree.addMouseListener(ml);
    }

    class MyRenderer extends DefaultTreeCellRenderer
    {
        public MyRenderer() {
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
        {
          ArrayList tests;
          super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
          DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
          Object object = node.getUserObject();
          if (isNotComponent(object))
          {
             if (leaf)
               setIcon(Resources.getIcon("tree_folder_16"));
             else
             {
               setClosedIcon(Resources.getIcon("tree_folder_full_16"));
               setOpenIcon(Resources.getIcon("fileopen16"));
             }
             setToolTipText(null);
          }
          else{
            if (node.getParent().toString().matches("Properties")){
              tests=ParserManager.getInstance().getParser().getTests();
              ParsedTest test=(ParsedTest)object;
              setIcon(Resources.getIcon(((ParsedTest)tests
              	.get(tests.indexOf(test))).getState()));
            }
            else
              setIcon(Resources.getIcon("module16"));
          }
          if (leaf && isParsedType(object)) {
            ParsedType type = (ParsedType)object;
            setToolTipText(type.getName() + " = " + type.getValue());
          }
          if (leaf && isParsedDatatype(object)) {
            ParsedDatatype datatype = (ParsedDatatype)object;
            setToolTipText(datatype.getName() + " = " + datatype.getValue());
          }
          if (leaf && isParsedElement(object)) {
            ParsedFunction element = (ParsedFunction)object;
            String display = "";
            String parameters[] = element.getValue();
            for (int i = 0; i < parameters.length-1; i++){
              display = display.concat((String)parameters[i] + " -> ");
            }
            display = display.concat(parameters[parameters.length-1]);
            setToolTipText(element.getName() + " :: " + display);
          }
          return this;
        }

        protected boolean isNotComponent(Object object) {
            if((object.getClass().getName().equals("java.lang.String"))) {
                return true;
            }
            return false;
        }
        protected boolean isParsedType(Object object) {
            if((object.getClass().getName().equals("utils.parser.ParsedType"))) {
                return true;
            }
            return false;
        }
        protected boolean isParsedDatatype(Object object) {
             if((object.getClass().getName().equals("utils.parser.ParsedDatatype"))) {
                return true;
            }
            return false;
        }
        protected boolean isParsedElement(Object object) {
          if ((object.getClass().getName().equals("utils.parser.ParsedFunction"))) {
            return true;
          }
          return false;
        }
    }
    
    /**
     * Runs all the tests that are in the Properties tree node
     * 
     */
    public void runTests(){
      ConsoleWindow cw = WindowManager.getInstance().getConsoleWindow();
      cw.readyToReceiveTestResults();
      TreeModel tree = this.tree.getModel();
      DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getRoot();
      DefaultMutableTreeNode testsNode = (DefaultMutableTreeNode) root.getChildAt(treeChildProperties);
      if (!testsNode.isLeaf())
        for (int i=0;i<testsNode.getLeafCount();i++){
    	  ParsedTest test = (ParsedTest) ((DefaultMutableTreeNode) testsNode
    			              .getChildAt(i)).getUserObject();
    	  cw.runTest(test); 
        }
    }

}
