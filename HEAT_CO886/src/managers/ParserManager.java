/**
 *
 * Copyright (c) 2006 University of Kent
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

package managers;

import utils.parser.*;
import managers.WindowManager;

/**
 *  Keeps a list (in form of Parser object) of up-to-date
 *  elements and types found in currently open document.
 *
 */
public class ParserManager
{
    private static ParserManager instance = null;
    private static Parser currentParser = new Parser("");
    
    protected ParserManager()
    {
        /* Exists to prevent instantiation */
    }
    
    /**
     *  Returns an instance of ParserManager
     *
     *  @return FileManager instance
     */
    public static ParserManager getInstance()
    {
        if (instance == null)
            instance = new ParserManager();

        return instance;
    }
    
    /**
     *  Reloads the parser with specified text
     *  Additionally updates TokenMarker for the DispayWindow
     *
     *  @param String inText
     */
    public static void refresh(String inText)
    {
        currentParser.reloadComponents(inText);
        //WindowManager.getInstance().getEditorWindow()
        //  .getTextPane().setTokenMarker(new HaskellTokenMarker()));  //(new HaskellTokenMarker(currentParser));
    }
    
    /**
     *  Reloads the parser with text from DisplayWindow
     *  Additionally updates TokenMarker for the DispayWindow
     */
    public static void refresh()
    {
        refresh(WindowManager.getInstance().getEditorWindow().getEditorContent());
    }
    
    /**
     *  Returns up-to-date Parser object
     *
     *  @return Parser currentParser
     */
    public static Parser getParser()
    {
        return currentParser;
    }   
    
    
}
