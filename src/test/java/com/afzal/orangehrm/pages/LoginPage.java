package com.afzal.orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;

public class LoginPage extends BasePage {

    private static final By USERNAME = By.name("username");
    private static final By PASSWORD = By.name("password");
    private static final By LOGIN = By.cssSelector("button[type='submit']");
    private static final By ERROR = By.cssSelector(".oxd-alert-content-text");
    private static final By FIELD_ERRORS = By.cssSelector(".oxd-input-field-error-message");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage open() {
        open("/auth/login");
        visible(USERNAME);
        return this;
    }

    public DashboardPage loginAs(String username, String password) {
        submit(username, password);
        return new DashboardPage(driver).waitUntilLoaded();
    }

    /** Submits without expecting success, for negative tests. */
    public LoginPage submit(String username, String password) {
        type(USERNAME, username);
        type(PASSWORD, password);
        click(LOGIN);
        return this;
    }

    public String errorMessage() {
        return text(ERROR);
    }

    public List<String> fieldErrors() {
        visible(FIELD_ERRORS);
        return texts(FIELD_ERRORS);
    }

    /** Waits until the browser is on the login page, e.g. after a redirect. */
    public LoginPage waitUntilDisplayed() {
        waitForUrlContains("/auth/login");
        visible(LOGIN);
        return this;
    }

    public boolean isDisplayed() {
        return !driver.findElements(LOGIN).isEmpty() && driver.getCurrentUrl().contains("/auth/login");
    }
}
