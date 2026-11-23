package com.afzal.orangehrm.pages;

import com.afzal.orangehrm.components.SideMenu;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;

public class DashboardPage extends BasePage {

    private static final By TITLE = By.cssSelector(".oxd-topbar-header-breadcrumb h6");
    private static final By WIDGET_TITLES = By.cssSelector(".orangehrm-dashboard-widget-name p");
    private static final By USER_DROPDOWN = By.cssSelector(".oxd-userdropdown-tab");
    private static final By LOGOUT = By.xpath("//a[normalize-space()='Logout']");

    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    public DashboardPage waitUntilLoaded() {
        waitForUrlContains("/dashboard");
        visible(TITLE);
        waitForSpinner();
        return this;
    }

    public String title() {
        return text(TITLE);
    }

    public List<String> widgetTitles() {
        visible(WIDGET_TITLES);
        return texts(WIDGET_TITLES);
    }

    public SideMenu menu() {
        return new SideMenu(driver, wait);
    }

    public LoginPage logout() {
        click(USER_DROPDOWN);
        click(LOGOUT);
        waitForUrlContains("/auth/login");
        return new LoginPage(driver);
    }
}
