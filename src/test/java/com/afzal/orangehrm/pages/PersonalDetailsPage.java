package com.afzal.orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** An employee's profile, opened after saving a new employee. */
public class PersonalDetailsPage extends BasePage {

    private static final By FULL_NAME = By.cssSelector(".orangehrm-edit-employee-name h6");
    private static final Pattern EMP_NUMBER = Pattern.compile("/empNumber/(\\d+)");

    public PersonalDetailsPage(WebDriver driver) {
        super(driver);
    }

    public PersonalDetailsPage waitUntilLoaded() {
        waitForUrlContains("/pim/viewPersonalDetails/empNumber/");
        waitForSpinner();
        // The header fills in after the profile data arrives.
        wait.until(d -> !d.findElement(FULL_NAME).getText().isBlank());
        return this;
    }

    public String fullName() {
        return text(FULL_NAME);
    }

    /** OrangeHRM's internal id, used by the API to delete the employee again. */
    public int employeeNumber() {
        Matcher m = EMP_NUMBER.matcher(driver.getCurrentUrl());
        if (!m.find()) {
            throw new IllegalStateException("No employee number in " + driver.getCurrentUrl());
        }
        return Integer.parseInt(m.group(1));
    }
}
