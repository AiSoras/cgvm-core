package ru.etu.cgvm.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public final class SettingManager {

    private final Properties properties = new Properties();

    private SettingManager() {
        loadProperties("settings.properties");
    }

    private static SettingManager instance;

    private static SettingManager getInstance() {
        if (instance == null) {
            instance = new SettingManager();
        }
        return instance;
    }

    private void loadProperties(final String resourceName) {
        InputStream inStream = this.getClass().getClassLoader().getResourceAsStream(resourceName);
        if (inStream != null) {
            try {
                properties.load(inStream);
                inStream.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        } else {
            log.warn(String.format("Resource \"%1$s\" could not be found", resourceName));
        }
    }

    public static String getProperty(final String key) {
        return System.getProperty(key, getInstance().properties.getProperty(key));
    }

    public static String getProperty(final String key, final String defaultValue) {
        return System.getProperty(key, getInstance().properties.getProperty(key, defaultValue));
    }

    public static void setProperty(final String key, final String value) {
        getInstance().setProperty(key, value);
    }
}