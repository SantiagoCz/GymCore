package com.santiagocz.employeeservice.domain.enums;

public enum EmployeeRole {
    RECEPTIONIST ("Recepcionist"),
    TRAINER ("Trainer"),
    ADMINISTRATIVE ("Administrative"),
    CLEANING ("Cleaning"),
    MANAGER ("Manager");

    private final String displayName;

    EmployeeRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
