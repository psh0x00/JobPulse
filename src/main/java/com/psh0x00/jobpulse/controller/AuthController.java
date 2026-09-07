package com.psh0x00.jobpulse.controller;

import com.psh0x00.jobpulse.dto.AuthResponse;
import com.psh0x00.jobpulse.dto.LoginRequest;
import com.psh0x00.jobpulse.dto.RegisterRequest;
import com.psh0x00.jobpulse.dto.UserResponse;
import com.psh0x00.jobpulse.model.User;
import com.psh0x00.jobpulse.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;


    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request){
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal User currentUser){
        return ResponseEntity.ok(new UserResponse(currentUser));
    }
}
