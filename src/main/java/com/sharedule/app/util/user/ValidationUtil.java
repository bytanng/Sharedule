package com.sharedule.app.util.user;

import java.util.regex.Pattern;

public class ValidationUtil {
    private static final int MIN_USERNAME_LENGTH = 6;
    private static final int MAX_USERNAME_LENGTH = 20;
    private static final int MIN_PASSWORD_LENGTH = 3;
    
    private static final String[] VALID_EMAIL_DOMAINS = {
        "@gmail.com",
        "@outlook.com",
        "@hotmail.com",
        "@yahoo.com"
    };

    public static String validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return "Username cannot be empty";
        }
        
        if (username.length() < MIN_USERNAME_LENGTH) {
            return "Username must be at least " + MIN_USERNAME_LENGTH + " characters long";
        }
        
        if (username.length() > MAX_USERNAME_LENGTH) {
            return "Username cannot be longer than " + MAX_USERNAME_LENGTH + " characters";
        }
        
        // Check if username contains only alphanumeric characters and underscores
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            return "Username can only contain letters, numbers, and underscores";
        }
        
        return null; // null means validation passed
    }

    public static String validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "Email cannot be empty";
        }

        // Basic email format check
        if (!Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$").matcher(email).matches()) {
            return "Invalid email format";
        }

        // Check if email ends with valid domain
        boolean hasValidDomain = false;
        for (String domain : VALID_EMAIL_DOMAINS) {
            if (email.toLowerCase().endsWith(domain)) {
                hasValidDomain = true;
                break;
            }
        }

        if (!hasValidDomain) {
            return "Email domain not supported. Please use gmail.com, outlook.com, hotmail.com, or yahoo.com";
        }

        return null; // null means validation passed
    }

    public static String validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return "Password cannot be empty";
        }

        if (password.length() < MIN_PASSWORD_LENGTH) {
            return "Password must be at least " + MIN_PASSWORD_LENGTH + " characters long";
        }

        return null; // null means validation passed
    }
} 