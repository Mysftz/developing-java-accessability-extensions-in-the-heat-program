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

package view.windows;

import managers.WindowManager;

import utils.Search;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Displays a window to perform a search
 */
public class SearchDialog {
  private JPanel jPanel1 = new JPanel();
  private JTextField jTextField1 = new JTextField();
  private JLabel jLabel1 = new JLabel();
  private JCheckBox jCheckBox1 = new JCheckBox();
  private JButton jButton1 = new JButton();
  JFrame frame = new JFrame("Find");
  private int lastResult = 0;
  private boolean firstletter = true;
  WindowManager wm = WindowManager.getInstance();

  public SearchDialog() {
    try {
      jbInit();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Initialize the Search window
   * @throws Exception
   */
  private void jbInit() throws Exception {
    
    jPanel1.setSize(new Dimension(383, 72));
    jTextField1.setMinimumSize(new Dimension(6, 50));
    jTextField1.setSize(new Dimension(100, 20));
    jTextField1.setPreferredSize(new Dimension(150, 20));
    jTextField1.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          jTextField1_actionPerformed(e);
        }
      });
    /* Listens for key presses and makes button enabled */
    jTextField1.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent evt) {
       if(evt.getKeyCode() != 0) {
         if (jTextField1.getText().length() == 1 && evt.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
           jButton1.setEnabled(false);
           firstletter = true;
         }
         else if (jTextField1.getText().length() == 1 && evt.getKeyCode() == KeyEvent.VK_DELETE) {
           jButton1.setEnabled(false);
           firstletter = true;
         }
         else if (jTextField1.getText().equalsIgnoreCase("") && !firstletter) {
           jButton1.setEnabled(false);
           firstletter = true;
         }
         else {
           jButton1.setEnabled(true);
           firstletter = false;
         }
        }
      }
    });
    jLabel1.setText("Search for text:  ");
    jLabel1.setToolTipText("Enter search phrase here");
    jCheckBox1.setText("Case sensitive");
    jCheckBox1.setToolTipText("Tick for a case sensitive search");
    jButton1.setText("Find next");
    jButton1.setEnabled(false);
    jButton1.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          jButton1_actionPerformed(e);
        }
      });
    jPanel1.add(jLabel1, null);
    jPanel1.add(jTextField1, null);
    jPanel1.add(jCheckBox1, null);
    jPanel1.add(jButton1, null);
  }

  private void jTextField1_actionPerformed(ActionEvent e) {
    jButton1_actionPerformed(e);
  }

  /**
   * The find next button
   * @param e
   */
  private void jButton1_actionPerformed(ActionEvent e) {
    String text = jTextField1.getText();
    boolean caseSensitive = jCheckBox1.isSelected();

    if (text.equalsIgnoreCase("") ||
        text.equalsIgnoreCase("No search string entered"))
      jTextField1.setText("No search string entered");

    /* Perform search */
    else {
      int thisResult;
      String toBeSearched = wm.getEditorWindow().getEditorContent();

      if (caseSensitive) {
        thisResult = Search.searchCaseText(text, toBeSearched, lastResult);
        lastResult = thisResult;
      } else {
        thisResult = Search.searchNoCaseText(text, toBeSearched, lastResult);
        lastResult = thisResult;
      }
    }

    if (lastResult != -1)
      wm.getEditorWindow().getJTextPane().setCaretPosition(lastResult);
    else
      lastResult = 0;
  }

  public void show() {
    frame.getContentPane().add(jPanel1);
    frame.setSize(400, 100);
    frame.setResizable(false);
    frame.setLocationRelativeTo(wm.getMainScreenFrame());
    frame.setVisible(true);
  }
}
