package org.marketplace.enums;

public enum UserRole {
    ADMIN("Admin"),
    USER("User");

    private String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getValue() {
        return role;
    }
}