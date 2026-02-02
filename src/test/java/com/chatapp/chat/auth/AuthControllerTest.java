package com.chatapp.chat.auth;

import com.chatapp.chat.auth.dto.LoginRequest;
import com.chatapp.chat.auth.dto.LoginResponse;
import com.chatapp.chat.auth.dto.RegisterRequest;
import com.chatapp.chat.auth.dto.RegisterResponse;
import com.chatapp.chat.security.JwtAuthenticationFilter;
import com.chatapp.chat.security.JwtUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(MockedAuthServiceConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthService authService;

    @MockitoBean
    JwtUtils jwtUtils;

    @MockitoBean
    JwtAuthenticationFilter filter;

    @MockitoBean
    PasswordEncoder encoder;


    @Test
    @DisplayName("POST /api/auth should return 201 Created with RegisterResponse")
    void shouldRegisterUser() throws Exception {
        // given
        RegisterRequest registerRequest = new RegisterRequest(
                "charlie@example.com",
                "myStrongPassword",
                "charlieABC",
                "Charlie",
                "Johnson"
        );

        RegisterResponse registerResponse = new RegisterResponse(
                UUID.randomUUID(),
                "charlie@example.com",
                "charlieABC"
        );

        Mockito.when(authService.register(any(RegisterRequest.class)))
                .thenReturn(registerResponse);

        // when + then
        mockMvc.perform(post("/api/auth/register")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(registerRequest))
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("charlie@example.com"))
                .andExpect(jsonPath("$.username").value("charlieABC"));
    }

    @Test
    @DisplayName("POST /api/auth/login should return 200 OK with LoginResponse")
    void shouldLoginUser() throws Exception {
        // given
        LoginRequest loginRequest = new LoginRequest("charlie@example.com", "myStrongPassword");

        LoginResponse loginResponse = new LoginResponse("jwt-test-token");

        Mockito.when(authService.login(any(LoginRequest.class)))
                .thenReturn(loginResponse);

        // when + then
        mockMvc.perform(post("/api/auth/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("jwt-test-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }
}