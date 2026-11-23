package com.afzal.orangehrm.components;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/** The short confirmation message at the bottom left, e.g. "Successfully Saved". */
public final class Toast {

    private static final By MESSAGE = By.cssSelector(".oxd-toast .oxd-text--toast-message");

    private Toast() {
    }

    public static String waitForMessage(WebDriverWait wait) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(MESSAGE)).getText().trim();
    }
}
