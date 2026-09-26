package com.rabtech.task03.model;

import com.rabtech.task03.exception.InvalidEmployeeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeTest {

    @Test
    @DisplayName("Developer should correctly inherit from Employee")
    void testDeveloperCreationAndInheritance() {
        Developer dev = new Developer("EMP-001", "Alice Smith", "Engineering", 90000.0, "Java");

        assertTrue(dev instanceof Employee);
        assertEquals("EMP-001", dev.getId());
        assertEquals("Alice Smith", dev.getName());
        assertEquals("Engineering", dev.getDepartment());
        assertEquals(90000.0, dev.getSalary());
        assertEquals("Java", dev.getProgrammingLanguage());
        assertEquals("Developer", dev.getRole());
    }

    @Test
    @DisplayName("Manager should correctly inherit from Employee")
    void testManagerCreationAndInheritance() {
        Manager mgr = new Manager("EMP-002", "Bob Jones", "Management", 120000.0, 5);

        assertTrue(mgr instanceof Employee);
        assertEquals("EMP-002", mgr.getId());
        assertEquals("Bob Jones", mgr.getName());
        assertEquals("Management", mgr.getDepartment());
        assertEquals(120000.0, mgr.getSalary());
        assertEquals(5, mgr.getTeamSize());
        assertEquals("Manager", mgr.getRole());
    }

    @Test
    @DisplayName("Polymorphism: Bonus calculation differs per subclass")
    void testPolymorphismBonusCalculation() {
        Employee dev = new Developer("DEV-1", "Charlie", "Engineering", 100000.0, "Python");
        Employee mgr = new Manager("MGR-1", "Diana", "Engineering", 100000.0, 10);

        // Developer bonus: 15% of 100000 = 15000
        assertEquals(15000.0, dev.calculateBonus(), 0.001);

        // Manager bonus: 20% of 100000 + (10 * 500) = 20000 + 5000 = 25000
        assertEquals(25000.0, mgr.calculateBonus(), 0.001);
    }

    @Test
    @DisplayName("Validation: Blank Employee ID throws InvalidEmployeeException")
    void testInvalidEmployeeIdValidation() {
        InvalidEmployeeException ex = assertThrows(
                InvalidEmployeeException.class,
                () -> new Developer(" ", "Alice", "Dev", 50000.0, "Java")
        );
        assertTrue(ex.getMessage().contains("Employee ID cannot be blank"));
    }

    @Test
    @DisplayName("Validation: Blank Employee Name throws InvalidEmployeeException")
    void testInvalidEmployeeNameValidation() {
        InvalidEmployeeException ex = assertThrows(
                InvalidEmployeeException.class,
                () -> new Developer("DEV-1", "", "Dev", 50000.0, "Java")
        );
        assertTrue(ex.getMessage().contains("Employee name cannot be blank"));
    }

    @Test
    @DisplayName("Validation: Blank Department throws InvalidEmployeeException")
    void testInvalidDepartmentValidation() {
        InvalidEmployeeException ex = assertThrows(
                InvalidEmployeeException.class,
                () -> new Manager("MGR-1", "Bob", null, 60000.0, 3)
        );
        assertTrue(ex.getMessage().contains("Department cannot be blank"));
    }

    @Test
    @DisplayName("Validation: Negative Salary throws InvalidEmployeeException")
    void testNegativeSalaryValidation() {
        InvalidEmployeeException ex = assertThrows(
                InvalidEmployeeException.class,
                () -> new Developer("DEV-1", "Alice", "Dev", -500.0, "Java")
        );
        assertTrue(ex.getMessage().contains("Salary cannot be negative"));
    }

    @Test
    @DisplayName("Validation: Blank Programming Language throws InvalidEmployeeException")
    void testInvalidDeveloperLanguage() {
        assertThrows(
                InvalidEmployeeException.class,
                () -> new Developer("DEV-1", "Alice", "Dev", 50000.0, "   ")
        );
    }

    @Test
    @DisplayName("Validation: Negative Team Size throws InvalidEmployeeException")
    void testInvalidManagerTeamSize() {
        assertThrows(
                InvalidEmployeeException.class,
                () -> new Manager("MGR-1", "Bob", "Mgmt", 70000.0, -1)
        );
    }
}
