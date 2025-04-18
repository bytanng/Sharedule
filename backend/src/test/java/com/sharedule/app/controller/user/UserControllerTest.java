package com.sharedule.app.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sharedule.app.dto.PasswordResetDTO;
import com.sharedule.app.dto.PasswordResetRequestDTO;
import com.sharedule.app.dto.UserProfileUpdateDTO;
import com.sharedule.app.dto.UserRegistrationDTO;
import com.sharedule.app.exception.BackendErrorException;
import com.sharedule.app.model.user.AppUsers;
import com.sharedule.app.model.user.Users;
import com.sharedule.app.repository.user.UserRepo;
import com.sharedule.app.service.transaction.TransactionService;
import com.sharedule.app.service.user.UserService;
import com.sharedule.app.service.email.EmailService;
import com.sharedule.app.service.user.JWTService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserController.class)  // This loads only the controller for testing
public class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;  // Automatically injected by Spring

    @MockBean
    private UserService userService;  // Mock service layer

    @MockBean
    private EmailService emailService;  // Mock any other dependencies

    @MockBean
    private JWTService jwtService;

    @MockBean
    private UserRepo userRepo;

    @MockBean
    private BCryptPasswordEncoder encoder;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private SecurityFilterChain securityFilterChain;

    @MockBean
    private TransactionService transactionService;

    private UserRegistrationDTO registrationDTO;

    private AppUsers appUser;

    @BeforeEach
    void setUp() {
        // Initialize registrationDTO with test data
        registrationDTO = new UserRegistrationDTO();
        registrationDTO.setUsername("johnDoe");
        registrationDTO.setEmail("john.doe@hotmail.com");
        registrationDTO.setPassword("password123");
        registrationDTO.setRole("USER");
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password"));
        appUser = new AppUsers();
        appUser.setUsername("testUser");
        appUser.setEmail("test@hotmail.com.com");
        appUser.setPassword("hashedPassword");
        appUser.setRole("USER");


    }

    @Test
    void testRegisterUser() throws Exception {


        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().string("User successfully registered"));

    }


    @Test
    void testLogin_Success() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(appUser)))
                .andExpect(status().isOk());
    }


    @Test
    void testGetAllUsers() throws Exception {
        // Mock the retrieval of users
        AppUsers appUser1 = new AppUsers();
        appUser1.setUsername("testUser1");
        appUser1.setEmail("test1@hotmail.com.com");
        appUser1.setPassword("hashedPassword");
        appUser1.setRole("USER");

        AppUsers appUser2 = new AppUsers();
        appUser2.setUsername("testUser2");
        appUser2.setEmail("test2@hotmail.com.com");
        appUser2.setPassword("hashedPassword");
        appUser2.setRole("USER");
        when(userService.getAllUsers()).thenReturn(List.of(appUser1, appUser2));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk()) // Expecting HTTP 200 OK
                .andExpect(jsonPath("$[0].username").value("testUser1")) // Check first user
                .andExpect(jsonPath("$[1].username").value("testUser2")) // Check second user
                .andExpect(jsonPath("$", hasSize(2))); // Verify the list size
    }
    @Test
    void testGetUser() throws Exception {
        AppUsers mockUser = new AppUsers();
        mockUser.setUsername("testUser");
        mockUser.setEmail("test@hotmail.com");
        mockUser.setPassword("hashedPassword");
        mockUser.setRole("USER");

        when(userService.getUser(anyString())).thenReturn(mockUser);

        mockMvc.perform(get("/user/profile")
                        .header("Authorization", "Bearer validToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.email").value("test@hotmail.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void testLogout() throws Exception {
        when(userService.logout(anyString())).thenReturn("User logged out successfully");

        mockMvc.perform(post("/logout")
                        .header("Authorization", "Bearer validToken"))
                .andExpect(status().isOk())
                .andExpect(content().string("User logged out successfully"));
    }
    @Test
    void testDeleteAccount() throws Exception {
        // Mock the service to simulate a successful account deletion
        when(userService.deleteAccount(anyString())).thenReturn("Account successfully deleted");
        when(jwtService.isTokenExpired(anyString())).thenReturn(false);
        when(jwtService.extractUserName(anyString())).thenReturn("testUser");
        mockMvc.perform(delete("/user/delete")
                        .header("Authorization", "Bearer validToken")
                        .header("Confirmation", "CONFIRM_DELETE"))
                .andExpect(status().isOk())
                .andExpect(content().string("Your account and all associated data have been permanently deleted"));
    }

    @Test
    void testDeleteAccount_InvalidConfirmation() throws Exception {
        mockMvc.perform(delete("/user/delete")
                        .header("Authorization", "Bearer validToken")
                        .header("Confirmation", "INVALID_CONFIRMATION"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Please provide the confirmation header with value 'CONFIRM_DELETE' to confirm account deletion"));
    }

    @Test
    void testDeleteAccount_ExpiredToken() throws Exception {
        when(jwtService.isTokenExpired(anyString())).thenReturn(true);
        mockMvc.perform(delete("/user/delete")
                        .header("Authorization", "Bearer expiredToken")
                        .header("Confirmation", "CONFIRM_DELETE"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Token has expired"));
    }
    @Test
    void testUpdateProfile() throws Exception {
        // Setup the mock user
        AppUsers mockUser = new AppUsers();
        mockUser.setUsername("testUser");
        mockUser.setEmail("test@hotmail.com");
        mockUser.setPassword("hashedPassword");
        mockUser.setRole("USER");

        // Setup the profile update data
        UserProfileUpdateDTO profileUpdateDTO = new UserProfileUpdateDTO();
        profileUpdateDTO.setEmail("newemail@hotmail.com");

        // Mock the service to simulate a profile update
        when(userService.updateProfile(anyString(), any(UserProfileUpdateDTO.class)))
                .thenReturn("Profile successfully updated: newemail@hotmail.com");
        when(jwtService.extractUserName(anyString())).thenReturn("testUser");

        mockMvc.perform(put("/user/update-profile")
                        .header("Authorization", "Bearer validToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Profile successfully updated: newemail@hotmail.com"));
    }

    @Test
    void testUpdateProfile_InvalidToken() throws Exception {
        UserProfileUpdateDTO profileUpdateDTO = new UserProfileUpdateDTO();
        profileUpdateDTO.setEmail("newemail@hotmail.com");

        mockMvc.perform(put("/user/update-profile")
                        .header("Authorization", "Bearer invalidToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileUpdateDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid token"));
    }
    @Test
    void testRequestPasswordReset() throws Exception {
        // Mock the service to simulate email check and sending of reset link
        when(userService.emailExists(anyString())).thenReturn(true);
        when(userService.generatePasswordResetToken(anyString())).thenReturn("resetToken");

        mockMvc.perform(post("/user/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PasswordResetRequestDTO("test@hotmail.com"))))
                .andExpect(status().isOk())
                .andExpect(content().string("Password reset link has been sent to your email address."));
    }

    @Test
    void testRequestPasswordReset_EmailNotFound() throws Exception {
        when(userService.emailExists(anyString())).thenReturn(false);

        mockMvc.perform(post("/user/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PasswordResetRequestDTO("invalidemail@hotmail.com"))))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Email address not found"));
    }
    @Test
    void testResetPassword() throws Exception {
        // Setup the password reset DTO
        PasswordResetDTO resetDTO = new PasswordResetDTO("resetToken", "newPassword123");

        // Mock the service to simulate password reset
        when(userService.resetPassword(any(PasswordResetDTO.class), anyString())).thenReturn("Password successfully reset");
        when(jwtService.extractUserName(anyString())).thenReturn("testUser");

        mockMvc.perform(put("/user/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Your password has been successfully reset"));
    }

    @Test
    void testResetPassword_TokenExpired() throws Exception {
        PasswordResetDTO resetDTO = new PasswordResetDTO("expiredToken", "newPassword123");
        when(jwtService.isTokenExpired(anyString())).thenReturn(true);

        mockMvc.perform(put("/user/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Reset token has expired"));
    }

}
