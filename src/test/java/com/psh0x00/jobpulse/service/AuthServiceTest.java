package com.psh0x00.jobpulse.service;

import com.psh0x00.jobpulse.dto.AuthResponse;
import com.psh0x00.jobpulse.dto.LoginRequest;
import com.psh0x00.jobpulse.exception.ResourceNotFoundException;
import com.psh0x00.jobpulse.model.User;
import com.psh0x00.jobpulse.repository.UserRepository;
import com.psh0x00.jobpulse.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.webmvc.autoconfigure.WebMvcProperties;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

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
}