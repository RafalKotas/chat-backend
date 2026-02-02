package com.chatapp.chat.chat.message;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MessageTest {

    @Test
    @DisplayName("Should build Message using builder()")
    void shouldBuildMessageUsingBuilder() {
        // given
        UUID id = UUID.randomUUID();
        Instant createdAt = Instant.now();

        // when
        Message message = Message.builder()
                .id(id)
                .chatId("chat-123")
                .sender("Alice")
                .content("Hello!")
                .createdAt(createdAt)
                .build();

        // then
        assertThat(message.getId()).isEqualTo(id);
        assertThat(message.getChatId()).isEqualTo("chat-123");
        assertThat(message.getSender()).isEqualTo("Alice");
        assertThat(message.getContent()).isEqualTo("Hello!");
        assertThat(message.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("Should set and get fields correctly")
    void shouldSetAndGetFields() {
        // given
        Message message = new Message();

        UUID id = UUID.randomUUID();
        Instant createdAt = Instant.now();

        // when
        message.setId(id);
        message.setChatId("chat-xyz");
        message.setSender("Bob");
        message.setContent("Message content");
        message.setCreatedAt(createdAt);

        // then
        assertThat(message.getId()).isEqualTo(id);
        assertThat(message.getChatId()).isEqualTo("chat-xyz");
        assertThat(message.getSender()).isEqualTo("Bob");
        assertThat(message.getContent()).isEqualTo("Message content");
        assertThat(message.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("Should create empty Message using no-args constructor")
    void shouldCreateEmptyMessage() {
        // when
        Message message = new Message();

        // then
        assertThat(message.getId()).isNull();
        assertThat(message.getChatId()).isNull();
        assertThat(message.getSender()).isNull();
        assertThat(message.getContent()).isNull();
        assertThat(message.getCreatedAt()).isNull();
    }

    @Test
    @DisplayName("Should create Message with all-args constructor")
    void shouldCreateMessageWithAllArgsConstructor() {
        // given
        UUID id = UUID.randomUUID();
        Instant createdAt = Instant.now();

        // when
        Message msg = new Message(id, "c1", "Alice", "test", createdAt);

        // then
        assertThat(msg.getId()).isEqualTo(id);
        assertThat(msg.getChatId()).isEqualTo("c1");
        assertThat(msg.getSender()).isEqualTo("Alice");
        assertThat(msg.getContent()).isEqualTo("test");
        assertThat(msg.getCreatedAt()).isEqualTo(createdAt);
    }
}