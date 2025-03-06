package com.sharedule.app.repository.user;
import com.sharedule.app.model.user.Users;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepo extends MongoRepository<Users, String> {
    Users findByUsername(String username);
    Users findByEmail(String email);
    Optional<Users> findById(Long id);

}
