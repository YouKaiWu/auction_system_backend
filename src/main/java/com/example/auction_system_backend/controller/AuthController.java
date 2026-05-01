package com.example.auction_system_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.example.auction_system_backend.DTO.auth.LoginRequest;
import com.example.auction_system_backend.DTO.auth.RegisterRequest;
import com.example.auction_system_backend.DTO.auth.AuthResponse;
import com.example.auction_system_backend.entity.User;
import com.example.auction_system_backend.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // =========================
    // POST /api/auth/register
    // =========================
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest req) {

        authService.register(req);
        return ResponseEntity.ok("Register success");
    }

    // =========================
    // POST /api/auth/login
    // =========================
    @PostMapping("/login")
    public AuthResponse login(
            @RequestBody LoginRequest req) {

        String token = authService.login(req);

        return new AuthResponse(token);
    }

    // =========================
    // GET /api/auth/me
    // =========================
    @GetMapping("/me")
    public User me(@AuthenticationPrincipal Long authId) {
        System.out.println("test:"+authId);
        return authService.getMe(authId);
    }
}