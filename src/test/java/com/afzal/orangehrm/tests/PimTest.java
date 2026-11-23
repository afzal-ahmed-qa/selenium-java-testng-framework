package com.afzal.orangehrm.tests;

import com.afzal.orangehrm.pages.AddEmployeePage;
import com.afzal.orangehrm.pages.EmployeeListPage;
import com.afzal.orangehrm.pages.PersonalDetailsPage;
import net.datafaker.Faker;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;

/** PIM = Personal Information Management: the employee records. */
public class PimTest extends LoggedInTest {

    private final Faker faker = new Faker();

    /** A unique id per test, at most 10 characters (OrangeHRM's limit). */
    private static String uniqueEmployeeId() {
        return "QA" + ThreadLocalRandom.current().nextInt(10_000_000, 99_999_999);
    }

    private record Employee(String first, String middle, String last, String id) {
    }

    private Employee newEmployee() {
        return new Employee(faker.name().firstName(), faker.name().firstName(), faker.name().lastName(), uniqueEmployeeId());
    }

    private PersonalDetailsPage create(Employee e) {
        PersonalDetailsPage profile = new AddEmployeePage(driver()).open()
                .fill(e.first(), e.middle(), e.last(), e.id())
                .save();
        cleanUpEmployee(profile.employeeNumber());
        return profile;
    }

    @Test(groups = "smoke", description = "PIM-01 a new employee is saved and their profile opens")
    public void addEmployee() {
        Employee e = newEmployee();
        PersonalDetailsPage profile = create(e);
        assertThat(profile.fullName()).isEqualTo(e.first() + " " + e.last());
    }

    @Test(description = "PIM-02 searching by Employee Id finds exactly that employee")
    public void searchByEmployeeId() {
        Employee e = newEmployee();
        create(e);

        EmployeeListPage list = new EmployeeListPage(driver()).open().searchById(e.id());
        assertThat(list.recordCount()).isEqualTo("(1) Record Found");
        List<String> row = list.rows().get(0);
        // Columns: [checkbox, Id, First (& Middle) Name, Last Name, Job Title, Status, Sub Unit, Supervisor, Actions]
        assertThat(row.get(1)).isEqualTo(e.id());
        assertThat(row.get(2)).isEqualTo(e.first() + " " + e.middle());
        assertThat(row.get(3)).isEqualTo(e.last());
    }

    @Test(description = "PIM-03 first and last name are required")
    public void namesAreRequired() {
        AddEmployeePage form = new AddEmployeePage(driver()).open().saveExpectingErrors();
        assertThat(form.errorUnder("firstName")).isEqualTo("Required");
        assertThat(form.errorUnder("lastName")).isEqualTo("Required");
    }

    @Test(description = "PIM-04 an employee deleted in the list can no longer be found")
    public void deleteEmployee() {
        Employee e = newEmployee();
        create(e);

        EmployeeListPage list = new EmployeeListPage(driver()).open().searchById(e.id());
        assertThat(list.deleteFirstRow()).isEqualTo("Successfully Deleted");
        assertThat(new EmployeeListPage(driver()).open().searchById(e.id()).recordCount()).isEqualTo("No Records Found");
    }
}
