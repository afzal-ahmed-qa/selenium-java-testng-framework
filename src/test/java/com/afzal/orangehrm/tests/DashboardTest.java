package com.afzal.orangehrm.tests;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DashboardTest extends LoggedInTest {

    @Test(groups = "smoke", description = "DASH-01 the dashboard shows the main widgets")
    public void dashboardShowsWidgets() {
        assertThat(dashboard().widgetTitles()).contains("Time at Work", "My Actions", "Quick Launch");
    }

    @Test(description = "DASH-02 the admin sees every module in the main menu")
    public void adminSeesAllModules() {
        assertThat(dashboard().menu().items()).contains(
                "Admin", "PIM", "Leave", "Time", "Recruitment", "My Info",
                "Performance", "Dashboard", "Directory", "Maintenance", "Claim", "Buzz");
    }

    @Test(description = "DASH-03 the menu search narrows the menu to matching modules")
    public void menuSearchFilters() {
        assertThat(dashboard().menu().search("Lea")).containsExactly("Leave");
    }
}
