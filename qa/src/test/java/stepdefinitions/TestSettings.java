package stepdefinitions;

import java.io.IOException;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class TestSettings {
    private static TestSettings testSettings;
    private final Properties properties;

    private static final Logger logger = Logger.getLogger(TestSettings.class.getName());

    private TestSettings(Properties properties) {
        this.properties = properties;
    }

    public static synchronized TestSettings getInstance() {
        if (testSettings == null) {
            Properties properties = new Properties();
            try {
                properties.load(TestSettings.class.getClassLoader().getResourceAsStream("application-test.properties"));
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error loading application-test.properties file", e);
                throw new RuntimeException("Failed to load application-test.properties", e);
            }

            testSettings = new TestSettings(properties);

            logger.info("Using properties:");
            for (Entry<Object, Object> entry : testSettings.properties.entrySet()) {
                logger.info(entry.getKey() + " = " + entry.getValue());
            }
        }

        return testSettings;
    }

    public String getProperty(String key) {
        String envValue = System.getenv(toEnvKeyName(key));
        if (envValue != null) {
            logger.fine("Using environment variable for key " + key + ": " + envValue);
            return envValue;
        }

        String propertyValue = properties.getProperty(key);
        if (propertyValue != null) {
            logger.fine("Using property value for key " + key + ": " + propertyValue);
            return propertyValue;
        }

        logger.warning("No value found for key: " + key);
        return null;
    }

    private String toEnvKeyName(String key) {
        return key.toUpperCase().replace("-", "_").replace(".", "_");
    }
}