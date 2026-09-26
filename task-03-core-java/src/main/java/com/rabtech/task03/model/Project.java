package com.rabtech.task03.model;

import com.rabtech.task03.exception.InvalidEmployeeException;

public record Project(String id, String name, String clientName) {
    public Project {
        if (id == null || id.isBlank()) {
            throw new InvalidEmployeeException("Project ID cannot be blank");
        }
        if (name == null || name.isBlank()) {
            throw new InvalidEmployeeException("Project name cannot be blank");
        }
    }
}
