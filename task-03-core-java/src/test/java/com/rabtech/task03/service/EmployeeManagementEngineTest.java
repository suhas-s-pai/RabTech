package com.rabtech.task03.service;

import com.rabtech.task03.exception.DuplicateAssignmentException;
import com.rabtech.task03.exception.DuplicateEmployeeException;
import com.rabtech.task03.exception.EmployeeNotFoundException;
import com.rabtech.task03.exception.InvalidEmployeeException;
import com.rabtech.task03.model.Developer;
import com.rabtech.task03.model.Employee;
import com.rabtech.task03.model.Manager;
import com.rabtech.task03.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeManagementEngineTest {

    private EmployeeManagementEngine engine;
    private Employee dev1;
    private Employee mgr1;

    @BeforeEach
    void setUp() {
        engine = new EmployeeManagementEngine();
        dev1 = new Developer("E101", "Eve Adams", "Engineering", 95000.0, "Java");
        mgr1 = new Manager("E102", "Frank Wright", "Management", 130000.0, 8);
    }

    @Test
    @DisplayName("Should successfully add employees")
    void testAddEmployee() {
        engine.addEmployee(dev1);
        engine.addEmployee(mgr1);

        assertEquals(2, engine.getAllEmployees().size());
        assertEquals(2, engine.getEmployeeSet().size());
    }

    @Test
    @DisplayName("Unchecked Exception: Duplicate employee ID throws DuplicateEmployeeException")
    void testAddDuplicateEmployeeThrowsUncheckedException() {
        engine.addEmployee(dev1);

        Developer duplicate = new Developer("E101", "Eve Clone", "HR", 50000.0, "Python");

        DuplicateEmployeeException ex = assertThrows(
                DuplicateEmployeeException.class,
                () -> engine.addEmployee(duplicate)
        );
        assertTrue(ex.getMessage().contains("already exists"));
    }

    @Test
    @DisplayName("Should find employee by ID")
    void testFindEmployeeById() throws EmployeeNotFoundException {
        engine.addEmployee(dev1);

        Employee found = engine.findEmployeeById("E101");
        assertNotNull(found);
        assertEquals("Eve Adams", found.getName());
    }

    @Test
    @DisplayName("Checked Exception: Finding non-existent employee throws EmployeeNotFoundException")
    void testFindNonExistentEmployeeThrowsCheckedException() {
        EmployeeNotFoundException ex = assertThrows(
                EmployeeNotFoundException.class,
                () -> engine.findEmployeeById("NON-EXISTENT")
        );
        assertTrue(ex.getMessage().contains("not found"));
    }

    @Test
    @DisplayName("Should successfully remove employee")
    void testRemoveEmployee() throws EmployeeNotFoundException {
        engine.addEmployee(dev1);
        engine.removeEmployee("E101");

        assertEquals(0, engine.getAllEmployees().size());
        assertThrows(EmployeeNotFoundException.class, () -> engine.findEmployeeById("E101"));
    }

    @Test
    @DisplayName("Checked Exception: Removing non-existent employee throws EmployeeNotFoundException")
    void testRemoveNonExistentEmployeeThrowsCheckedException() {
        assertThrows(
                EmployeeNotFoundException.class,
                () -> engine.removeEmployee("GHOST-ID")
        );
    }

    @Test
    @DisplayName("Should assign projects to employee")
    void testAssignProject() throws EmployeeNotFoundException {
        engine.addEmployee(dev1);
        Project project = new Project("PRJ-1", "Cloud Migration", "Internal");

        engine.assignProject("E101", project);

        Set<Project> assigned = engine.getProjectsForEmployee("E101");
        assertEquals(1, assigned.size());
        assertTrue(assigned.contains(project));
    }

    @Test
    @DisplayName("Unchecked Exception: Duplicate project assignment throws DuplicateAssignmentException")
    void testDuplicateProjectAssignmentThrowsUncheckedException() throws EmployeeNotFoundException {
        engine.addEmployee(dev1);
        Project project = new Project("PRJ-1", "Cloud Migration", "Internal");

        engine.assignProject("E101", project);

        DuplicateAssignmentException ex = assertThrows(
                DuplicateAssignmentException.class,
                () -> engine.assignProject("E101", project)
        );
        assertTrue(ex.getMessage().contains("already assigned"));
    }

    @Test
    @DisplayName("Checked Exception: Assigning project to non-existent employee throws EmployeeNotFoundException")
    void testAssignProjectToNonExistentEmployeeThrowsCheckedException() {
        Project project = new Project("PRJ-1", "AI Portal", "Client X");

        assertThrows(
                EmployeeNotFoundException.class,
                () -> engine.assignProject("UNKNOWN", project)
        );
    }

    @Test
    @DisplayName("Unchecked Exception: Blank project name throws InvalidEmployeeException")
    void testInvalidProjectNameThrowsUncheckedException() {
        engine.addEmployee(dev1);

        assertThrows(
                InvalidEmployeeException.class,
                () -> new Project("P1", "  ", "Client Y")
        );
    }
}
