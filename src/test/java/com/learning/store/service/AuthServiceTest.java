package com.learning.store.service;

import com.learning.store.dto.LoginRequestDto;
import com.learning.store.dto.LoginResponseDto;
import com.learning.store.entity.User;
import com.learning.store.exception.UnauthorizedException;
import com.learning.store.model.Role;
import com.learning.store.repository.UserRepository;
import com.learning.store.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setName("Admin");
        user.setEmail("admin@ngo.org");
        user.setPasswordHash("hashed-password");
        user.setRole(Role.ADMIN);
    }

    private LoginRequestDto request(String email, String password) {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail(email);
        dto.setPassword(password);
        return dto;
    }

    @Test
    @DisplayName("Valid credentials return a token and the user details")
    void loginWithValidCredentialsReturnsToken() {
        when(userRepository.findByEmail("admin@ngo.org")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret", "hashed-password")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("signed-jwt");

        LoginResponseDto response = authService.login(request("admin@ngo.org", "secret"));

        assertThat(response.getToken()).isEqualTo("signed-jwt");
        assertThat(response.getUser().getEmail()).isEqualTo("admin@ngo.org");
        assertThat(response.getUser().getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    @DisplayName("An unknown email is rejected without checking a password")
    void loginWithUnknownEmailThrows() {
        when(userRepository.findByEmail("nobody@ngo.org")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request("nobody@ngo.org", "secret")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Invalid email or password");

        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    @DisplayName("A wrong password is rejected and no token is issued")
    void loginWithWrongPasswordThrows() {
        when(userRepository.findByEmail("admin@ngo.org")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed-password")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request("admin@ngo.org", "wrong")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Invalid email or password");

        verify(jwtService, never()).generateToken(any());
    }

    @Test
    @DisplayName("Unknown email and wrong password fail identically, so neither reveals which accounts exist")
    void loginFailuresAreIndistinguishable() {
        when(userRepository.findByEmail("nobody@ngo.org")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("admin@ngo.org")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed-password")).thenReturn(false);

        String unknownEmail = assertThatUnauthorizedMessage("nobody@ngo.org", "secret");
        String wrongPassword = assertThatUnauthorizedMessage("admin@ngo.org", "wrong");

        assertThat(unknownEmail).isEqualTo(wrongPassword);
    }

    private String assertThatUnauthorizedMessage(String email, String password) {
        try {
            authService.login(request(email, password));
            throw new AssertionError("Expected login to fail for " + email);
        } catch (UnauthorizedException ex) {
            return ex.getMessage();
        }
    }
}
