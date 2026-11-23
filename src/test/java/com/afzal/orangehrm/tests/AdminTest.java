package com.afzal.orangehrm.tests;

import com.afzal.orangehrm.pages.SystemUsersPage;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AdminTest extends LoggedInTest {

    @Test(description = "ADM-01 searching users by username finds the Admin account")
    public void searchUserByUsername() {
        SystemUsersPage users = new SystemUsersPage(driver()).open().searchByUsername("Admin");
        List<List<String>> rows = users.rows();
        assertThat(rows).isNotEmpty();
        // Row cells: [checkbox, Username, User Role, Employee Name, Status, Actions]
        assertThat(rows).allSatisfy(row -> assertThat(row.get(1)).isEqualToIgnoringCase("Admin"));
        assertThat(rows.get(0).get(2)).isEqualTo("Admin");
    }

    /**
     * Filters by the Admin role: the demo is shared and visitors delete other
     * users, but the Admin account this suite logs in with always exists.
     */
    @Test(description = "ADM-02 filtering by role shows only users with that role")
    public void filterUsersByRole() {
        SystemUsersPage users = new SystemUsersPage(driver()).open().filterByRole("Admin");
        List<List<String>> rows = users.rows();
        assertThat(rows).as("users with role Admin").isNotEmpty();
        assertThat(rows).allSatisfy(row -> assertThat(row.get(2)).isEqualTo("Admin"));
    }
}
