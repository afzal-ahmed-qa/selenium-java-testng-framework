package com.afzal.orangehrm.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

/** The main menu on the left, shown on every page after login. */
public class SideMenu {

    private static final By ITEMS = By.cssSelector(".oxd-main-menu-item span");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public SideMenu(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public List<String> items() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(ITEMS));
        return driver.findElements(ITEMS).stream().map(e -> e.getText().trim()).toList();
    }

    public void open(String item) {
        By link = By.xpath("//a[contains(@class,'oxd-main-menu-item')][.//span[normalize-space()='" + item + "']]");
        wait.until(ExpectedConditions.elementToBeClickable(link)).click();
    }

    /** Filters the menu with its search box, as a user would. */
    public List<String> search(String term) {
        By box = By.cssSelector(".oxd-main-menu-search input");
        wait.until(ExpectedConditions.visibilityOfElementLocated(box)).sendKeys(term);
        return items();
    }
}
