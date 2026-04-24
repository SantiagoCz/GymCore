package com.santiagocz.employeeservice.domain.enums;

public enum EmployeeStatus {
    ACTIVE ("Active"),
    INACTIVE ("Inactive"),
    SUSPENDED ("Suspended");

    private final String displayName;

    EmployeeStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}