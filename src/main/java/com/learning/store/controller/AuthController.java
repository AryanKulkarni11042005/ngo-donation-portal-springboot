package com.learning.store.controller;

import com.learning.store.dto.LoginRequestDto;
import com.learning.store.dto.LoginResponseDto;
import com.learning.store.entity.User;
import com.learning.store.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponseDto login(@Valid @RequestBody LoginRequestDto dto) {
        return authService.login(dto);
    }

    @GetMapping("/profile")
    public Map<String, Object> profile(@AuthenticationPrincipal User user) {
        return Map.of("user", authService.toDto(user));
    }
}
