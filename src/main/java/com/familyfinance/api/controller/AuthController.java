package com.familyfinance.api.controller;

import com.familyfinance.api.dto.request.LoginRequest;
import com.familyfinance.api.dto.request.RegisterRequest;
import com.familyfinance.api.dto.response.AuthResponse;
import com.familyfinance.api.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // Health check — useful for verifying the API is up before starting mobile dev
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("FamilyFinance API is running 🚀");
    }
}
