package com.afzal.orangehrm.pages;

import com.afzal.orangehrm.core.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.function.Function;

/**
 * Wait-first helpers shared by all pages. Every action waits for the element
 * to be ready instead of sleeping, so tests are as fast as the app allows.
 */
public abstract class BasePage {

    /** OrangeHRM shows this spinner while it loads data. */
    private static final By SPINNER = By.cssSelector(".oxd-loading-spinner");

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Config.explicitWait());
    }

    /**
     * Inputs in OrangeHRM have no id or name in many forms, but every one sits
     * in a group with a visible label. Locating by label is stable and readable.
     * The class test matches the exact class "oxd-input-group": a plain contains()
     * would also match the label's own wrapper, "oxd-input-group__label-wrapper".
     */
    protected static By inputByLabel(String label) {
        return By.xpath("//label[normalize-space()='" + label + "']"
                + "/ancestor::div[contains(concat(' ',normalize-space(@class),' '),' oxd-input-group ')][1]//input");
    }

    protected static By buttonByText(String text) {
        return By.xpath("//button[normalize-space()='" + text + "']");
    }

    protected WebElement visible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void click(By locator) {
        waitForSpinner();
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    /** Select-all is Cmd+A on a Mac and Ctrl+A everywhere else. */
    private static final Keys SELECT_ALL_KEY =
            System.getProperty("os.name").toLowerCase().contains("mac") ? Keys.COMMAND : Keys.CONTROL;

    /** Clears with select-all + delete: OrangeHRM's Vue inputs ignore WebElement.clear(). */
    protected void type(By locator, String text) {
        WebElement field = visible(locator);
        field.sendKeys(Keys.chord(SELECT_ALL_KEY, "a"), Keys.DELETE);
        field.sendKeys(text);
    }

    /**
     * Runs a read and repeats it if the page replaced an element meanwhile.
     * OrangeHRM re-renders tables after every search, so an element found a moment
     * ago can be gone ("stale") by the time its text is read.
     */
    protected <T> T stable(Function<WebDriver, T> read) {
        return wait.ignoring(StaleElementReferenceException.class).until(read::apply);
    }

    protected String text(By locator) {
        visible(locator);
        return stable(d -> d.findElement(locator).getText().trim());
    }

    protected List<String> texts(By locator) {
        return stable(d -> d.findElements(locator).stream().map(e -> e.getText().trim()).toList());
    }

    /**
     * Reads the rows of a result table. Waits until rows are rendered, unless the
     * count says there are none: right after a search the table can be empty for a
     * moment while the new rows are drawn.
     */
    protected List<List<String>> tableRows(By rowsLocator, By countLocator) {
        return stable(d -> {
            String count = d.findElement(countLocator).getText().trim();
            List<List<String>> rows = d.findElements(rowsLocator).stream()
                    .map(row -> row.findElements(By.cssSelector(".oxd-table-cell")).stream()
                            .map(cell -> cell.getText().trim()).toList())
                    .toList();
            if (count.startsWith("No Records")) {
                return List.<List<String>>of();
            }
            return rows.isEmpty() ? null : rows;
        });
    }

    /**
     * Clicks a search button and waits until the result count shows something new.
     * If the count happens to stay the same, it goes on after the timeout's wait
     * rather than failing: the caller's assertions then check the result.
     */
    protected void searchAndWaitForNewResults(By searchButton, By countLocator) {
        String before = driver.findElements(countLocator).isEmpty() ? "" : text(countLocator);
        click(searchButton);
        waitForSpinner();
        try {
            stable(d -> {
                String now = d.findElement(countLocator).getText().trim();
                return !now.isEmpty() && !now.equals(before) ? now : null;
            });
        } catch (TimeoutException sameCount) {
            // The search returned the same number of records as before.
        }
    }

    protected void waitForSpinner() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(SPINNER));
    }

    protected void waitForUrlContains(String part) {
        wait.until(ExpectedConditions.urlContains(part));
    }

    protected void open(String path) {
        driver.get(Config.baseUrl() + path);
        waitForSpinner();
    }
}
