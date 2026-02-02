package com.chatapp.chat.auth;

import com.chatapp.chat.auth.dto.LoginRequest;
import com.chatapp.chat.auth.dto.LoginResponse;
import com.chatapp.chat.auth.dto.RegisterRequest;
import com.chatapp.chat.auth.dto.RegisterResponse;
import com.chatapp.chat.security.JwtUtils;
import com.chatapp.chat.user.User;
import com.chatapp.chat.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    public RegisterResponse register(RegisterRequest registerRequest) {

        User user = User.builder()
                .email(registerRequest.email())
                .username(registerRequest.username())
                .password(passwordEncoder.encode(registerRequest.password()))
                .firstName(registerRequest.firstName())
                .lastName(registerRequest.lastName())
                .build();

        User saved = userRepository.save(user);

        return new RegisterResponse(saved.getId(), saved.getEmail(), saved.getUsername());
    }

    public LoginResponse login(LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.username(), loginRequest.password()
                )
        );

        String token = jwtUtils.generateToken(authentication.getName());

        return new LoginResponse(token);
    }
}
