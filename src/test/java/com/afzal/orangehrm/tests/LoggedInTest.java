package com.afzal.orangehrm.tests;

import com.afzal.orangehrm.api.OrangeHrmApi;
import com.afzal.orangehrm.core.Config;
import com.afzal.orangehrm.core.DriverFactory;
import com.afzal.orangehrm.pages.DashboardPage;
import com.afzal.orangehrm.pages.LoginPage;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * Starts each test logged in as the admin, and deletes any employee the test
 * registered with {@link #cleanUpEmployee(int)} afterwards, through the API.
 * The demo is shared by many people, so tests must not leave data behind.
 */
public abstract class LoggedInTest extends BaseTest {

    /** Per thread, for the same reason as the browser (see BaseTest). */
    private static final ThreadLocal<List<Integer>> EMPLOYEES_TO_DELETE = ThreadLocal.withInitial(ArrayList::new);

    @BeforeMethod(alwaysRun = true)
    public void login() {
        new LoginPage(driver()).open().loginAs(Config.adminUser(), Config.adminPassword());
    }

    protected DashboardPage dashboard() {
        return new DashboardPage(driver()).waitUntilLoaded();
    }

    protected void cleanUpEmployee(int employeeNumber) {
        EMPLOYEES_TO_DELETE.get().add(employeeNumber);
    }

    /** Runs before BaseTest.closeBrowser, while the session cookie is still valid. */
    @AfterMethod(alwaysRun = true)
    public void deleteTestData() {
        List<Integer> ids = EMPLOYEES_TO_DELETE.get();
        if (ids.isEmpty() || !DriverFactory.isStarted()) {
            return;
        }
        try {
            int status = new OrangeHrmApi(driver()).deleteEmployees(ids);
            Reporter.log("Clean-up: deleted employees " + ids + " (HTTP " + status + ")");
        } catch (Exception e) {
            Reporter.log("Clean-up failed for employees " + ids + ": " + e.getMessage());
        } finally {
            ids.clear();
        }
    }
}
