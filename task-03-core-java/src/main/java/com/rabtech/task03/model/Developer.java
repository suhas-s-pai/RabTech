package com.rabtech.task03.model;

import com.rabtech.task03.exception.InvalidEmployeeException;

public class Developer extends Employee {
    private String programmingLanguage;

    public Developer(String id, String name, String department, double salary, String programmingLanguage) {
        super(id, name, department, salary);
        setProgrammingLanguage(programmingLanguage);
    }

    public String getProgrammingLanguage() {
        return programmingLanguage;
    }

    public void setProgrammingLanguage(String programmingLanguage) {
        if (programmingLanguage == null || programmingLanguage.isBlank()) {
            throw new InvalidEmployeeException("Programming language cannot be blank");
        }
        this.programmingLanguage = programmingLanguage;
    }

    @Override
    public String getRole() {
        return "Developer";
    }

    @Override
    public double calculateBonus() {
        return getSalary() * 0.15;
    }
}
