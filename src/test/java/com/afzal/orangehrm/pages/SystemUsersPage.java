package com.afzal.orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;

/** Admin > User Management > Users. */
public class SystemUsersPage extends BasePage {

    private static final By USERNAME = inputByLabel("Username");
    private static final By SEARCH = By.cssSelector("button[type='submit']");
    private static final By ROWS = By.cssSelector(".oxd-table-body .oxd-table-card");
    private static final By RECORD_COUNT = By.cssSelector(".orangehrm-horizontal-padding .oxd-text--span");

    public SystemUsersPage(WebDriver driver) {
        super(driver);
    }

    public SystemUsersPage open() {
        open("/admin/viewSystemUsers");
        visible(SEARCH);
        return this;
    }

    public SystemUsersPage searchByUsername(String username) {
        type(USERNAME, username);
        searchAndWaitForNewResults(SEARCH, RECORD_COUNT);
        return this;
    }

    /** Picks a value in the "User Role" dropdown (a custom select, not a native one). */
    public SystemUsersPage filterByRole(String role) {
        click(By.xpath("//label[normalize-space()='User Role']/ancestor::div[contains(concat(' ',normalize-space(@class),' '),' oxd-input-group ')][1]"
                + "//div[contains(@class,'oxd-select-text')]"));
        click(By.xpath("//div[@role='listbox']//span[normalize-space()='" + role + "']"));
        searchAndWaitForNewResults(SEARCH, RECORD_COUNT);
        return this;
    }

    public String recordCount() {
        return text(RECORD_COUNT);
    }

    /** Each row: [checkbox, Username, User Role, Employee Name, Status, Actions]. */
    public List<List<String>> rows() {
        return tableRows(ROWS, RECORD_COUNT);
    }
}
