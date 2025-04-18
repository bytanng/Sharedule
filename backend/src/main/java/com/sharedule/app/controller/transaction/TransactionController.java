package com.sharedule.app.controller.transaction;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.sharedule.app.dto.CreateTransactionDTO;
import com.sharedule.app.dto.BookTransactionDTO;
import com.sharedule.app.model.item.Item;
import com.sharedule.app.model.transaction.Transaction;
import com.sharedule.app.model.user.Users;
import com.sharedule.app.service.item.ItemService;
import com.sharedule.app.service.transaction.TransactionService;
import com.sharedule.app.service.user.JWTService;
import com.sharedule.app.service.user.UserService;

import jakarta.validation.Valid;

@RestController
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private JWTService jwtService;

    @PostMapping("/create-transaction/{itemId}")
    public ResponseEntity<?> createTransaction(
            @RequestHeader("Authorization") String token,
            @PathVariable String itemId,
            @Valid @RequestBody CreateTransactionDTO transaction) {
        try {
            // Validate token format
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token format");
            }
            // Extract and validate token
            String jwtToken = token.substring(7);
            if (jwtService.isTokenExpired(jwtToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token has expired");
            }

            // Get user from token
            Users user = userService.getUser(token);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }

            // Get item from itemId
            Item item = itemService.getItem(itemId);
            if (item == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Item not found");
            }

            // Create and save the transaction
            Transaction savedTransaction = transactionService.createTransaction(transaction, user, item);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTransaction);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while processing your request: " + e.getMessage());
        }
    }

    @GetMapping("/transactions")
    public ResponseEntity<?> getItemTransactions(@RequestHeader("Authorization") String token) {
        try {
            // Validate token format
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token format");
            }

            // Extract and validate token
            String jwtToken = token.substring(7);
            if (jwtService.isTokenExpired(jwtToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token has expired");
            }

            // Get authenticated user
            Users user = userService.getUser(token);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }

            // Retrieve user's items
            List<Transaction> transactions = transactionService.getTransactionsByUser(user);
            return ResponseEntity.ok(transactions);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching items: " + e.getMessage());
        }
    }

    @GetMapping("/transactions/{itemId}")
    public ResponseEntity<?> getAvailableTransactionsForItem(@PathVariable String itemId) {
        try {
            // Get item from itemId
            Item item = itemService.getItem(itemId);
            if (item == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found");
            }

            // Retrieve available transactions for the item
            List<Transaction> availableTransactions = transactionService.getTransactionsForItem(item);
            return ResponseEntity.ok(availableTransactions);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching available transactions: " + e.getMessage());
        }
    }
    
    @PostMapping("/transactions/book")
    public ResponseEntity<?> bookTransaction(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody BookTransactionDTO bookingData) {
        try {
            // Validate token format
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token format");
            }
            
            // Extract and validate token
            String jwtToken = token.substring(7);
            if (jwtService.isTokenExpired(jwtToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token has expired");
            }

            // Get authenticated user
            Users user = userService.getUser(token);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }

            // Retrieve the transaction
            Transaction transaction = transactionService.getTransaction(bookingData.getTransactionId());
            if (transaction == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaction not found");
            }
            
            // Check if the transaction is already booked
            if (transaction.getBuyerId() != null && !transaction.getBuyerId().isEmpty()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("This transaction is already booked");
            }
            
            // Book the transaction with the current user as buyer
            Transaction bookedTransaction = transactionService.bookTransaction(transaction, user);
            
            return ResponseEntity.ok(bookedTransaction);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while booking the transaction: " + e.getMessage());
        }
    }

    @PostMapping("/appointments/{id}/cancel")
    public ResponseEntity<?> cancelAppointment(
            @RequestHeader("Authorization") String token,
            @PathVariable String id) {
        try {
            // Validate token format
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token format");
            }
            
            // Extract and validate token
            String jwtToken = token.substring(7);
            if (jwtService.isTokenExpired(jwtToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token has expired");
            }

            // Get user from token
            Users user = userService.getUser(token);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }

            // Retrieve the transaction
            Transaction transaction = transactionService.getTransaction(id);
            if (transaction == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Appointment not found");
            }
            
            // Check if the user is either the buyer or seller of this transaction
            boolean isBuyer = transaction.getBuyer() != null && transaction.getBuyer().getId().equals(user.getId());
            boolean isSeller = transaction.getSeller() != null && transaction.getSeller().getId().equals(user.getId());
            
            if (!isBuyer && !isSeller) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You don't have permission to cancel this appointment");
            }
            
            // For both buyer and seller cancellations, we remove the buyer information
            // This makes the slot available for booking again
            transaction.setBuyer(null);
            transaction.setBuyerId(null);
            
            // Save the updated transaction
            Transaction updatedTransaction = transactionService.saveTransaction(transaction);
            
            // Ensure the updated transaction has its associated timeslot loaded
            Transaction transactionWithTimeslot = transactionService.getTransactionWithTimeslot(updatedTransaction.getId());
            
            return ResponseEntity.ok(transactionWithTimeslot);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while cancelling the appointment: " + e.getMessage());
        }
    }
}
