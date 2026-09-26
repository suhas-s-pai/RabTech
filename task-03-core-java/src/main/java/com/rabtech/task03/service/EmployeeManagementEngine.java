package com.rabtech.task03.service;

import com.rabtech.task03.exception.DuplicateAssignmentException;
import com.rabtech.task03.exception.DuplicateEmployeeException;
import com.rabtech.task03.exception.EmployeeNotFoundException;
import com.rabtech.task03.exception.InvalidEmployeeException;
import com.rabtech.task03.model.Employee;
import com.rabtech.task03.model.EmployeeSummary;
import com.rabtech.task03.model.Project;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class EmployeeManagementEngine {

    // Collections Framework Integration
    private final Map<String, Employee> employeesById = new LinkedHashMap<>();
    private final Set<Employee> employeeSet = new HashSet<>();
    private final Map<String, Set<Project>> projectAssignments = new HashMap<>();

    public void addEmployee(Employee employee) {
        if (employee == null) {
            throw new InvalidEmployeeException("Employee cannot be null");
        }
        if (employeesById.containsKey(employee.getId())) {
            throw new DuplicateEmployeeException("Employee with ID '" + employee.getId() + "' already exists");
        }
        employeesById.put(employee.getId(), employee);
        employeeSet.add(employee);
        projectAssignments.putIfAbsent(employee.getId(), new HashSet<>());
    }

    public void removeEmployee(String employeeId) throws EmployeeNotFoundException {
        if (employeeId == null || !employeesById.containsKey(employeeId)) {
            throw new EmployeeNotFoundException("Employee with ID '" + employeeId + "' not found");
        }
        Employee removed = employeesById.remove(employeeId);
        employeeSet.remove(removed);
        projectAssignments.remove(employeeId);
    }

    public Employee findEmployeeById(String employeeId) throws EmployeeNotFoundException {
        if (employeeId == null || !employeesById.containsKey(employeeId)) {
            throw new EmployeeNotFoundException("Employee with ID '" + employeeId + "' not found");
        }
        return employeesById.get(employeeId);
    }

    public List<Employee> getAllEmployees() {
        return new ArrayList<>(employeesById.values());
    }

    public Set<Employee> getEmployeeSet() {
        return Collections.unmodifiableSet(employeeSet);
    }

    public void assignProject(String employeeId, Project project) throws EmployeeNotFoundException {
        if (project == null || project.name() == null || project.name().isBlank()) {
            throw new InvalidEmployeeException("Project name cannot be blank");
        }
        if (!employeesById.containsKey(employeeId)) {
            throw new EmployeeNotFoundException("Employee with ID '" + employeeId + "' not found");
        }
        Set<Project> assignedProjects = projectAssignments.computeIfAbsent(employeeId, k -> new HashSet<>());
        if (assignedProjects.contains(project)) {
            throw new DuplicateAssignmentException("Employee '" + employeeId + "' is already assigned to project '" + project.name() + "'");
        }
        assignedProjects.add(project);
    }

    public Set<Project> getProjectsForEmployee(String employeeId) throws EmployeeNotFoundException {
        if (!employeesById.containsKey(employeeId)) {
            throw new EmployeeNotFoundException("Employee with ID '" + employeeId + "' not found");
        }
        return Collections.unmodifiableSet(projectAssignments.getOrDefault(employeeId, Collections.emptySet()));
    }

    // Java Streams and Lambdas Operations

    public List<Employee> filterByDepartment(String department) {
        if (department == null) return Collections.emptyList();
        return employeesById.values().stream()
                .filter(e -> e.getDepartment().equalsIgnoreCase(department))
                .collect(Collectors.toList());
    }

    public List<Employee> filterByMinimumSalary(double minSalary) {
        return employeesById.values().stream()
                .filter(e -> e.getSalary() >= minSalary)
                .collect(Collectors.toList());
    }

    public List<Employee> sortBySalary(boolean ascending) {
        Comparator<Employee> comparator = Comparator.comparingDouble(Employee::getSalary);
        if (!ascending) {
            comparator = comparator.reversed();
        }
        return employeesById.values().stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    public List<Employee> sortByName() {
        return employeesById.values().stream()
                .sorted(Comparator.comparing(Employee::getName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    public Map<String, List<Employee>> groupByDepartment() {
        return employeesById.values().stream()
                .collect(Collectors.groupingBy(Employee::getDepartment));
    }

    public Map<String, Long> countByRole() {
        return employeesById.values().stream()
                .collect(Collectors.groupingBy(Employee::getRole, Collectors.counting()));
    }

    public EmployeeSummary generateSummary() {
        int totalEmployees = employeesById.size();
        double totalSalaryBudget = employeesById.values().stream()
                .mapToDouble(Employee::getSalary)
                .sum();
        double averageSalary = totalEmployees > 0 ? totalSalaryBudget / totalEmployees : 0.0;
        Map<String, Long> roleCounts = countByRole();

        return new EmployeeSummary(totalEmployees, totalSalaryBudget, averageSalary, roleCounts);
    }
}
