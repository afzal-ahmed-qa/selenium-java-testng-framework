package com.afzal.orangehrm.pages;

import com.afzal.orangehrm.components.Toast;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;

public class AddEmployeePage extends BasePage {

    private static final By FIRST_NAME = By.name("firstName");
    private static final By MIDDLE_NAME = By.name("middleName");
    private static final By LAST_NAME = By.name("lastName");
    private static final By EMPLOYEE_ID = inputByLabel("Employee Id");
    private static final By SAVE = By.cssSelector("button[type='submit']");
    private static final By FIELD_ERRORS = By.cssSelector(".oxd-input-field-error-message");

    public AddEmployeePage(WebDriver driver) {
        super(driver);
    }

    public AddEmployeePage open() {
        open("/pim/addEmployee");
        visible(FIRST_NAME);
        return this;
    }

    public AddEmployeePage fill(String firstName, String middleName, String lastName, String employeeId) {
        type(FIRST_NAME, firstName);
        type(MIDDLE_NAME, middleName);
        type(LAST_NAME, lastName);
        type(EMPLOYEE_ID, employeeId);
        return this;
    }

    /** Saves and returns the new employee's profile; its URL carries the employee number. */
    public PersonalDetailsPage save() {
        click(SAVE);
        String toast = Toast.waitForMessage(wait);
        if (!toast.contains("Successfully Saved")) {
            throw new IllegalStateException("Saving the employee showed: " + toast);
        }
        return new PersonalDetailsPage(driver).waitUntilLoaded();
    }

    public AddEmployeePage saveExpectingErrors() {
        click(SAVE);
        visible(FIELD_ERRORS);
        return this;
    }

    public List<String> fieldErrors() {
        return texts(FIELD_ERRORS);
    }

    public String errorUnder(String fieldName) {
        By error = By.xpath("//input[@name='" + fieldName + "']"
                + "/ancestor::div[contains(concat(' ',normalize-space(@class),' '),' oxd-input-group ')][1]//span[contains(@class,'oxd-input-field-error-message')]");
        return text(error);
    }
}
