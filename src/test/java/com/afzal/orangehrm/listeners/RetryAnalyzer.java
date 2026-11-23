package com.afzal.orangehrm.listeners;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Retries a failed test once, on CI only. The OrangeHRM demo is a shared public
 * site and is sometimes slow; locally a flaky test should fail at once so it gets fixed.
 * Retried tests show in the report, so flakiness stays visible.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final int MAX_RETRIES = System.getenv("CI") != null ? 1 : 0;
    private int attempts = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (attempts < MAX_RETRIES) {
            attempts++;
            return true;
        }
        return false;
    }
}
