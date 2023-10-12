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

package managers;

import java.util.logging.Logger;
import utils.Settings;
import java.io.*;
import java.util.Properties;
import javax.swing.JOptionPane;

/**
 * The manager Class responsible for creating, loading and saving settings 
 */
public class SettingsManager {
  
  private static Logger log = Logger.getLogger("heat");
  private static SettingsManager instance = null;
  private String SETTINGS_PATH = System.getProperty("user.home") +
    System.getProperty("file.separator");
  private String SETTINGS_FILE = "heat.settings";

  /* Properties file containing settings */
  private Properties heatSettings = new Properties();
  private File settingsFile;
  private boolean newSettingsFile = false;
  private boolean haveChanges = false;

  protected SettingsManager() {
    /* Exists to prevent instantiation */
  }

  public static SettingsManager getInstance() {
    if (instance == null)
      instance = new SettingsManager();
    return instance;
  }
  
  /**
   * Loads the settings file. If this does not yet exist it will create one 
   * based on the default settings.
   */
  public void loadSettings() {
    InputStream settingsFileStream;
    try {
      settingsFile = new File(SETTINGS_PATH + SETTINGS_FILE);
      /* File exists use that */
      if (settingsFile.exists()) {
        settingsFileStream = new FileInputStream(settingsFile);
        heatSettings.load(settingsFileStream);
        Properties defaultSettings = createDefaultProperties();
        /* Check that interpreter path exists */
        if (heatSettings.getProperty(Settings.INTERPRETER_PATH)==null) {
            newSettingsFile = true;
        }
        /* Check all default settings exist */
        java.util.Enumeration e = defaultSettings.propertyNames();
        while (e.hasMoreElements()) {
          String name = (String) e.nextElement();
          if (heatSettings.getProperty(name) == null) {
            String value = getDefault(name);
            heatSettings.setProperty(name, value);
          }
        }
        settingsFileStream.close();
      }
      /* File does not exist, create default */
      else {
        log.warning("[SettingsManager] - No settings file (" +
          settingsFile.getAbsolutePath() + ") found");
        heatSettings = createDefaultProperties();
        newSettingsFile = true;
      }
    } catch (IOException ioe) {
      log.warning("[SettingsManager] - No settings file found");
      newSettingsFile = true;
    }
  }
  
  /**
   * Creates the default settings for HEAT
   * @return Properties with the default settings
   */
  private Properties createDefaultProperties() {
    Properties newSettings = new Properties();
    newSettings.setProperty(Settings.INTERPRETER_PATH,
      "C:\\Program Files\\Hugs98\\hugs.exe");
    newSettings.setProperty(Settings.LIBRARY_PATH,
      System.getProperty("user.home"));
    newSettings.setProperty(Settings.OUTPUT_FONT_SIZE, "12");
    newSettings.setProperty(Settings.CODE_FONT_SIZE, "14");
    newSettings.setProperty(Settings.INTERPRETER_OPTS,"");
    newSettings.setProperty(Settings.TEST_FUNCTION,"");
    newSettings.setProperty(Settings.TEST_POSITIVE,"True");
    return newSettings;
  }
  
  /**
   * Get the default setting based on given key
   * @param s The key to check for
   * @return The default value, or null if not found
   */
  public String getDefault(String s) {
    return createDefaultProperties().getProperty(s);
  }
  
  /**
   * Writes the settings to the settings file
   */
  public void saveSettings() {
    OutputStream settingsFile;
    try {
      settingsFile = new FileOutputStream(SETTINGS_PATH + SETTINGS_FILE);
      heatSettings.store(settingsFile, "HEAT SETTINGS");
      settingsFile.close();
      JOptionPane.showMessageDialog(WindowManager.getInstance()
                                                 .getMainScreenFrame(),
        "Settings Saved", "Saved", JOptionPane.INFORMATION_MESSAGE);
    } catch (IOException ioe) {
      JOptionPane.showMessageDialog(WindowManager.getInstance()
                                                 .getMainScreenFrame(),
        "Error saving settings file", "Error", JOptionPane.ERROR_MESSAGE);
      log.warning("[SettingsManager] - IO Exception when writing settings file");
    }
  }
  
  /**
   * Get a setting
   * @param name The key of the setting to get
   * @return The value for that key
   */
  public String getSetting(String name) {
    return heatSettings.getProperty(name);
  }
  
  /**
   * Set the given setting key with the given value
   * @param key The setting to set
   * @param value The value to store
   */
  public void setSetting(String key, String value) {
    setHaveChanges(true);
    heatSettings.setProperty(key, value);
  }
  
  /**
   * Return true if a new setting file was created
   * @return True if new file created
   */
  public boolean isNewSettingsFile() {
    return newSettingsFile;
  }

  public void setHaveChanges(boolean haveChanges) {
	this.haveChanges = haveChanges;
  }

  public boolean isHaveChanges() {
	return haveChanges;
  }
}
