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
 * @author Louis Whest
 *
 */

package view.windows;

import managers.ActionManager;
import managers.SettingsManager;
import managers.WindowManager;

import utils.Printer;
import utils.Resources;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;


public class PrintWindow {
  private WindowManager wm = WindowManager.getInstance();
  private JPanel jPanel0 = new JPanel();
  private JPanel jPanel1 = new JPanel();
  private JPanel jPanel2 = new JPanel();
  private JPanel jpMessage = new JPanel();
  private Icon iconCode = new ImageIcon();
  private Icon iconOutput = new ImageIcon();
  private BorderLayout borderLayout1 = new BorderLayout();
  private JButton jCode = new JButton();
  private JButton jOutput = new JButton();
  private JLabel jlDialogMessage = new JLabel();
  JFrame frame = new JFrame("Print");
  Printer printer = new Printer();

  public PrintWindow() {
    try {
      jbInit();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    iconCode = Resources.getIcon("printcode16");
    iconOutput = Resources.getIcon("printoutput16");

    jPanel0.setSize(new Dimension(300, 100));

    jPanel1.setLayout(borderLayout1);

    jCode.setText("Code");
    jCode.setPreferredSize(new Dimension(130, 25));
    jCode.setIcon(iconCode);
    jCode.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          jCode_actionPerformed(e);
        }
      });

    jOutput.setText("Output");
    jOutput.setPreferredSize(new Dimension(130, 25));
    jOutput.setIcon(iconOutput);
    jOutput.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          jOutput_actionPerformed(e);
        }
      });

    jlDialogMessage.setText("Please select which area you would like to print.");
    jpMessage.setSize(new Dimension(150, 21));
    jpMessage.add(jlDialogMessage, null);

    jPanel2.add(jCode, null);
    jPanel2.add(jOutput, null);
    jPanel1.add(jPanel2, BorderLayout.SOUTH);

    jPanel1.add(jpMessage, BorderLayout.NORTH);
    jPanel0.add(jPanel1, null);
  }

  public void show() {
    frame.getContentPane().add(jPanel0);
    frame.setSize(300, 100);
    frame.setLocationRelativeTo(wm.getMainScreenFrame());
    frame.setVisible(true);
  }

  private void jCode_actionPerformed(ActionEvent e) {
    close();
    printCode();
  }

  private void jOutput_actionPerformed(ActionEvent e) {
    close();
    printOutput();
  }

  public void printCode() {
    printer.print(wm.getEditorWindow().getTextPane());
  }

  public void printOutput() {
    printer.printOut(wm.getConsoleWindow().getTextArea());
  }

  public void close() {
    frame.dispose();
  }
}
