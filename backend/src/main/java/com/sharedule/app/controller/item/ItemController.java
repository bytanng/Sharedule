package com.sharedule.app.controller.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.sharedule.app.dto.CreateItemDTO;
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

    @GetMapping("/items")
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
    public ResponseEntity<?> getItem(
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

            Item itemToBeViewed = itemService.getItem(itemId);

            if (!user.equals(itemToBeViewed.getUser())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to view this item");
            }

            return ResponseEntity.status(HttpStatus.OK).body(itemToBeViewed);
        } catch (BackendErrorException bee) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Item not found");
        }
    }

    @GetMapping("/items/search")
    public ResponseEntity<?> searchItems(
            @RequestParam String query,
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

            List<Item> itemsFound = itemService.searchItems(query);

            for (Item item : itemsFound) {
                if (!user.equals(item.getUser())) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body("You are not authorized to view this item");
                }
            }

            return ResponseEntity.status(HttpStatus.OK).body(itemsFound);
        } catch (BackendErrorException bee) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bee.getMessage());
        }
    }

    @GetMapping("/products")
    public ResponseEntity<?> getProducts() {
        try {
            List<Item> productsFound = itemService.getProducts();
            return ResponseEntity.status(HttpStatus.OK).body(productsFound);
        } catch (BackendErrorException bee) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bee.getMessage());
        }
    }

    @GetMapping("/product/{itemId}")
    public ResponseEntity<?> getProduct(@PathVariable String itemId) {
        try {
            Item productFound = itemService.getItem(itemId);
            return ResponseEntity.status(HttpStatus.OK).body(productFound);
        } catch (BackendErrorException bee) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Item not found");
        }
    }

    @GetMapping("/products/search")
    public ResponseEntity<?> searchProducts(@RequestParam String query) {
        try {
            List<Item> productsFound = itemService.searchProducts(query);
            return ResponseEntity.status(HttpStatus.OK).body(productsFound);
        } catch (BackendErrorException bee) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bee.getMessage());
        }
    }

    @DeleteMapping("/item/{itemId}/delete")
    public ResponseEntity<String> deleteItem(
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

            Item itemToBeDeleted = itemService.getItem(itemId);

            if (itemToBeDeleted == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Item not found");
            }

            if (!user.equals(itemToBeDeleted.getUser())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("You are not authorized to delete this item");
            }

            String result = itemService.deleteItem(itemId);

            // Handle different response cases
            switch (result) {
                case "Item successfully deleted":
                    return ResponseEntity.ok()
                            .body("The item has been permanently deleted");
                case "Item not found":
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Item not found. It may have been already deleted");
                default:
                    if (result.startsWith("Failed to delete item")) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("An error occurred while deleting your item. Please try again later");
                    }
                    return ResponseEntity.badRequest().body(result);
            }
        } catch (BackendErrorException bee) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Item not found");
        }
    }
}
