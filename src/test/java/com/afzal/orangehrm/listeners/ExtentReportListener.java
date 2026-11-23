package com.afzal.orangehrm.listeners;

import com.afzal.orangehrm.core.Config;
import com.afzal.orangehrm.core.DriverFactory;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

/**
 * Writes an HTML report to reports/index.html: one entry per test, with the
 * failure, the TestNG log lines and a screenshot of the browser at the moment
 * of failure. Runs before @AfterMethod, so the browser is still open.
 */
public class ExtentReportListener implements ITestListener {

    private static final ExtentReports EXTENT = create();
    private static final ThreadLocal<ExtentTest> CURRENT = new ThreadLocal<>();

    private static ExtentReports create() {
        ExtentSparkReporter spark = new ExtentSparkReporter("reports/index.html");
        spark.config().setDocumentTitle("OrangeHRM Selenium tests");
        spark.config().setReportName("OrangeHRM UI regression (" + Config.browser() + ")");
        spark.config().setTheme(Theme.STANDARD);
        ExtentReports extent = new ExtentReports();
        extent.attachReporter(spark);
        extent.setSystemInfo("Browser", Config.browser() + (Config.headless() ? " (headless)" : ""));
        extent.setSystemInfo("Application", Config.baseUrl());
        extent.setSystemInfo("Java", System.getProperty("java.version"));
        return extent;
    }

    @Override
    public void onTestStart(ITestResult result) {
        String description = result.getMethod().getDescription();
        String name = description != null && !description.isBlank() ? description : result.getMethod().getMethodName();
        ExtentTest test = EXTENT.createTest(name).assignCategory(result.getMethod().getGroups());
        CURRENT.set(test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logOutput(result);
        CURRENT.get().pass("Passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logOutput(result);
        ExtentTest test = CURRENT.get();
        test.fail(result.getThrowable());
        if (DriverFactory.isStarted()) {
            String png = ((TakesScreenshot) DriverFactory.get()).getScreenshotAs(OutputType.BASE64);
            test.fail("Browser at the moment of failure",
                    MediaEntityBuilder.createScreenCaptureFromBase64String(png).build());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest test = CURRENT.get();
        if (test != null) {
            test.skip(result.wasRetried() ? "Failed once and was retried" : "Skipped");
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        EXTENT.flush();
    }

    private static void logOutput(ITestResult result) {
        for (String line : Reporter.getOutput(result)) {
            CURRENT.get().info(line);
        }
    }
}
