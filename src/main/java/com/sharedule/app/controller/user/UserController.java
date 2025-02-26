package com.sharedule.app.controller.user;
import com.sharedule.app.service.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sharedule.app.service.user.UserService;
import com.sharedule.app.service.user.JWTService;
import org.springframework.http.ResponseEntity;
import com.sharedule.app.dto.UserRegistrationDTO;
import com.sharedule.app.dto.PasswordResetRequestDTO;
import com.sharedule.app.dto.PasswordResetDTO;
import java.util.List;
import org.springframework.http.HttpStatus;

import com.sharedule.app.model.user.Users;
import com.sharedule.app.dto.UserProfileUpdateDTO;

@RestController
public class UserController {
    
    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JWTService jwtService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegistrationDTO userRegistrationDTO){
        String response = userService.register(userRegistrationDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public String login(@RequestBody Users user){
        return userService.verify(user);
    }

    @GetMapping("/users")
    public List<Users> getAllUsers(){
        return userService.getAllUsers();
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        String response = userService.logout(token);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/user/delete")
    public ResponseEntity<String> deleteAccount(
            @RequestHeader("Authorization") String token,
            @RequestHeader(value = "Confirmation", required = true) String confirmation) {
        try {
            // Validate confirmation
            if (!"CONFIRM_DELETE".equals(confirmation)) {
                return ResponseEntity.badRequest().body("Please provide the confirmation header with value 'CONFIRM_DELETE' to confirm account deletion");
            }

            // Validate token format
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token format");
            }

            // Extract and validate token
            String jwtToken = token.substring(7);
            if (jwtService.isTokenExpired(jwtToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token has expired");
            }

            // Get username from token
            String username = jwtService.extractUserName(jwtToken);
            if (username == null || username.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
            }

            // Attempt to delete the account
            String result = userService.deleteAccount(username);
            
            // Handle different response cases
            switch (result) {
                case "Account successfully deleted":
                    return ResponseEntity.ok()
                        .body("Your account and all associated data have been permanently deleted");
                case "User not found":
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Account not found. It may have been already deleted");
                case "Cannot delete admin account through this endpoint":
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Admin accounts cannot be deleted through this endpoint");
                default:
                    if (result.startsWith("Failed to delete account")) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("An error occurred while deleting your account. Please try again later");
                    }
                    return ResponseEntity.badRequest().body(result);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred while processing your request");
        }
    }

    @PutMapping("/user/profile")
    public ResponseEntity<String> updateProfile(
            @RequestHeader("Authorization") String token,
            @RequestBody UserProfileUpdateDTO profileUpdateDTO) {
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

            // Get username from token
            String username = jwtService.extractUserName(jwtToken);
            if (username == null || username.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
            }

            // Attempt to update the profile
            String result = userService.updateProfile(username, profileUpdateDTO);
            
            // Handle different response cases
            switch (result) {
                case "Profile successfully updated":
                    return ResponseEntity.ok(result);
                case "User not found":
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Account not found");
                case "Email is already taken":
                    return ResponseEntity.badRequest()
                        .body("This email is already registered with another account");
                default:
                    if (result.contains("Invalid email format") || result.contains("Email domain not supported")) {
                        return ResponseEntity.badRequest().body(result);
                    }
                    if (result.startsWith("Failed to update profile")) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("An error occurred while updating your profile. Please try again later");
                    }
                    return ResponseEntity.badRequest().body(result);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred while processing your request");
        }
    }

    @PostMapping("/user/reset-password")
    public ResponseEntity<String> requestPasswordReset(
            @RequestBody PasswordResetRequestDTO resetRequest) {
        try {
            boolean userExists = userService.emailExists(resetRequest.getEmail());
            if (!userExists) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Email address not found");
            }

            String resetToken = userService.generatePasswordResetToken(resetRequest.getEmail());

            // URL subject to change when frontend reset page is done
            String resetUrl = "http://localhost:9001/reset-password?token=" + resetToken;
            String subject = "Sharedule Account Password Reset Request";
            String body = "Click the following link to reset your password: " + resetUrl;
            emailService.sendPasswordResetEmail(resetRequest.getEmail(), subject, body);

            return ResponseEntity.ok("Password reset link has been sent to your email address.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing your request");
        }
    }

    @PutMapping("/user/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestBody PasswordResetDTO passwordResetDTO) {
        try {
            if (jwtService.isTokenExpired(passwordResetDTO.getResetToken())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Reset token has expired");
            }

            // Verify the token and extract email or username
            String email = jwtService.extractUserName(passwordResetDTO.getResetToken());
            if (email == null || email.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid or expired reset token");
            }

            // Validate the new password
            String result = userService.resetPassword(passwordResetDTO);
            if ("Password reset successful".equals(result)) {
                return ResponseEntity.ok("Your password has been successfully reset");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("An error occurred while resetting your password");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while processing your request");
        }
    }
}
