package com.rabtech.task03.service;

import com.rabtech.task03.model.Developer;
import com.rabtech.task03.model.Employee;
import com.rabtech.task03.model.EmployeeSummary;
import com.rabtech.task03.model.Manager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class StreamAndLambdaOperationsTest {

    private EmployeeManagementEngine engine;

    @BeforeEach
    void setUp() {
        engine = new EmployeeManagementEngine();
        engine.addEmployee(new Developer("E01", "Charlie", "Engineering", 80000.0, "Java"));
        engine.addEmployee(new Developer("E02", "Alice", "Engineering", 110000.0, "Go"));
        engine.addEmployee(new Developer("E03", "Bob", "Quality Assurance", 70000.0, "Python"));
        engine.addEmployee(new Manager("E04", "Diana", "Engineering", 140000.0, 10));
        engine.addEmployee(new Manager("E05", "Evan", "Quality Assurance", 120000.0, 5));
    }

    @Test
    @DisplayName("Streams: Filter employees by department")
    void testFilterByDepartment() {
        List<Employee> engEmployees = engine.filterByDepartment("Engineering");
        assertEquals(3, engEmployees.size());

        List<Employee> qaEmployees = engine.filterByDepartment("Quality Assurance");
        assertEquals(2, qaEmployees.size());

        List<Employee> hrEmployees = engine.filterByDepartment("Human Resources");
        assertTrue(hrEmployees.isEmpty());
    }

    @Test
    @DisplayName("Streams: Filter employees by minimum salary")
    void testFilterByMinimumSalary() {
        List<Employee> highEarners = engine.filterByMinimumSalary(110000.0);
        assertEquals(3, highEarners.size());
        assertTrue(highEarners.stream().allMatch(e -> e.getSalary() >= 110000.0));
    }

    @Test
    @DisplayName("Streams: Sort employees by salary ascending and descending")
    void testSortBySalaryAscendingAndDescending() {
        List<Employee> asc = engine.sortBySalary(true);
        assertEquals("Bob", asc.get(0).getName());
        assertEquals("Diana", asc.get(asc.size() - 1).getName());

        List<Employee> desc = engine.sortBySalary(false);
        assertEquals("Diana", desc.get(0).getName());
        assertEquals("Bob", desc.get(desc.size() - 1).getName());
    }

    @Test
    @DisplayName("Streams: Sort employees by name case-insensitively")
    void testSortByName() {
        List<Employee> sorted = engine.sortByName();
        assertEquals("Alice", sorted.get(0).getName());
        assertEquals("Bob", sorted.get(1).getName());
        assertEquals("Charlie", sorted.get(2).getName());
        assertEquals("Diana", sorted.get(3).getName());
        assertEquals("Evan", sorted.get(4).getName());
    }

    @Test
    @DisplayName("Streams: Group employees by department")
    void testGroupByDepartment() {
        Map<String, List<Employee>> grouped = engine.groupByDepartment();

        assertEquals(2, grouped.size());
        assertTrue(grouped.containsKey("Engineering"));
        assertTrue(grouped.containsKey("Quality Assurance"));
        assertEquals(3, grouped.get("Engineering").size());
        assertEquals(2, grouped.get("Quality Assurance").size());
    }

    @Test
    @DisplayName("Streams: Count employees by role")
    void testCountByRole() {
        Map<String, Long> roleCounts = engine.countByRole();

        assertEquals(2, roleCounts.size());
        assertEquals(3L, roleCounts.get("Developer"));
        assertEquals(2L, roleCounts.get("Manager"));
    }

    @Test
    @DisplayName("Streams: Generate employee summary information")
    void testGenerateEmployeeSummary() {
        EmployeeSummary summary = engine.generateSummary();

        assertEquals(5, summary.totalEmployees());
        // 80k + 110k + 70k + 140k + 120k = 520k
        assertEquals(520000.0, summary.totalSalaryBudget(), 0.001);
        // 520k / 5 = 104k
        assertEquals(104000.0, summary.averageSalary(), 0.001);
        assertEquals(3L, summary.roleCounts().get("Developer"));
        assertEquals(2L, summary.roleCounts().get("Manager"));
    }
}
