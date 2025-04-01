package com.sharedule.app.repository.transaction;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.sharedule.app.model.transaction.Transaction;
import com.sharedule.app.model.user.Users;

@Repository
public interface TransactionRepo extends MongoRepository<Transaction, String> {
    List<Transaction> findByUser(Users user);

    Optional<Transaction> findById(String id);
}
