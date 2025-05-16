package com.sharedule.app.factory;

import com.sharedule.app.model.user.AppAdmins;
import com.sharedule.app.model.user.Users;

public class AdminUserFactory implements UserFactory {

    @Override
    public Users createUser(String username, String email, String password) {
        return new AppAdmins(username, email, password, "ADMIN", "");
    }
}
