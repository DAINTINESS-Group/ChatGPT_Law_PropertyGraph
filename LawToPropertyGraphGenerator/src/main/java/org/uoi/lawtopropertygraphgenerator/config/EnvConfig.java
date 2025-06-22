package org.uoi.lawtopropertygraphgenerator.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EnvConfig {

    private static final Properties properties = new Properties();

    static {
        try (InputStream input = EnvConfig.class.getClassLoader().getResourceAsStream("env.properties")) {
            if (input == null) {
                throw new IOException("Unable to find env.properties in the resources folder.");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load environment properties", e);
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}
