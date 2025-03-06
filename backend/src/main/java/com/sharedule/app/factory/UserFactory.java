package com.sharedule.app.factory;

import com.sharedule.app.model.user.*;

public class UserFactory {
    public static Users createUser(String role, String username, String email, String password) {
        if ("ADMIN".equalsIgnoreCase(role)) {
            return new AppAdmins("","ADMIN", username, email, password);
        } else if ("USER".equalsIgnoreCase(role)) {
            return new AppUsers("","USER", username, email, password);
        } else {
            throw new IllegalArgumentException("Invalid role: " + role);
        }
    }
}
