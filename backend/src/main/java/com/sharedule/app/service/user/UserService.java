package com.sharedule.app.service.user;
import com.sharedule.app.dto.PasswordResetDTO;
import com.sharedule.app.dto.UserRegistrationDTO;
import com.sharedule.app.dto.UserProfileUpdateDTO;
import com.sharedule.app.model.user.Users;
import com.sharedule.app.repository.user.UserRepo;
import com.sharedule.app.util.user.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepo repo;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private AuthenticationManager authManager;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public boolean emailExists(String email){
        return repo.findByEmail(email) != null;
    }

    public String register(UserRegistrationDTO userRegistrationDTO){
        System.out.println("DEBUG - Attempting to register user: " + userRegistrationDTO.getUsername());
        
        // Validate username - set to between 6 and 20 char for now
        String usernameError = ValidationUtil.validateUsername(userRegistrationDTO.getUsername());
        if (usernameError != null) {
            System.out.println("WARN - Username validation failed: " + usernameError);
            return usernameError;
        }

        // Validate email - set to gmail, outlook, hotmail, yahoo for now
        String emailError = ValidationUtil.validateEmail(userRegistrationDTO.getEmail());
        if (emailError != null) {
            System.out.println("WARN - Email validation failed: " + emailError);
            return emailError;
        }

        // Validate password - set to min 3 char for now
        String passwordError = ValidationUtil.validatePassword(userRegistrationDTO.getPassword());
        if (passwordError != null) {
            System.out.println("WARN - Password validation failed: " + passwordError);
            return passwordError;
        }

        //check if username exists
        if(repo.findByUsername(userRegistrationDTO.getUsername()) != null){
            System.out.println("WARN - Username is already taken: " + userRegistrationDTO.getUsername());
            return "Username is taken";
        }
        //check if email exists
        if (repo.findByEmail(userRegistrationDTO.getEmail()) != null){
            System.out.println("WARN - Email is already taken: " + userRegistrationDTO.getEmail());
            return "Email is taken";
        }
        
        //proceed to register if both username and email does not exist
        Users newUser = new Users();
        newUser.setUsername(userRegistrationDTO.getUsername());
        newUser.setEmail(userRegistrationDTO.getEmail());
        String hashedPassword = encoder.encode(userRegistrationDTO.getPassword());
        System.out.println("DEBUG - Generated password hash: " + hashedPassword);
        newUser.setPassword(hashedPassword);
        repo.save(newUser);
        System.out.println("DEBUG - Successfully registered user: " + newUser.getUsername());
        return "User successfully registered";
    }

    public String resetPassword(PasswordResetDTO passwordResetDTO) {
        Users user = repo.findByEmail(passwordResetDTO.getEmail());
        if (user == null) {
            return "No such user with email found";
        }
        String hashedPassword = encoder.encode(passwordResetDTO.getNewPassword());
        System.out.println("DEBUG - Generated password hash: " + hashedPassword);
        user.setPassword(hashedPassword);
        repo.save(user);
        System.out.println("DEBUG - Successfully reset password for user: " + user.getUsername());
        return "Password successfully reset";
    }

    // public Users login(Users user){
    //     return repo.findByUsername(user.getUsername());
    // }

    public String verify(Users user){
        System.out.println("DEBUG - Attempting to verify user: " + user.getUsername());
        System.out.println("DEBUG - Received password: " + user.getPassword());
        try {
            Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );

            if(authentication.isAuthenticated()){
                System.out.println("DEBUG - User authenticated successfully: " + user.getUsername());
                return jwtService.generateToken(user.getUsername());
            }
            System.out.println("WARN - Authentication failed for user: " + user.getUsername());
            return "Authentication failed";
        } catch (Exception e) {
            System.out.println("ERROR - Authentication error for user " + user.getUsername() + ": " + e.getMessage());
            e.printStackTrace();
            return "Authentication error: " + e.getMessage();
        }
    }

    public List<Users> getAllUsers(){
        List<Users> users = repo.findAll();
        System.out.println("DEBUG - Found " + users.size() + " users in database");
        return users;
    }

    public Users getUser(String token) {
        String jwtToken = token.substring(7);
        String usernameFromToken = jwtService.extractUserName(jwtToken);
        Users user = repo.findByUsername(usernameFromToken);
        return user;
    }

    public String logout(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtService.isTokenExpired(token)) {
                System.out.println("DEBUG - Token is already expired");
                return "Token is already expired";
            }
            System.out.println("DEBUG - User logged out successfully");
            return "Logged out successfully";
        }
        System.out.println("WARN - Invalid token format");
        return "Invalid token format";
    }

    public String deleteAccount(String username) {
        System.out.println("DEBUG - Attempting to delete account for user: " + username);
        
        // Don't allow deletion of admin account
        if ("admin".equals(username)) {
            System.out.println("WARN - Attempted to delete admin account");
            return "Cannot delete admin account through this endpoint";
        }

        Users user = repo.findByUsername(username);
        if (user == null) {
            System.out.println("WARN - User not found for deletion: " + username);
            return "User not found";
        }

        try {
            // Log user details before deletion (for audit purposes)
            System.out.println("INFO - Deleting user account - Username: " + username + ", Email: " + user.getEmail());
            
            // Delete the user
            repo.delete(user);
            
            // Log successful deletion
            System.out.println("INFO - Successfully deleted user account and associated data for: " + username);
            return "Account successfully deleted";
        } catch (Exception e) {
            // Log the error with stack trace
            System.out.println("ERROR - Failed to delete user " + username + ": " + e.getMessage());
            e.printStackTrace();
            return "Failed to delete account: " + e.getMessage();
        }
    }

    public String updateProfile(String username, UserProfileUpdateDTO profileUpdateDTO) {
        System.out.println("DEBUG - Attempting to update profile for user: " + username);
        
        Users user = repo.findByUsername(username);
        if (user == null) {
            System.out.println("WARN - User not found for profile update: " + username);
            return "User not found";
        }

        try {
            // Validate and update username if provided
            if (profileUpdateDTO.getUsername() != null && !profileUpdateDTO.getUsername().equals(user.getUsername())) {
                String usernameError = ValidationUtil.validateUsername(profileUpdateDTO.getUsername());
                if (usernameError != null) {
                    System.out.println("WARN - Username validation failed: " + usernameError);
                    return usernameError;
                }

                // Check if username is already taken
                Users existingUserWithUsername = repo.findByUsername(profileUpdateDTO.getUsername());
                if (existingUserWithUsername != null) {
                    System.out.println("WARN - Username is already taken: " + profileUpdateDTO.getUsername());
                    return "Username is already taken";
                }

                user.setUsername(profileUpdateDTO.getUsername());
            }

            // Validate and update email if provided
            if (profileUpdateDTO.getEmail() != null && !profileUpdateDTO.getEmail().equals(user.getEmail())) {
                String emailError = ValidationUtil.validateEmail(profileUpdateDTO.getEmail());
                if (emailError != null) {
                    System.out.println("WARN - Email validation failed: " + emailError);
                    return emailError;
                }

                // Check if email is already taken
                Users existingUserWithEmail = repo.findByEmail(profileUpdateDTO.getEmail());
                if (existingUserWithEmail != null) {
                    System.out.println("WARN - Email is already taken: " + profileUpdateDTO.getEmail());
                    return "Email is already taken";
                }

                user.setEmail(profileUpdateDTO.getEmail());
            }

            // Update display picture if provided
            if (profileUpdateDTO.getDisplayPicture() != null) {
                user.setDisplayPicture(profileUpdateDTO.getDisplayPicture());
            }

            // Save the updated user
            repo.save(user);
            System.out.println("INFO - Successfully updated profile for user: " + username);
            return "Profile successfully updated";
        } catch (Exception e) {
            System.out.println("ERROR - Failed to update profile for user " + username + ": " + e.getMessage());
            e.printStackTrace();
            return "Failed to update profile: " + e.getMessage();
        }
    }

    public String generatePasswordResetToken(String email) {
        String resetToken = jwtService.generateToken(email);
        System.out.println("INFO - Successfully generate reset token: " + resetToken);
        return resetToken;
    }
}
