package com.chatapp.chat.auth;

import com.chatapp.chat.auth.dto.LoginRequest;
import com.chatapp.chat.auth.dto.LoginResponse;
import com.chatapp.chat.auth.dto.RegisterRequest;
import com.chatapp.chat.auth.dto.RegisterResponse;
import com.chatapp.chat.security.JwtUtils;
import com.chatapp.chat.user.User;
import com.chatapp.chat.user.UserRepository;
import com.chatapp.chat.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    AuthService subject;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    JwtUtils jwtUtils;

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        subject = new AuthService(authenticationManager, passwordEncoder, jwtUtils, userRepository);
    }

    @Test
    @DisplayName("Should return RegisterResponse after successful register")
    void shouldReturnRegisterResponseAfterSuccessfulRegister() {
        // given
        RegisterRequest registerRequest = new RegisterRequest(
                "bob@example.com",
                "megaGigaStrongPassword",
                "fuerteBob",
                "Bob",
                "Oldman"
        );
        Instant createdUpdated = Instant.now();
        User saved = new User(
                UUID.randomUUID(),
                "bob@example.com",
                "encoded",
                "Bob",
                "Oldman",
                "fuerteBob",
                UserRole.USER,
                true,
                createdUpdated,
                createdUpdated
        );

        when(passwordEncoder.encode(registerRequest.password())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(saved);

        // when
        RegisterResponse registerResponse = subject.register(registerRequest);

        // then
        assertThat(registerResponse).isNotNull();
        assertThat(registerResponse.email()).isEqualTo("bob@example.com");
        assertThat(registerResponse.username()).isEqualTo("fuerteBob");
        assertThat(registerResponse.id()).isEqualTo(saved.getId());
    }

    @Test
    @DisplayName("Should return LoginResponse after successful login")
    void shouldReturnLoginResponseAfterSuccessfulLogin() {
        // given
        LoginRequest request = new LoginRequest(
                "fuerteBob",
                "megaGigaStrongPassword"
        );

        Authentication auth = mock(Authentication.class);

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(auth.getName()).thenReturn("fuerteBob");
        when(jwtUtils.generateToken("fuerteBob")).thenReturn("jwt-mocked-token");

        // when
        LoginResponse response = subject.login(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.accessToken()).isEqualTo("jwt-mocked-token");
    }
}