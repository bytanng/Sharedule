package com.sharedule.app.repository.transaction;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.sharedule.app.model.transaction.Transaction;
import com.sharedule.app.model.user.Users;
import com.sharedule.app.model.item.Item;

@Repository
public interface TransactionRepo extends MongoRepository<Transaction, String> {
    List<Transaction> findBySeller(Users user);
    
    List<Transaction> findByItem(Item item);

    Optional<Transaction> findById(String id);
    
    List<Transaction> findByBuyer(Users user);
}
