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

package utils;

import utils.jsyntax.JEditTextArea;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;

import java.io.*;

import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.basic.*;
import javax.swing.text.*;
import javax.swing.text.rtf.*;


public class Printer {

  /* The number of pages to be printed on*/
  int pages = 0;

  /**
   * A method to print the text on the output window
   *
   * @param jtarea the JTextArea where the text is displayed
   */
  public void printOut(JTextPane jtarea) {
    JEditTextArea jetarea1 = new JEditTextArea();

    String temp = jetarea1.getText();
    String temp1 = jtarea.getText();
    jetarea1.setText(temp1);
    print(jetarea1);
    jetarea1.setText(temp);
  }
  
  /**
   * A method to get the length of the longest line in the JEditTextArea
   *
   * @param jetarea the JEditTextArea where the text to be printed is displayed
   * @return max the length of the longest line
   */
  public int getLength(JEditTextArea jetarea) {
    int max = 0;

    for (int i = 0; i < jetarea.getLineCount(); i++) {
      int len = jetarea.getLineLength(i);

      if (len > max)
        max = len;
    }

    return max;
  }
  
  /**
   * A method to print the text in a JEditTextArea on several pages
   * 
   * @param newjetarea the JEditTextArea where the text is displayed
   */
  public void print(JEditTextArea newjetarea) {
    JEditTextArea jetarea = newjetarea;
    PrinterJob printJob = PrinterJob.getPrinterJob();
    Book book = new Book();
    PageFormat pf = printJob.defaultPage();
    pf = printJob.pageDialog(pf);

    int lineHeight = jetarea.getPainter().getFontMetrics().getHeight();
    int lineNum = (int) pf.getImageableHeight() / lineHeight;
    int num = jetarea.getLineCount();

    if ((num % lineNum) == 0)
      pages = num / lineNum;
    else
      pages = (num / lineNum) + 1;

    book.append(new OnePage(pages, jetarea, lineHeight, lineNum), pf, pages);
    printJob.setPageable(book);

    if (printJob.printDialog()) {
      try {
        printJob.print();
      } catch (Exception PrintException) {
        PrintException.printStackTrace();
      }
    }
  }
}


class OnePage extends JTextPane implements Printable {
  private final static int POINTS_PER_INCH = 72;
  /* the number of pages the text is printed on. */
  int pages = 0;
  /* the height of each line. */
  int lineHeight = 0;
  /* the length of the longest line in the text */
  int lineLen = 0;
  /* the number of lines per page. */
  int lineNum = 0;
  /* the area where the text to be printed is kept. */
  JEditTextArea jetarea;
  /* the font metrics of the text area. */
  FontMetrics fm;

  public OnePage(int num, JEditTextArea newjetarea, int lineH, int lineN) {
    pages = num;
    jetarea = newjetarea;
    lineHeight = lineH;
    lineNum = lineN;
    lineLen = getLength(newjetarea);
  }

  /**
   * A method to get the length of the longest line in the JEditTextArea
   *
   * @param jetarea the JEditTextArea where the text to be printed is displayed
   * @return max the length of the longest line
   */
  public int getLength(JEditTextArea jetarea) {
    int max = 0;

    for (int i = 0; i < jetarea.getLineCount(); i++) {
      int len = jetarea.getLineLength(i);

      if (len > max)
        max = len;
    }

    return max * 8;
  }

  /**
   * A method draw a page in preparation for printing
   *
   * @param jetarea the text area where the text is kept
   * @param page the page to be drawn
   * @param g2d the graphics in charge draw the page
   * @param the page format of the printing page
   */
  public void getOnePage(JEditTextArea jetarea, int page, Graphics2D g2d,
    PageFormat pf) {
    int line = page * lineNum;

    if ((line + lineNum) < jetarea.getLineCount()) {
      for (int i = 0; i < lineNum; i++) {
        g2d.drawString(jetarea.getLineText(i + line), 0, lineHeight * (i + 1));
      }
    } else {
      for (int i = 0; i < (jetarea.getLineCount() - line); i++) {
        g2d.drawString(jetarea.getLineText(i + line), 0, lineHeight * (i + 1));
      }
    }
  }

  public int print(Graphics g, PageFormat pageFormat, int page) {
    Graphics2D g2d = (Graphics2D) g;
    double scale = 1.0;
    g2d.setFont(jetarea.getPainter().getFontMetrics().getFont());
    g2d.setClip((int) (pageFormat.getImageableX()),
      (int) (pageFormat.getImageableY()), (int) lineLen,
      (int) pageFormat.getHeight());

    if (lineLen > pageFormat.getImageableWidth()) {
      scale = pageFormat.getImageableWidth() / lineLen;
      g2d.scale(scale, scale);
    }

    g2d.translate(g2d.getClipBounds().getX(), g2d.getClipBounds().getY());

    if (page < pages) {
      getOnePage(jetarea, page, g2d, pageFormat);

      return (PAGE_EXISTS);
    }

    return (NO_SUCH_PAGE);
  }
}
