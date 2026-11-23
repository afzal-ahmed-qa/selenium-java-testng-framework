package com.afzal.orangehrm.tests;

import com.afzal.orangehrm.core.Config;
import com.afzal.orangehrm.pages.DashboardPage;
import com.afzal.orangehrm.pages.LoginPage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginTest extends BaseTest {

    @Test(groups = "smoke", description = "LOGIN-01 admin logs in and lands on the dashboard")
    public void adminCanLogIn() {
        DashboardPage dashboard = new LoginPage(driver()).open().loginAs(Config.adminUser(), Config.adminPassword());
        assertThat(dashboard.title()).isEqualTo("Dashboard");
    }

    /** Negative cases come from a JSON file, so adding a case needs no code change. */
    @DataProvider(name = "invalidLogins", parallel = true)
    public Object[][] invalidLogins() throws IOException {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("testdata/invalid-logins.json")) {
            JsonNode cases = new ObjectMapper().readTree(in);
            Object[][] rows = new Object[cases.size()][];
            for (int i = 0; i < cases.size(); i++) {
                JsonNode c = cases.get(i);
                rows[i] = new Object[] {c.get("id").asText() + " " + c.get("case").asText(),
                        c.get("username").asText(), c.get("password").asText(), c.get("message").asText()};
            }
            return rows;
        }
    }

    @Test(dataProvider = "invalidLogins", description = "LOGIN-02..04 invalid credentials are rejected")
    public void invalidCredentialsAreRejected(String testCase, String username, String password, String message) {
        LoginPage login = new LoginPage(driver()).open().submit(username, password);
        assertThat(login.errorMessage()).as(testCase).isEqualTo(message);
        assertThat(login.isDisplayed()).as(testCase + ": still on the login page").isTrue();
    }

    @Test(description = "LOGIN-05 empty username and password show 'Required' under both fields")
    public void emptyFieldsAreRequired() {
        LoginPage login = new LoginPage(driver()).open().submit("", "");
        assertThat(login.fieldErrors()).containsExactly("Required", "Required");
    }

    @Test(groups = "smoke", description = "LOGIN-06 after logout the dashboard needs a new login")
    public void logoutEndsTheSession() {
        new LoginPage(driver()).open().loginAs(Config.adminUser(), Config.adminPassword()).logout();
        driver().get(Config.baseUrl() + "/dashboard/index");
        // The app redirects to the login page; waitUntilDisplayed fails if it does not.
        assertThat(new LoginPage(driver()).waitUntilDisplayed().isDisplayed()).isTrue();
    }
}
