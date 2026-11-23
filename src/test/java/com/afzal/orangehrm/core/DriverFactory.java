package com.afzal.orangehrm.core;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

/**
 * One browser per test thread. TestNG runs test methods in parallel, so the
 * driver lives in a ThreadLocal: each thread only ever sees its own browser.
 * Selenium Manager (built into Selenium 4) downloads the matching driver.
 */
public final class DriverFactory {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverFactory() {
    }

    public static WebDriver start() {
        WebDriver driver = switch (Config.browser()) {
            case "chrome" -> new ChromeDriver(chromeOptions());
            case "firefox" -> new FirefoxDriver(firefoxOptions());
            default -> throw new IllegalArgumentException("Unsupported browser: " + Config.browser());
        };
        driver.manage().window().setSize(new Dimension(1440, 900));
        driver.manage().timeouts().pageLoadTimeout(Config.pageLoadTimeout());
        // Implicit waits stay at zero: mixing them with explicit waits makes timing unpredictable.
        DRIVER.set(driver);
        return driver;
    }

    public static WebDriver get() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException("No browser started for this thread");
        }
        return driver;
    }

    public static boolean isStarted() {
        return DRIVER.get() != null;
    }

    public static void quit() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            try {
                driver.quit();
            } finally {
                DRIVER.remove();
            }
        }
    }

    private static ChromeOptions chromeOptions() {
        ChromeOptions options = new ChromeOptions();
        if (Config.headless()) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--disable-dev-shm-usage", "--no-sandbox", "--lang=en-US");
        return options;
    }

    private static FirefoxOptions firefoxOptions() {
        FirefoxOptions options = new FirefoxOptions();
        if (Config.headless()) {
            options.addArguments("-headless");
        }
        options.addPreference("intl.accept_languages", "en-US");
        return options;
    }
}
