package com.chatapp.chat.user.controller;

import com.chatapp.chat.user.UserService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MockedUserServiceConfig {

    @Bean
    public UserService userService() {
        return Mockito.mock(UserService.class);
    }
}
