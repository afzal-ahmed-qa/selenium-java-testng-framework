package com.afzal.orangehrm.api;

import com.afzal.orangehrm.core.Config;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Calls OrangeHRM's own REST API with the logged-in browser's session cookie.
 * Used for clean-up: deleting test data through the API is fast and does not
 * depend on the UI, so a failed UI test still leaves the shared demo clean.
 */
public class OrangeHrmApi {

    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build();
    private final String cookieHeader;

    public OrangeHrmApi(WebDriver loggedInDriver) {
        this.cookieHeader = loggedInDriver.manage().getCookies().stream()
                .map(c -> c.getName() + "=" + c.getValue())
                .collect(Collectors.joining("; "));
    }

    /** Returns the HTTP status: 200 when deleted, 404 when the employee was already gone. */
    public int deleteEmployees(Collection<Integer> employeeNumbers) throws IOException, InterruptedException {
        String ids = employeeNumbers.stream().map(String::valueOf).collect(Collectors.joining(","));
        HttpRequest request = HttpRequest.newBuilder(URI.create(Config.baseUrl() + "/api/v2/pim/employees"))
                .timeout(Duration.ofSeconds(20))
                .header("Cookie", cookieHeader)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .method("DELETE", HttpRequest.BodyPublishers.ofString("{\"ids\":[" + ids + "]}"))
                .build();
        return http.send(request, HttpResponse.BodyHandlers.discarding()).statusCode();
    }
}
