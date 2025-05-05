package com.sharedule.app.controller.timeslot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sharedule.app.dto.CreateTimeslotDTO;
import com.sharedule.app.model.timeslot.Timeslot;
import com.sharedule.app.model.transaction.Transaction;
import com.sharedule.app.model.user.Users;
import com.sharedule.app.service.timeslot.TimeslotService;
import com.sharedule.app.service.transaction.TransactionService;
import com.sharedule.app.service.user.JWTService;
import com.sharedule.app.service.user.UserService;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

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
                return createErrorResponse("Invalid token format", HttpStatus.UNAUTHORIZED);
            }
            // Extract and validate token
            String jwtToken = token.substring(7);
            if (jwtService.isTokenExpired(jwtToken)) {
                return createErrorResponse("Token has expired", HttpStatus.UNAUTHORIZED);
            }

            // Get user from token
            Users user = userService.getUser(token);
            if (user == null) {
                return createErrorResponse("User not found", HttpStatus.UNAUTHORIZED);
            }

            Transaction transaction = transactionService.getTransaction(transactionId);
            if (transaction == null) {
                return createErrorResponse("Transaction not found", HttpStatus.NOT_FOUND);
            }

            Timeslot savedTimeslot = timeslotService.createTimeslot(timeslot, transaction);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTimeslot);

        } catch (Exception e) {
            return createErrorResponse("An unexpected error occurred: " + e.getMessage(), 
                                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/timeslot/{transactionId}")
    public ResponseEntity<?> getTimeslotByTransactionId(
            @RequestHeader("Authorization") String token,
            @PathVariable String transactionId) {
        try {
            // Validate token format
            if (token == null || !token.startsWith("Bearer ")) {
                return createErrorResponse("Invalid token format", HttpStatus.UNAUTHORIZED);
            }
            // Extract and validate token
            String jwtToken = token.substring(7);
            if (jwtService.isTokenExpired(jwtToken)) {
                return createErrorResponse("Token has expired", HttpStatus.UNAUTHORIZED);
            }

            // Get user from token
            Users user = userService.getUser(token);
            if (user == null) {
                return createErrorResponse("User not found", HttpStatus.UNAUTHORIZED);
            }

            // Get timeslot
            Timeslot timeslotToByViewed = timeslotService.getTimeslotByTransactionId(transactionId);
            if (timeslotToByViewed == null) {
                return createErrorResponse("Timeslot not found", HttpStatus.NOT_FOUND);
            }

            return ResponseEntity.ok(timeslotToByViewed);
        } catch (Exception e) {
            return createErrorResponse("An unexpected error occurred: " + e.getMessage(),
                                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<Map<String, String>> createErrorResponse(String message, HttpStatus status) {
        Map<String, String> response = new HashMap<>();
        response.put("error", message);
        return ResponseEntity.status(status).body(response);
    }
}
