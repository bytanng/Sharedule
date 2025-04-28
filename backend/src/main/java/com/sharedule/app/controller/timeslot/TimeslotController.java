package com.sharedule.app.controller.timeslot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.sharedule.app.dto.CreateTimeslotDTO;
import com.sharedule.app.model.timeslot.Timeslot;
import com.sharedule.app.model.transaction.Transaction;
import com.sharedule.app.model.user.Users;
import com.sharedule.app.service.timeslot.TimeslotService;
import com.sharedule.app.service.transaction.TransactionService;
import com.sharedule.app.service.user.JWTService;
import com.sharedule.app.service.user.UserService;

import jakarta.validation.Valid;

@RestController
public class TimeslotController {
    @Autowired
    private TimeslotService timeslotService;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private JWTService jwtService;

    @PostMapping("/create-timeslot/{transactionId}")
    public ResponseEntity<?> createTimeslot(
            @RequestHeader("Authorization") String token,
            @PathVariable String transactionId,
            @Valid @RequestBody CreateTimeslotDTO timeslot) {
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

            Transaction transaction = transactionService.getTransaction(transactionId);

            // Create and save the transaction
            Timeslot savedTimeslot = timeslotService.createTimeslot(timeslot, transaction);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTimeslot);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while processing your request: " + e.getMessage());
        }
    }

    @GetMapping("/timeslot/{transactionId}")
    public ResponseEntity<?> getTimeslotByTransactionId(
            @RequestHeader("Authorization") String token,
            @PathVariable String transactionId) {
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

            // Create and save the transaction
            Timeslot timeslotToByViewed = timeslotService.getTimeslotByTransactionId(transactionId);
            return ResponseEntity.status(HttpStatus.OK).body(timeslotToByViewed);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while processing your request: " + e.getMessage());
        }
    }
}
