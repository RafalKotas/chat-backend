package com.chatapp.chat.user.controller;

import com.chatapp.chat.user.User;
import com.chatapp.chat.user.UserService;
import com.chatapp.chat.user.dto.UserRegistrationRequest;
import com.chatapp.chat.user.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@RequestBody @Valid UserRegistrationRequest request) {

        log.info("Received registration request for email={}", request.getEmail());

        User user = User.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .build();

        User saved = userService.registerUser(user);

        log.info("User successfully registered with id={} email={}",
                saved.getId(), saved.getEmail());

        return UserResponse.from(saved);
    }
}
