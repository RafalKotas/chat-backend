package com.chatapp.chat.auth;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;

public class MockedAuthServiceConfig {

    @Bean
    public AuthService authService() {
        return Mockito.mock(AuthService.class);
    }
}
