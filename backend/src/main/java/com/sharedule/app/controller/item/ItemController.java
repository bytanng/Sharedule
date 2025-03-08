package com.sharedule.app.controller.item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.sharedule.app.dto.CreateItemDTO;
import com.sharedule.app.dto.EditItemDTO;
import com.sharedule.app.exception.BackendErrorException;
import com.sharedule.app.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.sharedule.app.service.user.JWTService;
import com.sharedule.app.model.user.Users;
import com.sharedule.app.model.item.Item;
import com.sharedule.app.service.item.ItemService;
import jakarta.validation.Valid;

import java.util.List;

@RestController
public class ItemController {
    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private JWTService jwtService;

    @PostMapping("/create-item")
    public ResponseEntity<?> createItem(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody CreateItemDTO item) {
        try {
            // Validate token format
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token format");
            }
            System.out.println("DEBUG - TESTTT");
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

            // Create and save the item
            Item savedItem = itemService.createItem(item, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedItem);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while processing your request: " + e.getMessage());
        }
    }

    @GetMapping("/user/items")
    public ResponseEntity<?> getUserItems(@RequestHeader("Authorization") String token) {
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
            List<Item> items = itemService.getItemsByUser(user);
            return ResponseEntity.ok(items);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching items: " + e.getMessage());
        }
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<?> viewItem(
        @PathVariable String itemId,
        @RequestHeader("Authorization") String token) {
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

            Item itemToBeViewed = itemService.viewItem(itemId);

            if (!user.equals(itemToBeViewed.getUser())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to view this item");
            }

            return ResponseEntity.status(HttpStatus.OK).body(itemToBeViewed);
        } catch (BackendErrorException bee) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Item not found"); 
        }
    }

    @PutMapping("/item/{itemId}")
    public ResponseEntity<?> editItem(
            @PathVariable String itemId,
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody EditItemDTO updatedItem) {
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

            // Retrieve the item
            Item existingItem = itemService.viewItem(itemId);
            if (existingItem == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found");
            }

            // Check if the user is the owner of the item
            if (!existingItem.getUser().equals(user)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to edit this item");
            }

            // Update item details
            Item updatedItemEntity = itemService.updateItem(existingItem, updatedItem);

            return ResponseEntity.ok(updatedItemEntity);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while updating the item: " + e.getMessage());
        }
    }
}
