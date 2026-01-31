package com.chatapp.chat.user.controller;

import com.chatapp.chat.security.JwtAuthenticationFilter;
import com.chatapp.chat.security.JwtUtils;
import com.chatapp.chat.user.User;
import com.chatapp.chat.user.UserService;
import com.chatapp.chat.user.dto.UserRegistrationRequest;
import com.chatapp.chat.user.exception.EmailAlreadyUsedException;
import com.chatapp.chat.user.exception.UsernameAlreadyUsedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(MockedUserServiceConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService; // <- mock injected by config

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    JwtUtils jwtUtils;

    @Test
    @DisplayName("POST /api/users/register should return 201 Created with UserResponse")
    void shouldReturn201CreatedWithProperUserResponse() throws Exception {

        // given
        UUID id = UUID.randomUUID();

        User saved = User.builder()
                .id(id)
                .email("test@example.com")
                .username("raf")
                .build();

        Mockito.when(userService.registerUser(any(User.class)))
                .thenReturn(saved);

        UserRegistrationRequest request = new UserRegistrationRequest(
                "test@example.com",
                "password123",
                "John",
                "Doe",
                "raf"
        );

        // when + then
        mockMvc.perform(post("/api/users/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.displayName").value("raf"));
    }

    @Test
    @DisplayName("POST /api/users/register → should return 400 when email already exists")
    void shouldReturn400WhenEmailAlreadyExists() throws Exception {

        // given
        Mockito.when(userService.registerUser(any(User.class)))
                .thenThrow(new EmailAlreadyUsedException("test@example.com"));

        UserRegistrationRequest req = new UserRegistrationRequest(
                "test@example.com", "password123", "John", "Doe", "johnny"
        );

        // when + then
        mockMvc.perform(post("/api/users/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email already in use: test@example.com"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }


    @Test
    @DisplayName("POST /api/users/register → should return 400 when username already exists")
    void shouldReturn400WhenUsernameAlreadyExists() throws Exception {

        // given
        Mockito.when(userService.registerUser(any(User.class)))
                .thenThrow(new UsernameAlreadyUsedException("johnny"));

        UserRegistrationRequest req = new UserRegistrationRequest(
                "john@example.com", "password123", "John", "Doe", "johnny"
        );

        // when + then
        mockMvc.perform(post("/api/users/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Username already in use: johnny"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }
}
