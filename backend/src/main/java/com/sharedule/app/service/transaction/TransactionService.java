package com.sharedule.app.service.transaction;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.sharedule.app.dto.CreateTransactionDTO;
import com.sharedule.app.exception.BackendErrorException;
import com.sharedule.app.exception.NotFoundException;
import com.sharedule.app.model.item.Item;
import com.sharedule.app.model.timeslot.Timeslot;
import com.sharedule.app.model.transaction.Transaction;
import com.sharedule.app.model.user.Users;
import com.sharedule.app.repository.timeslot.TimeslotRepo;
import com.sharedule.app.repository.transaction.TransactionRepo;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepo repo;
    
    @Autowired
    private TimeslotRepo timeslotRepo;

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
        transaction.setSeller(user);
        transaction.setItem(item);
        return repo.save(transaction);
    }

    public List<Transaction> getTransactionsByUser(Users user) {
        return repo.findBySeller(user);
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
    
    public List<Transaction> getTransactionsForItem(Item item) {
        return repo.findByItem(item);
    }
    
    public Transaction bookTransaction(Transaction transaction, Users buyer) {
        // Validate transaction is not already booked
        if (transaction.getBuyerId() != null && !transaction.getBuyerId().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This transaction is already booked");
        }
        
        // Set the buyer details
        transaction.setBuyer(buyer);
        transaction.setBuyerId(buyer.getId());
        
        // Save and return the updated transaction
        return repo.save(transaction);
    }
    
    /**
     * Get transactions where the user is the buyer (appointments the user has booked)
     * and include their associated timeslot data
     */
    public List<Transaction> getBuyingTransactions(Users user) {
        List<Transaction> transactions = repo.findByBuyer(user);
        return transactions.stream()
            .map(transaction -> {
                // Fetch and attach the associated timeslot
                Timeslot timeslot = timeslotRepo.findByTransaction_Id(transaction.getId());
                transaction.setTimeslot(timeslot);
                return transaction;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Get transactions where the user is the seller (appointments for the user's listings)
     * and include their associated timeslot data
     */
    public List<Transaction> getSellingTransactions(Users user) {
        List<Transaction> transactions = repo.findBySeller(user);
        return transactions.stream()
            .map(transaction -> {
                // Fetch and attach the associated timeslot
                Timeslot timeslot = timeslotRepo.findByTransaction_Id(transaction.getId());
                transaction.setTimeslot(timeslot);
                return transaction;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Save an updated transaction
     */
    public Transaction saveTransaction(Transaction transaction) {
        return repo.save(transaction);
    }

    /**
     * Get a transaction by ID and ensure its timeslot is loaded
     */
    public Transaction getTransactionWithTimeslot(String transactionId) throws BackendErrorException {
        Transaction transaction = getTransaction(transactionId);
        if (transaction != null) {
            Timeslot timeslot = timeslotRepo.findByTransaction_Id(transactionId);
            transaction.setTimeslot(timeslot);
        }
        return transaction;
    }
}
