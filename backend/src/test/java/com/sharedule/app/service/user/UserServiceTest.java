package com.sharedule.app.service.user;

import com.sharedule.app.dto.UserRegistrationDTO;
import com.sharedule.app.dto.PasswordResetDTO;
import com.sharedule.app.dto.UserProfileUpdateDTO;
import com.sharedule.app.exception.BackendErrorException;
import com.sharedule.app.exception.ExistsInRepoException;
import com.sharedule.app.model.user.AppUsers;
import com.sharedule.app.model.user.Users;
import com.sharedule.app.repository.user.UserRepo;
import com.sharedule.app.util.user.ValidationUtil;
import com.sharedule.app.service.user.JWTService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private JWTService jwtService;

    @Mock
    private AuthenticationManager authManager;

    @InjectMocks
    private UserService userService;

    private AppUsers appUser;
    private UserRegistrationDTO registrationDTO;
    private PasswordResetDTO passwordResetDTO;
    private UserProfileUpdateDTO profileUpdateDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        appUser = new AppUsers();
        appUser.setUsername("testUser");
        appUser.setEmail("test@hotmail.com.com");
        appUser.setPassword("hashedPassword");
        appUser.setRole("USER");


        registrationDTO = new UserRegistrationDTO();
        registrationDTO.setUsername("johnDoe");
        registrationDTO.setEmail("john.doe@hotmail.com");
        registrationDTO.setPassword("password123");
        registrationDTO.setRole("USER");



        passwordResetDTO = new PasswordResetDTO();
        passwordResetDTO.setNewPassword("newPassword");

        profileUpdateDTO = new UserProfileUpdateDTO();
        profileUpdateDTO.setUsername("newUsername");
        profileUpdateDTO.setEmail("new@hotmail.com");
    }

    @Test
    void testRegisterUser_Success() throws BackendErrorException {
        when(userRepo.findByUsername(anyString())).thenReturn(null);
        when(userRepo.findByEmail(anyString())).thenReturn(null);

        userService.register(registrationDTO);

        verify(userRepo, times(1)).save(any(Users.class));
    }

    @Test
    void testRegisterUser_EmailExists() throws BackendErrorException {
        when(userRepo.findByEmail(anyString())).thenReturn(new AppUsers());

        BackendErrorException exception = assertThrows(BackendErrorException.class, () -> userService.register(registrationDTO));
        assertEquals("Email is taken", exception.getMessage());
    }

    @Test
    void testResetPassword_Success() {
        // Setup mock behaviors
        when(userRepo.findByEmail("john.doe@hotmail.com")).thenReturn(appUser);
        when(jwtService.generateToken(anyString())).thenReturn("validToken");

        // Call the method under test
        String result = userService.resetPassword(passwordResetDTO, "john.doe@hotmail.com");

        // Assert the expected outcome
        assertEquals("Password successfully reset", result);
    }


    @Test
    void testGeneratePasswordResetToken() {
        when(jwtService.generateToken(anyString())).thenReturn("resetToken");

        String result = userService.generatePasswordResetToken("test@example.com");

        assertEquals("resetToken", result);
    }

    @Test
    void testUpdateProfile_Success() {
        // Ensure appUser is set up correctly
        when(userRepo.findByUsername("testUser")).thenReturn(appUser);

        // Execute the service method
        String result = userService.updateProfile("testUser", profileUpdateDTO);

        // Check that the result contains the expected success message
        assertTrue(result.contains("Profile successfully updated"));

        // Verify that save was called once on the repository
        verify(userRepo, times(1)).save(any(Users.class));
    }


    @Test
    void testDeleteAccount_Success() {
        when(userRepo.findByUsername(anyString())).thenReturn(appUser);

        String result = userService.deleteAccount("testUser");

        assertEquals("Account successfully deleted", result);
        verify(userRepo, times(1)).delete(any(Users.class));
    }

    @Test
    void testDeleteAccount_Fail() {
        when(userRepo.findByUsername(anyString())).thenReturn(null);

        String result = userService.deleteAccount("testUser");

        assertEquals("Users not found", result);
    }


    @Test
    void testLogout_Success() {
        String result = userService.logout("Bearer validToken");

        assertEquals("Logged out successfully", result);
    }

    @Test
    void testLogout_InvalidTokenFormat() {
        String result = userService.logout("InvalidToken");

        assertEquals("Invalid token format", result);
    }
}
