package com.chatapp.chat.auth;

import com.chatapp.chat.auth.dto.LoginRequest;
import com.chatapp.chat.auth.dto.LoginResponse;
import com.chatapp.chat.auth.dto.RegisterRequest;
import com.chatapp.chat.auth.dto.RegisterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponse register(@RequestBody RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }
}
