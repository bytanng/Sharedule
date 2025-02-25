package com.sharedule.app.service.user;

import com.sharedule.app.dto.UserRegistrationDTO;
import com.sharedule.app.dto.UserProfileUpdateDTO;
import com.sharedule.app.model.user.Users;
import com.sharedule.app.repository.user.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
public class UserServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private JWTService jwtService;

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserService userService;

    private UserRegistrationDTO validRegistrationDTO;
    private Users validUser;

    @BeforeEach
    void setUp() {
        // Set up test data
        validRegistrationDTO = new UserRegistrationDTO();
        validRegistrationDTO.setUsername("testuser123");
        validRegistrationDTO.setEmail("test@gmail.com");
        validRegistrationDTO.setPassword("password123");

        validUser = new Users();
        validUser.setUsername("testuser123");
        validUser.setPassword("password123");
    }

    @Test
    void testSuccessfulRegistration() {
        // Arrange
        when(userRepo.findByUsername(validRegistrationDTO.getUsername())).thenReturn(null);
        when(userRepo.findByEmail(validRegistrationDTO.getEmail())).thenReturn(null);
        when(userRepo.save(any(Users.class))).thenReturn(new Users());

        // Act
        String result = userService.register(validRegistrationDTO);

        // Assert
        assertEquals("User succesfully registered", result);
    }

    @Test
    void testRegistrationWithExistingUsername() {
        // Arrange
        when(userRepo.findByUsername(validRegistrationDTO.getUsername())).thenReturn(new Users());

        // Act
        String result = userService.register(validRegistrationDTO);

        // Assert
        assertEquals("Username is taken", result);
    }

    @Test
    void testRegistrationWithExistingEmail() {
        // Arrange
        when(userRepo.findByUsername(validRegistrationDTO.getUsername())).thenReturn(null);
        when(userRepo.findByEmail(validRegistrationDTO.getEmail())).thenReturn(new Users());

        // Act
        String result = userService.register(validRegistrationDTO);

        // Assert
        assertEquals("Email is taken", result);
    }

    @Test
    void testSuccessfulLogin() {
        // Arrange
        String mockToken = "mock.jwt.token";
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(jwtService.generateToken(validUser.getUsername())).thenReturn(mockToken);

        // Act
        String result = userService.verify(validUser);

        // Assert
        assertEquals(mockToken, result);
    }

    @Test
    void testLoginWithInvalidCredentials() {
        // Arrange
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        // Act
        String result = userService.verify(validUser);

        // Assert
        assertEquals("Authentication failed", result);
    }

    @Test
    void testRegistrationWithInvalidUsername() {
        // Arrange
        validRegistrationDTO.setUsername("test"); // too short

        // Act
        String result = userService.register(validRegistrationDTO);

        // Assert
        assertEquals("Username must be at least 6 characters long", result);
    }

    @Test
    void testRegistrationWithInvalidEmail() {
        // Arrange
        validRegistrationDTO.setEmail("invalid-email");

        // Act
        String result = userService.register(validRegistrationDTO);

        // Assert
        assertEquals("Invalid email format", result);
    }

    @Test
    void testRegistrationWithInvalidPassword() {
        // Arrange
        validRegistrationDTO.setPassword("12"); // too short

        // Act
        String result = userService.register(validRegistrationDTO);

        // Assert
        assertEquals("Password must be at least 3 characters long", result);
    }

    @Test
    void testSuccessfulAccountDeletion() {
        // Arrange
        String username = "testuser123";
        Users user = new Users();
        user.setUsername(username);
        user.setEmail("test@gmail.com");
        when(userRepo.findByUsername(username)).thenReturn(user);

        // Act
        String result = userService.deleteAccount(username);

        // Assert
        assertEquals("Account successfully deleted", result);
    }

    @Test
    void testDeleteNonExistentAccount() {
        // Arrange
        String username = "nonexistentuser";
        when(userRepo.findByUsername(username)).thenReturn(null);

        // Act
        String result = userService.deleteAccount(username);

        // Assert
        assertEquals("User not found", result);
    }

    @Test
    void testDeleteAdminAccount() {
        // Arrange
        String username = "admin";

        // Act
        String result = userService.deleteAccount(username);

        // Assert
        assertEquals("Cannot delete admin account through this endpoint", result);
    }

    @Test
    void testDeleteAccountWithDatabaseError() {
        // Arrange
        String username = "testuser123";
        Users user = new Users();
        user.setUsername(username);
        user.setEmail("test@gmail.com");
        when(userRepo.findByUsername(username)).thenReturn(user);
        doThrow(new RuntimeException("Database connection error"))
            .when(userRepo).delete(user);

        // Act
        String result = userService.deleteAccount(username);

        // Assert
        assertTrue(result.startsWith("Failed to delete account"));
        assertTrue(result.contains("Database connection error"));
    }

    @Test
    void testSuccessfulProfileUpdate() {
        // Arrange
        String username = "testuser123";
        Users user = new Users();
        user.setUsername(username);
        user.setEmail("old@gmail.com");
        user.setDisplayPicture("old-picture.jpg");

        UserProfileUpdateDTO updateDTO = new UserProfileUpdateDTO();
        updateDTO.setEmail("new@gmail.com");
        updateDTO.setDisplayPicture("new-picture.jpg");

        when(userRepo.findByUsername(username)).thenReturn(user);
        when(userRepo.findByEmail("new@gmail.com")).thenReturn(null);
        when(userRepo.save(any(Users.class))).thenReturn(user);

        // Act
        String result = userService.updateProfile(username, updateDTO);

        // Assert
        assertEquals("Profile successfully updated", result);
    }

    @Test
    void testProfileUpdateWithInvalidEmail() {
        // Arrange
        String username = "testuser123";
        Users user = new Users();
        user.setUsername(username);
        user.setEmail("old@gmail.com");

        UserProfileUpdateDTO updateDTO = new UserProfileUpdateDTO();
        updateDTO.setEmail("invalid-email");

        when(userRepo.findByUsername(username)).thenReturn(user);

        // Act
        String result = userService.updateProfile(username, updateDTO);

        // Assert
        assertEquals("Invalid email format", result);
    }

    @Test
    void testProfileUpdateWithExistingEmail() {
        // Arrange
        String username = "testuser123";
        Users user = new Users();
        user.setUsername(username);
        user.setEmail("old@gmail.com");

        UserProfileUpdateDTO updateDTO = new UserProfileUpdateDTO();
        updateDTO.setEmail("existing@gmail.com");

        when(userRepo.findByUsername(username)).thenReturn(user);
        when(userRepo.findByEmail("existing@gmail.com")).thenReturn(new Users());

        // Act
        String result = userService.updateProfile(username, updateDTO);

        // Assert
        assertEquals("Email is already taken", result);
    }

    @Test
    void testProfileUpdateWithSameEmail() {
        // Arrange
        String username = "testuser123";
        Users user = new Users();
        user.setUsername(username);
        user.setEmail("same@gmail.com");

        UserProfileUpdateDTO updateDTO = new UserProfileUpdateDTO();
        updateDTO.setEmail("same@gmail.com");
        updateDTO.setDisplayPicture("new-picture.jpg");

        when(userRepo.findByUsername(username)).thenReturn(user);
        when(userRepo.save(any(Users.class))).thenReturn(user);

        // Act
        String result = userService.updateProfile(username, updateDTO);

        // Assert
        assertEquals("Profile successfully updated", result);
    }

    @Test
    void testProfileUpdateForNonExistentUser() {
        // Arrange
        String username = "nonexistentuser";
        UserProfileUpdateDTO updateDTO = new UserProfileUpdateDTO();
        updateDTO.setEmail("new@gmail.com");

        when(userRepo.findByUsername(username)).thenReturn(null);

        // Act
        String result = userService.updateProfile(username, updateDTO);

        // Assert
        assertEquals("User not found", result);
    }

    @Test
    void testProfileUpdateWithDatabaseError() {
        // Arrange
        String username = "testuser123";
        Users user = new Users();
        user.setUsername(username);
        user.setEmail("old@gmail.com");

        UserProfileUpdateDTO updateDTO = new UserProfileUpdateDTO();
        updateDTO.setEmail("new@gmail.com");

        when(userRepo.findByUsername(username)).thenReturn(user);
        when(userRepo.findByEmail("new@gmail.com")).thenReturn(null);
        when(userRepo.save(any(Users.class))).thenThrow(new RuntimeException("Database connection error"));

        // Act
        String result = userService.updateProfile(username, updateDTO);

        // Assert
        assertTrue(result.startsWith("Failed to update profile"));
        assertTrue(result.contains("Database connection error"));
    }
} 