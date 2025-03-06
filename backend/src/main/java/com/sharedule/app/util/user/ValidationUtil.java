package com.sharedule.app.util.user;

import java.util.regex.Pattern;

import com.sharedule.app.exception.ValidationException;

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

    public static void validateUsername(String username) throws ValidationException {
        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("Username cannot be empty");
        }

        if (username.length() < MIN_USERNAME_LENGTH) {
            throw new ValidationException("Username must be at least " + MIN_USERNAME_LENGTH + " characters long");
        }

        if (username.length() > MAX_USERNAME_LENGTH) {
            throw new ValidationException("Username cannot be longer than " + MAX_USERNAME_LENGTH + " characters");
        }

        // Check if username contains only alphanumeric characters and underscores
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            throw new ValidationException("Username can only contain letters, numbers, and underscores");
        }
    }

    public static void validateEmail(String email) throws ValidationException {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email cannot be empty");
        }

        // Basic email format check
        if (!Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$").matcher(email).matches()) {
            throw new ValidationException("Invalid email format");
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
            throw new ValidationException(
                    "Email domain not supported. Please use gmail.com, outlook.com, hotmail.com, or yahoo.com");
        }
    }

    public static void validatePassword(String password) throws ValidationException {
        if (password == null || password.trim().isEmpty()) {
            throw new ValidationException("Password cannot be empty");
        }

        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new ValidationException("Password must be at least " + MIN_PASSWORD_LENGTH + " characters long");
        }
    }
}