package com.sharedule.app.service.transaction;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.sharedule.app.dto.CreateTransactionDTO;
import com.sharedule.app.exception.BackendErrorException;
import com.sharedule.app.exception.NotFoundException;
import com.sharedule.app.model.item.Item;
import com.sharedule.app.model.transaction.Transaction;
import com.sharedule.app.model.user.Users;
import com.sharedule.app.repository.transaction.TransactionRepo;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepo repo;

    public Transaction createTransaction(CreateTransactionDTO transactionDTO, Users user, Item item) {
        // Validate transaction location
        if (transactionDTO.getTransactionLocation() == null
                || transactionDTO.getTransactionLocation().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Location is required");
        }

        Transaction transaction = new Transaction();
        transaction.setTransactionName(transactionDTO.getTransactionName().trim());
        transaction.setBuyerId(transactionDTO.getBuyerId().trim());
        transaction.setTransactionLocation(transactionDTO.getTransactionLocation().trim());
        transaction.setUser(user);
        transaction.setItem(item);

        return repo.save(transaction);
    }

    public List<Transaction> getTransactionsByUser(Users user) {
        return repo.findByUser(user);
    }

    public Transaction getTransaction(String transactionId) throws BackendErrorException {
        try {
            Transaction transactionToBeViewed = repo.findById(transactionId)
                    .orElseThrow(() -> new NotFoundException("Transaction not found"));
            return transactionToBeViewed;
        } catch (NotFoundException nfe) {
            throw new BackendErrorException(nfe);
        }
    }
}
