package com.chatapp.chat.chat;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MockedChatServiceConfig {

    @Bean
    public ChatService chatService() {
        return Mockito.mock(ChatService.class);
    }
}
