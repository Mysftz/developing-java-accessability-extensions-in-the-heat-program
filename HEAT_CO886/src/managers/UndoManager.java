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
 *
 */

package managers;

import managers.ActionManager;

/**
 * The manager Class responsible for providing undo and redo features
 *
 * @author Dean Ashton
 */
public class UndoManager extends javax.swing.undo.UndoManager {
  private static UndoManager instance = null;

  protected UndoManager() {
    /* prevent instantiation */
  }

  /**
   * Returns consistent instance of {@link javax.swing.undo.UndoManager}
   *
   * @return an instance of javax.swing.undo.UndoManager
   */
  public static UndoManager getInstance() {
    if (instance == null)
      instance = new UndoManager();

    return instance;
  }
  
  //clear the undo/redo manager
  public void reset() {
      getInstance().discardAllEdits();
      ActionManager am = ActionManager.getInstance();
      am.getUndoAction().updateUndoState();
      am.getRedoAction().updateRedoState();
      am.getToolbarUndoAction().updateUndoState();
      am.getToolbarRedoAction().updateRedoState();
  }
 
}
