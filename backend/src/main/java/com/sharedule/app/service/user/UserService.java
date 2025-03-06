package com.sharedule.app.service.user;
import com.sharedule.app.dto.UserRegistrationDTO;
import com.sharedule.app.dto.UserProfileUpdateDTO;
import com.sharedule.app.dto.PasswordResetDTO;
import com.sharedule.app.factory.UserFactory;
import com.sharedule.app.model.user.AppUsers;
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

        // Validate username
        String usernameError = ValidationUtil.validateUsername(userRegistrationDTO.getUsername());
        if (usernameError != null) return usernameError;

        // Validate email
        String emailError = ValidationUtil.validateEmail(userRegistrationDTO.getEmail());
        if (emailError != null) return emailError;

        // Validate password
        String passwordError = ValidationUtil.validatePassword(userRegistrationDTO.getPassword());
        if (passwordError != null) return passwordError;

        // Check if username exists
        if(repo.findByUsername(userRegistrationDTO.getUsername()) != null) return "Username is taken";

        // Check if email exists
        if (repo.findByEmail(userRegistrationDTO.getEmail()) != null) return "Email is taken";

        // Use UserFactory to create a user
        Users newUsers = UserFactory.createUser("USER", userRegistrationDTO.getUsername(),
                userRegistrationDTO.getEmail(), encoder.encode(userRegistrationDTO.getPassword()));

        ((AppUsers) newUsers).setId(null);
        repo.save(newUsers);
        System.out.println("DEBUG - Successfully registered user: " + newUsers.getUsername());
        return "Users successfully registered";
    }


    // public Users login(Users user){
    //     return repo.findByUsername(user.getUsername());
    // }

    public String resetPassword(PasswordResetDTO passwordResetDTO, String email) {
        Users users = repo.findByEmail(email);
        if (users == null) {
            return "No such users with email found";
        }
        String hashedPassword = encoder.encode(passwordResetDTO.getNewPassword());
        System.out.println("DEBUG - Generated password hash: " + hashedPassword);
        ((AppUsers) users).setPassword(hashedPassword);
        repo.save((AppUsers) users);
        System.out.println("DEBUG - Successfully reset password for users: " + users.getUsername());
        return "Password successfully reset";
    }

    public String generatePasswordResetToken(String email) {
        String resetToken = jwtService.generateToken(email);
        System.out.println("INFO - Successfully generate reset token: " + resetToken);
        return resetToken;
    }

    public String verify(Users users){
        System.out.println(users.toString());
        System.out.println("DEBUG - Attempting to verify users: " + users.getUsername());
        System.out.println("DEBUG - Received password: " + users.getPassword());
        try {
            Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(users.getUsername(), users.getPassword())
            );

            if(authentication.isAuthenticated()){
                System.out.println("DEBUG - Users authenticated successfully: " + users.getUsername());
                return jwtService.generateToken(users.getUsername());
            }
            System.out.println("WARN - Authentication failed for users: " + users.getUsername());
            return "Authentication failed";
        } catch (Exception e) {
            System.out.println("ERROR - Authentication error for users " + users.getUsername() + ": " + e.getMessage());
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
        Users users = repo.findByUsername(usernameFromToken);
        return users;
    }

    public String logout(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtService.isTokenExpired(token)) {
                System.out.println("DEBUG - Token is already expired");
                return "Token is already expired";
            }
            System.out.println("DEBUG - Users logged out successfully");
            return "Logged out successfully";
        }
        System.out.println("WARN - Invalid token format");
        return "Invalid token format";
    }

    public String deleteAccount(String username) {
        System.out.println("DEBUG - Attempting to delete account for users: " + username);
        
        // Don't allow deletion of admin account
        if ("admin".equals(username)) {
            System.out.println("WARN - Attempted to delete admin account");
            return "Cannot delete admin account through this endpoint";
        }

        Users users = repo.findByUsername(username);
        if (users == null) {
            System.out.println("WARN - Users not found for deletion: " + username);
            return "Users not found";
        }

        try {
            // Log users details before deletion (for audit purposes)
            System.out.println("INFO - Deleting users account - Username: " + username + ", Email: " + users.getEmail());
            
            // Delete the users
            repo.delete((AppUsers) users);
            
            // Log successful deletion
            System.out.println("INFO - Successfully deleted users account and associated data for: " + username);
            return "Account successfully deleted";
        } catch (Exception e) {
            // Log the error with stack trace
            System.out.println("ERROR - Failed to delete users " + username + ": " + e.getMessage());
            e.printStackTrace();
            return "Failed to delete account: " + e.getMessage();
        }
    }

    public String updateProfile(String username, UserProfileUpdateDTO profileUpdateDTO) {
        System.out.println("DEBUG - Attempting to update profile for users: " + username);
        
        Users users = repo.findByUsername(username);
        if (users == null) {
            System.out.println("WARN - Users not found for profile update: " + username);
            return "Users not found";
        }

        try {
            // Validate and update username if provided
            if (profileUpdateDTO.getUsername() != null && !profileUpdateDTO.getUsername().equals(users.getUsername())) {
                String usernameError = ValidationUtil.validateUsername(profileUpdateDTO.getUsername());
                if (usernameError != null) {
                    System.out.println("WARN - Username validation failed: " + usernameError);
                    return usernameError;
                }

                // Check if username is already taken
                Users existingUsersWithUsername = repo.findByUsername(profileUpdateDTO.getUsername());
                if (existingUsersWithUsername != null) {
                    System.out.println("WARN - Username is already taken: " + profileUpdateDTO.getUsername());
                    return "Username is already taken";
                }

                ((AppUsers) users).setUsername(profileUpdateDTO.getUsername());
            }

            // Validate and update email if provided
            if (profileUpdateDTO.getEmail() != null && !profileUpdateDTO.getEmail().equals(users.getEmail())) {
                String emailError = ValidationUtil.validateEmail(profileUpdateDTO.getEmail());
                if (emailError != null) {
                    System.out.println("WARN - Email validation failed: " + emailError);
                    return emailError;
                }

                // Check if email is already taken
                Users existingUsersWithEmail = repo.findByEmail(profileUpdateDTO.getEmail());
                if (existingUsersWithEmail != null) {
                    System.out.println("WARN - Email is already taken: " + profileUpdateDTO.getEmail());
                    return "Email is already taken";
                }

                ((AppUsers) users).setEmail(profileUpdateDTO.getEmail());
            }

            // Update display picture if provided
            if (profileUpdateDTO.getDisplayPicture() != null) {
                ((AppUsers) users).setDisplayPicture(profileUpdateDTO.getDisplayPicture());
            }

            // Save the updated users
            repo.save((AppUsers) users);
            System.out.println("INFO - Successfully updated profile for users: " + username);
            return "Profile successfully updated: " + jwtService.generateToken(users.getUsername());
        } catch (Exception e) {
            System.out.println("ERROR - Failed to update profile for users " + username + ": " + e.getMessage());
            e.printStackTrace();
            return "Failed to update profile: " + e.getMessage();
        }
    }
}