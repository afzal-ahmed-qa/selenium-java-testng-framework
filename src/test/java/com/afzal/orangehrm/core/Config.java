package com.afzal.orangehrm.core;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

/**
 * Settings from config.properties. A system property with the same name wins,
 * so CI can run "mvn test -Dbrowser=firefox" without editing files.
 */
public final class Config {

    private static final Properties PROPS = load();

    private Config() {
    }

    private static Properties load() {
        Properties props = new Properties();
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in == null) {
                throw new IllegalStateException("config.properties not found on the test classpath");
            }
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read config.properties", e);
        }
        return props;
    }

    public static String get(String key) {
        String fromSystem = System.getProperty(key);
        if (fromSystem != null && !fromSystem.isBlank()) {
            return fromSystem.trim();
        }
        String value = PROPS.getProperty(key);
        if (value == null) {
            throw new IllegalStateException("Missing setting: " + key);
        }
        return value.trim();
    }

    public static String baseUrl() {
        return get("baseUrl");
    }

    public static String browser() {
        return get("browser").toLowerCase();
    }

    public static boolean headless() {
        return Boolean.parseBoolean(get("headless"));
    }

    public static Duration explicitWait() {
        return Duration.ofSeconds(Long.parseLong(get("explicitWaitSeconds")));
    }

    public static Duration pageLoadTimeout() {
        return Duration.ofSeconds(Long.parseLong(get("pageLoadTimeoutSeconds")));
    }

    public static String adminUser() {
        return get("adminUser");
    }

    public static String adminPassword() {
        return get("adminPassword");
    }
}
