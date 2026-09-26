package com.rabtech.task03.model;

import com.rabtech.task03.exception.InvalidEmployeeException;

public class Manager extends Employee {
    private int teamSize;

    public Manager(String id, String name, String department, double salary, int teamSize) {
        super(id, name, department, salary);
        setTeamSize(teamSize);
    }

    public int getTeamSize() {
        return teamSize;
    }

    public void setTeamSize(int teamSize) {
        if (teamSize < 0) {
            throw new InvalidEmployeeException("Team size cannot be negative");
        }
        this.teamSize = teamSize;
    }

    @Override
    public String getRole() {
        return "Manager";
    }

    @Override
    public double calculateBonus() {
        return (getSalary() * 0.20) + (teamSize * 500.0);
    }
}
