package org.shyam.extract;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {

    private final Properties properties;
    private static final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);

    public ConfigLoader() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                logger.warn("Sorry, unable to find config.properties");
                return;
            }
            properties.load(input);
        } catch (IOException ex) {
            logger.error("Error loading properties file", ex);
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
