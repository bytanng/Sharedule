package com.sharedule.app.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sharedule.app.dto.PasswordResetDTO;
import com.sharedule.app.dto.UserRegistrationDTO;
import com.sharedule.app.dto.UserProfileUpdateDTO;
import com.sharedule.app.model.user.AppUsers;
import com.sharedule.app.model.user.Users;
import com.sharedule.app.service.user.UserService;
import com.sharedule.app.service.user.JWTService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private JWTService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private AppUsers appUser;
    private UserRegistrationDTO registrationDTO;
    private PasswordResetDTO passwordResetDTO;
    private UserProfileUpdateDTO profileUpdateDTO;

    private static final String VALID_TOKEN = "Bearer valid.jwt.token";
    private static final String INVALID_TOKEN = "invalid.token";
    private static final String EXPIRED_TOKEN = "Bearer expired.jwt.token";

    @Test
    public void testSuccessfulRegistration() throws Exception {
        // Arrange
        registrationDTO = new UserRegistrationDTO();
        registrationDTO.setUsername("johnDoe");
        registrationDTO.setEmail("john.doe@hotmail.com");
        registrationDTO.setPassword("password123");
        registrationDTO.setRole("USER");

        when(userService.register(any(UserRegistrationDTO.class)))
                .thenReturn("DEBUG - Successfully registered user: "+registrationDTO.getUserName());

        // Act & Assert
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Users succesfully registered"));
    }
//
//    @Test
//    public void testRegistrationWithExistingUsername() throws Exception {
//        // Arrange
//        UserRegistrationDTO registrationDTO = new UserRegistrationDTO();
//        registrationDTO.setUsername("existinguser");
//        registrationDTO.setEmail("test@gmail.com");
//        registrationDTO.setPassword("password123");
//
//        when(userService.register(any(UserRegistrationDTO.class)))
//                .thenReturn("Username is taken");
//
//        // Act & Assert
//        mockMvc.perform(post("/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(registrationDTO)))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Username is taken"));
//    }
//
//    @Test
//    public void testRegistrationWithInvalidEmail() throws Exception {
//        // Arrange
//        UserRegistrationDTO registrationDTO = new UserRegistrationDTO();
//        registrationDTO.setUsername("testuser");
//        registrationDTO.setEmail("invalid-email");
//        registrationDTO.setPassword("password123");
//
//        when(userService.register(any(UserRegistrationDTO.class)))
//                .thenReturn("Invalid email format");
//
//        // Act & Assert
//        mockMvc.perform(post("/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(registrationDTO)))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Invalid email format"));
//    }
//
//    @Test
//    public void testSuccessfulLogin() throws Exception {
//        // Arrange
//        Users loginUser = new Users();
//        loginUser.setUsername("testuser");
//        loginUser.setPassword("password123");
//
//        String mockToken = "mock.jwt.token";
//        when(userService.verify(any(Users.class)))
//                .thenReturn(mockToken);
//
//        // Act & Assert
//        mockMvc.perform(post("/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(loginUser)))
//                .andExpect(status().isOk())
//                .andExpect(content().string(mockToken));
//    }
//
//    @Test
//    public void testLoginWithInvalidCredentials() throws Exception {
//        // Arrange
//        Users loginUser = new Users();
//        loginUser.setUsername("testuser");
//        loginUser.setPassword("wrongpassword");
//
//        when(userService.verify(any(Users.class)))
//                .thenReturn("Authentication failed");
//
//        // Act & Assert
//        mockMvc.perform(post("/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(loginUser)))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Authentication failed"));
//    }
//
//    @Test
//    public void testRegistrationWithShortUsername() throws Exception {
//        // Arrange
//        UserRegistrationDTO registrationDTO = new UserRegistrationDTO();
//        registrationDTO.setUsername("test"); // less than 6 characters
//        registrationDTO.setEmail("test@gmail.com");
//        registrationDTO.setPassword("password123");
//
//        when(userService.register(any(UserRegistrationDTO.class)))
//                .thenReturn("Username must be at least 6 characters long");
//
//        // Act & Assert
//        mockMvc.perform(post("/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(registrationDTO)))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Username must be at least 6 characters long"));
//    }
//
//    @Test
//    public void testRegistrationWithShortPassword() throws Exception {
//        // Arrange
//        UserRegistrationDTO registrationDTO = new UserRegistrationDTO();
//        registrationDTO.setUsername("testuser");
//        registrationDTO.setEmail("test@gmail.com");
//        registrationDTO.setPassword("12"); // less than 3 characters
//
//        when(userService.register(any(UserRegistrationDTO.class)))
//                .thenReturn("Password must be at least 3 characters long");
//
//        // Act & Assert
//        mockMvc.perform(post("/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(registrationDTO)))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Password must be at least 3 characters long"));
//    }
//
//    @Test
//    public void testSuccessfulLogout() throws Exception {
//        // Arrange
//        String token = "Bearer mock.jwt.token";
//        when(userService.logout(token))
//                .thenReturn("Logged out successfully");
//
//        // Act & Assert
//        mockMvc.perform(post("/logout")
//                        .header("Authorization", token))
//                .andExpect(status().isNoContent());
//    }
//
//    @Test
//    public void testLogoutWithInvalidToken() throws Exception {
//        // Arrange
//        String token = "InvalidToken";
//        when(userService.logout(token))
//                .thenReturn("Invalid token format");
//
//        // Act & Assert
//        mockMvc.perform(post("/logout")
//                        .header("Authorization", token))
//                .andExpect(status().isNoContent());
//    }
}