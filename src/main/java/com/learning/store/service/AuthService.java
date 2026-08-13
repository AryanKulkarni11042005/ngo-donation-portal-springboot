package com.learning.store.service;

import com.learning.store.dto.AuthUserDto;
import com.learning.store.dto.LoginRequestDto;
import com.learning.store.dto.LoginResponseDto;
import com.learning.store.entity.User;
import com.learning.store.exception.UnauthorizedException;
import com.learning.store.repository.UserRepository;
import com.learning.store.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthUserDto toDto(User user) {
        return new AuthUserDto(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    public LoginResponseDto login(LoginRequestDto dto) {
        // Same message for unknown email and wrong password, so the response
        // does not reveal which accounts exist.
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        return new LoginResponseDto(jwtService.generateToken(user), toDto(user));
    }
}
