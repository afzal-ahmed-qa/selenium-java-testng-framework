package com.afzal.orangehrm.pages;

import com.afzal.orangehrm.components.Toast;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class EmployeeListPage extends BasePage {

    private static final By EMPLOYEE_ID = inputByLabel("Employee Id");
    private static final By SEARCH = By.cssSelector("button[type='submit']");
    private static final By ROWS = By.cssSelector(".oxd-table-body .oxd-table-card");
    private static final By RECORD_COUNT = By.cssSelector(".orangehrm-horizontal-padding .oxd-text--span");
    private static final By CONFIRM_DELETE = buttonByText("Yes, Delete");

    public EmployeeListPage(WebDriver driver) {
        super(driver);
    }

    public EmployeeListPage open() {
        open("/pim/viewEmployeeList");
        visible(SEARCH);
        return this;
    }

    /**
     * Searches by Employee Id: a plain text field, unlike the name field,
     * which only works through its autocomplete list.
     */
    public EmployeeListPage searchById(String employeeId) {
        type(EMPLOYEE_ID, employeeId);
        searchAndWaitForNewResults(SEARCH, RECORD_COUNT);
        return this;
    }

    /** "(1) Record Found", "No Records Found", ... */
    public String recordCount() {
        return text(RECORD_COUNT);
    }

    public List<List<String>> rows() {
        return tableRows(ROWS, RECORD_COUNT);
    }

    /** Deletes the first row through the UI: trash icon, then the confirmation dialog. */
    public String deleteFirstRow() {
        rows(); // waits until the result rows are drawn
        // Look the row up and click in one stable step: the table may re-render in between.
        stable(d -> {
            d.findElement(ROWS).findElement(By.cssSelector("button i.bi-trash")).click();
            return true;
        });
        wait.until(ExpectedConditions.elementToBeClickable(CONFIRM_DELETE)).click();
        return Toast.waitForMessage(wait);
    }
}
