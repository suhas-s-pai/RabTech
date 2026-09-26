package com.rabtech.task03.model;

import java.util.Map;

public record EmployeeSummary(
        int totalEmployees,
        double totalSalaryBudget,
        double averageSalary,
        Map<String, Long> roleCounts
) {}
