package com.santiagocz.membershipservice.domain.enums;

public enum MembershipStatus {
    ACTIVE ("Active"),
    INACTIVE ("Inactive");

    private final String displayName;

    MembershipStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
