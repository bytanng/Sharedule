package com.sharedule.app.repository.user;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sharedule.app.model.user.Users;

@Repository
public interface UserRepo extends MongoRepository<Users, String> {
    Users findByUsername(String username);
    Users findByEmail(String email);
}
