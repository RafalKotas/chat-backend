package com.chatapp.chat.chat.message;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MessageResponseTest {

    @Test
    @DisplayName("Should correctly map Message entity to MessageResponse using fromEntity()")
    void shouldMapEntityToResponse() {
        // given
        UUID id = UUID.randomUUID();
        String chatId = "chat-123";
        String sender = "alice";
        String content = "Hello world!";
        Instant createdAt = Instant.now();

        Message entity = Message.builder()
                .id(id)
                .chatId(chatId)
                .sender(sender)
                .content(content)
                .createdAt(createdAt)
                .build();

        // when
        MessageResponse response = MessageResponse.fromEntity(entity);

        // then
        assertThat(response.id()).isEqualTo(id);
        assertThat(response.chatId()).isEqualTo(chatId);
        assertThat(response.sender()).isEqualTo(sender);
        assertThat(response.content()).isEqualTo(content);
        assertThat(response.createdAt()).isEqualTo(createdAt);
    }
}