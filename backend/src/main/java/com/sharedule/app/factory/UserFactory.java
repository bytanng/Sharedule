package com.sharedule.app.factory;

import com.sharedule.app.model.user.*;

public class UserFactory {
    public static Users createUser(String role, String username, String email, String password) {
        System.out.println("This is role");
        System.out.println(role);
        if ("ADMIN".equalsIgnoreCase(role)) {
            return new AppAdmins(username,email,password,role,"");
        } else if ("USER".equalsIgnoreCase(role)) {
            return new AppUsers(username,email,password,role,"");
        } else {
            throw new IllegalArgumentException("Invalid role: " + role);
        }
    }
}
