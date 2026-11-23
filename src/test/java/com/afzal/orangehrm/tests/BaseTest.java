package com.afzal.orangehrm.tests;

import com.afzal.orangehrm.core.DriverFactory;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Every test gets a fresh browser, so tests never depend on each other.
 *
 * TestNG runs the methods of one class in parallel on the SAME test object, so
 * nothing per-test may live in a field: a field would be overwritten by the
 * other threads. The browser always comes from DriverFactory's ThreadLocal.
 */
public abstract class BaseTest {

    @BeforeMethod(alwaysRun = true)
    public void startBrowser() {
        DriverFactory.start();
    }

    @AfterMethod(alwaysRun = true)
    public void closeBrowser() {
        DriverFactory.quit();
    }

    protected WebDriver driver() {
        return DriverFactory.get();
    }
}
