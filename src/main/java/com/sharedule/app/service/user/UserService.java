package com.sharedule.app.service.user;
import com.sharedule.app.dto.UserRegistrationDTO;
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
        return "User succesfully registered";
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

}