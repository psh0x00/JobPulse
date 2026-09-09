package com.psh0x00.jobpulse.service;

import com.psh0x00.jobpulse.dto.AuthResponse;
import com.psh0x00.jobpulse.dto.LoginRequest;
import com.psh0x00.jobpulse.dto.RegisterRequest;
import com.psh0x00.jobpulse.exception.DuplicateResourceException;
import com.psh0x00.jobpulse.exception.ResourceNotFoundException;
import com.psh0x00.jobpulse.model.User;
import com.psh0x00.jobpulse.repository.UserRepository;
import com.psh0x00.jobpulse.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;


    @Test
    void testLogin_UserNotFound(){

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("nonexistent@example.com");
        loginRequest.setPassword("password");

        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(ResourceNotFoundException.class, () -> {
            authService.login(loginRequest);
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testLogin_Successful() {

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("email@example.com");
        loginRequest.setPassword("password123");

        User user = new User();
        user.setEmail("email@example.com");

        when(userRepository.findByEmail("email@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("my-fake-token");

        AuthResponse authResponse = authService.login(loginRequest);

        assertEquals("my-fake-token", authResponse.getToken());
    }

    @Test
    void testRegister_Sucess(){

        RegisterRequest registerRequest = new RegisterRequest();

        registerRequest.setName("John Doe");
        registerRequest.setEmail("email@example.com");
        registerRequest.setPassword("password123");

        when(userRepository.findByEmail("email@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        when(jwtService.generateToken(any(User.class))).thenReturn("my-fake-token");

        AuthResponse authResponse = authService.register(registerRequest);

        assertNotNull(authResponse);
        assertEquals("my-fake-token", authResponse.getToken());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegister_DuplicateEmail(){

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("John Doe");
        registerRequest.setEmail("email@example.com");
        registerRequest.setPassword("password123");

        when(userRepository.findByEmail("email@example.com")).thenReturn(Optional.of(new User()));

        RuntimeException exception = assertThrows(DuplicateResourceException.class, () -> {
            authService.register(registerRequest);
        });

        assertEquals("Email already registered", exception.getMessage());
    }

    @Test
    void testLogin_WrongPassword(){

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("email@example.com");
        loginRequest.setPassword("wrongpassword");


        when(authenticationManager.authenticate(any())).thenThrow(new RuntimeException("Wrong password"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(loginRequest);
        });

        assertEquals("Wrong password", exception.getMessage());
    }
}