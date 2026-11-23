# Selenium + Java + TestNG framework for OrangeHRM

[![Selenium Tests](https://github.com/afzal-ahmed-qa/selenium-java-testng-framework/actions/workflows/selenium.yml/badge.svg)](https://github.com/afzal-ahmed-qa/selenium-java-testng-framework/actions/workflows/selenium.yml)
![Selenium](https://img.shields.io/badge/Selenium-4.52-43B02A?logo=selenium&logoColor=white)
![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)
![TestNG](https://img.shields.io/badge/TestNG-7.12-FF7F00)

UI tests for the public [OrangeHRM demo](https://opensource-demo.orangehrmlive.com), an open-source
HR system, with Selenium 4, Java 21, TestNG and Maven. Tests run in parallel, on Chrome and Firefox,
in GitHub Actions every night and on every push. Reports with a screenshot of each failure:
https://afzal-ahmed-qa.github.io/selenium-java-testng-framework/

My other automation projects use Playwright and TypeScript. This one covers the stack that many
teams still run in production: Selenium WebDriver with Java and TestNG.

## What's tested

| ID | Test | Group |
|---|---|---|
| LOGIN-01 | Admin logs in and lands on the dashboard | smoke |
| LOGIN-02..04 | Wrong password, unknown user, password case: "Invalid credentials" (data-driven from JSON) | |
| LOGIN-05 | Empty username and password: "Required" under both fields | |
| LOGIN-06 | After logout, the dashboard needs a new login | smoke |
| DASH-01 | Dashboard shows its main widgets | smoke |
| DASH-02 | Admin sees all 12 modules in the main menu | |
| DASH-03 | Menu search narrows the menu | |
| PIM-01 | A new employee is saved and their profile opens | smoke |
| PIM-02 | Search by Employee Id finds exactly that employee | |
| PIM-03 | First and last name are required | |
| PIM-04 | An employee deleted in the list can no longer be found | |
| ADM-01 | User search by username | |
| ADM-02 | User filter by role | |

15 test runs per browser.

## Project layout

```
src/test/java/com/afzal/orangehrm/
  core/        Config (properties + -D overrides), DriverFactory (ThreadLocal browser per thread)
  pages/       page objects; BasePage has the wait helpers
  components/  SideMenu, Toast
  api/         OrangeHRM REST client used for clean-up
  listeners/   ExtentReports (screenshot on failure), retry on CI
  tests/       BaseTest, LoggedInTest and the test classes
src/test/resources/
  config.properties, testdata/invalid-logins.json
testng.xml, testng-smoke.xml
```

## How it's put together

**One browser per thread.** TestNG runs the methods of a class in parallel, on the same test object.
A `driver` field would be overwritten by the other threads, and tests would drive each other's
browsers. My first run did exactly that ("browser died", "stale element"). Now the driver lives in a
`ThreadLocal` in `DriverFactory`, and tests always call `driver()`. The clean-up list is per thread too.

**Explicit waits only.** Implicit waits are zero. `BasePage` waits for each element to be visible or
clickable, and for OrangeHRM's loading spinner to go. After a search it waits until the record count
actually changes, because the old table is still on screen for a moment.

**Stale-safe reads.** OrangeHRM re-renders tables after every search, so an element found a moment
ago can be replaced before its text is read. Reads go through `stable()`, which repeats the read if
that happens.

**Locators by label.** Many OrangeHRM inputs have no id or name, but each sits in a group with a
visible label. `inputByLabel("Employee Id")` finds the input from its label. The XPath matches the exact
class `oxd-input-group`; a plain `contains()` also matched the label's wrapper
(`oxd-input-group__label-wrapper`) and found nothing.

**Clean data on a shared demo.** Tests that create an employee register it, and an `@AfterMethod`
deletes it through OrangeHRM's REST API with the browser's session cookie, even when the test failed.
Tests also avoid assumptions about other people's data: at one point the demo had a single user left,
so the role filter test uses the Admin role, which always exists.

**Data-driven.** The invalid-login cases come from a JSON file through a TestNG `@DataProvider`.

**Reports.** ExtentReports writes `reports/index.html` with every test, its log lines and a screenshot
of the browser at the moment of failure. On CI a failed test is retried once (the demo is public and
sometimes slow); retries stay visible in the report.

## Running it

Java 21 or newer. Maven comes with the project (`./mvnw`).

```bash
./mvnw test                                   # all tests, headless Chrome
./mvnw test -Dbrowser=firefox                 # Firefox
./mvnw test -Dheadless=false                  # watch the browser
./mvnw test -Dsuite=testng-smoke.xml          # smoke group only
open reports/index.html                       # the report
```

## Next on my list

- Selenium Grid in Docker for remote, parallel cross-browser runs.
- Leave and Recruitment workflows.

---

Afzal Ahmed, [github.com/afzal-ahmed-qa](https://github.com/afzal-ahmed-qa)
