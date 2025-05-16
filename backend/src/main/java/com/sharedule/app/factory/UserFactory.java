package com.sharedule.app.factory;

import com.sharedule.app.model.user.*;

public interface UserFactory {
    Users createUser(String username, String email, String password);
}

