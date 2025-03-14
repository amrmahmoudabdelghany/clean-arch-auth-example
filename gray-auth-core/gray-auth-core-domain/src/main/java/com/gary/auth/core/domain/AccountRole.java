package com.gary.auth.core.domain;

public enum AccountRole {
    USER,
    ADMIN,
    SUPER_ADMIN;


    public static AccountRole of(String role) {
        if (role == null || role.isEmpty()) {
            throw new IllegalArgumentException("Role cannot be null or empty");
        }
        for (AccountRole accountRole : AccountRole.values()) {
            if (accountRole.name().equalsIgnoreCase(role)) {
                return accountRole;
            }
        }
        throw new IllegalArgumentException("No enum constant for role: " + role);
    }

}
