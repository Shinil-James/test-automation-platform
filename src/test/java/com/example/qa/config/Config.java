package com.example.qa.config;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

/**
 * Central place for every tunable value in the suite.
 *
 * Resolution order, highest wins:
 *   1. A JVM system property  (-Dbrowser=firefox)
 *   2. config.properties on the test classpath
 *   3. The hard-coded fallback passed to get(...)
 *
 * That ordering is what lets CI override settings without editing files.
 */
public final class Config {

    private static final Properties PROPS = load();

    private Config() {
        // utility class
    }

    private static Properties load() {
        Properties props = new Properties();
        try (InputStream in = Config.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not read config.properties", e);
        }
        return props;
    }

    private static String get(String key, String fallback) {
        String fromSystem = System.getProperty(key);
        if (fromSystem != null && !fromSystem.isBlank()) {
            return fromSystem.trim();
        }
        return PROPS.getProperty(key, fallback).trim();
    }

    public static String baseUrl() {
        return get("baseUrl", "https://www.selenium.dev/selenium/web");
    }

    public static String browser() {
        return get("browser", "chrome").toLowerCase();
    }

    public static boolean headless() {
        return Boolean.parseBoolean(get("headless", "false"));
    }

    public static Duration explicitWait() {
        return Duration.ofSeconds(Long.parseLong(get("explicitWaitSeconds", "10")));
    }

    public static Duration pageLoadTimeout() {
        return Duration.ofSeconds(Long.parseLong(get("pageLoadTimeoutSeconds", "30")));
    }
}
