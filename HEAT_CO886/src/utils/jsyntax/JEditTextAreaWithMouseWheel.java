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

package utils.jsyntax;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;


public class JEditTextAreaWithMouseWheel extends JEditTextArea
  implements MouseWheelListener {
  private int mouseWheelUnit = 3;

  public JEditTextAreaWithMouseWheel() {
    super();
    addMouseWheelListener(this);
  }

  public JEditTextAreaWithMouseWheel(TextAreaDefaults textAreaDefaults) {
    super(textAreaDefaults);
    addMouseWheelListener(this);
  }

  public int getMouseWheelUnit() {
    return mouseWheelUnit;
  }

  public void setMouseWheelUnit(int mouseWheelUnit) {
    this.mouseWheelUnit = mouseWheelUnit;
  }

  public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
    if (mouseWheelEvent.getScrollAmount() == 0)
      return;

    vertical.setValue(vertical.getValue() +
      (mouseWheelUnit * mouseWheelEvent.getWheelRotation()));
  }
}
