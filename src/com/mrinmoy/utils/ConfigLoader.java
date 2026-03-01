package com.mrinmoy.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final Properties properties = new Properties();
    private static final String CONFIG_FILE = "config.properties";

    // Static block runs once when the class is loaded
    static {
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
        } catch (IOException ex) {
            System.err.println("⚠️ Error: Could not find " + CONFIG_FILE);
            // Fallback for IDEs/JARs: try loading from resources
            try (InputStream res = ConfigLoader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
                if (res != null) properties.load(res);
            } catch (Exception ignored) {
            }
        }
    }

    public static String getApiUrl() {
        return properties.getProperty("github.api.url");
    }

    public static String getToken() {
        return properties.getProperty("github.token");
    }
}