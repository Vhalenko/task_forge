package com.taskforge.service;

import com.taskforge.model.dto.AuthDTO;
import com.taskforge.model.dto.UserDTO;
import com.taskforge.model.dto.request.LoginRequest;
import com.taskforge.model.dto.request.RegisterRequest;
import com.taskforge.model.entity.User;
import com.taskforge.model.enums.UserRole;
import com.taskforge.support.IntegrationTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthServiceTest extends IntegrationTestSupport {

    @Autowired
    private AuthService authService;

    @Test
    void registerCreatesUserWithNormalizedEmailAndHashedPassword() {
        RegisterRequest request = new RegisterRequest(
                "Maya", "Lindqvist", "maya", "Maya@Example.COM", "secret123");

        UserDTO result = authService.register(request);

        assertThat(result.email()).isEqualTo("maya@example.com");
        assertThat(result.username()).isEqualTo("maya");
        assertThat(result.role()).isEqualTo(UserRole.USER.name());
        assertThat(result.isBlocked()).isFalse();

        User saved = userRepository.findByEmailIgnoreCase("maya@example.com").orElseThrow();
        assertThat(saved.getPasswordHash()).isNotEqualTo("secret123");
        assertThat(passwordEncoder.matches("secret123", saved.getPasswordHash())).isTrue();
    }

    @Test
    void registerFallsBackToFullNameWhenUsernameIsBlank() {
        RegisterRequest request = new RegisterRequest(
                "Maya", "Lindqvist", "   ", "maya@example.com", "secret123");

        UserDTO result = authService.register(request);

        assertThat(result.username()).isEqualTo("Maya Lindqvist");
    }

    @Test
    void registerRejectsDuplicateEmailIgnoringCase() {
        createUser("Maya", "Lindqvist", "maya@example.com", UserRole.USER, null, false);

        RegisterRequest request = new RegisterRequest(
                "Other", "Person", "other", "MAYA@EXAMPLE.COM", "secret123");

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> assertThat(
                        ((ResponseStatusException) exception).getStatusCode().value()).isEqualTo(409));
        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    void loginReturnsTokenForValidCredentials() {
        User user = createUser("Maya", "Lindqvist", "maya@example.com", UserRole.USER, null, false);

        AuthDTO result = authService.login(new LoginRequest("MAYA@example.com", DEFAULT_PASSWORD));

        assertThat(result.token()).isNotBlank();
        assertThat(jwtService.extractUsername(result.token())).isEqualTo("maya@example.com");
        assertThat(result.user().id()).isEqualTo(user.getId());
    }

    @Test
    void loginRejectsWrongPassword() {
        createUser("Maya", "Lindqvist", "maya@example.com", UserRole.USER, null, false);

        assertThatThrownBy(() ->
                authService.login(new LoginRequest("maya@example.com", "wrong-password")))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void loginRejectsUnknownEmail() {
        assertThatThrownBy(() ->
                authService.login(new LoginRequest("nobody@example.com", DEFAULT_PASSWORD)))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void loginRejectsBlockedAccount() {
        createUser("Maya", "Lindqvist", "maya@example.com", UserRole.USER, null, true);

        assertThatThrownBy(() ->
                authService.login(new LoginRequest("maya@example.com", DEFAULT_PASSWORD)))
                .isInstanceOf(LockedException.class);
    }
}
