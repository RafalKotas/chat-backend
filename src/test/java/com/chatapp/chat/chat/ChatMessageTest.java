package com.chatapp.chat.chat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChatMessageTest {

    @Test
    @DisplayName("Should correctly set and get fields")
    void shouldSetAndGetFields() {
        // given
        ChatMessage msg = new ChatMessage();

        // when
        msg.setChatId("chat-123");
        msg.setSender("alice");
        msg.setContent("Hello!");
        msg.setType(ChatMessageType.CHAT);

        // then
        assertThat(msg.getChatId()).isEqualTo("chat-123");
        assertThat(msg.getSender()).isEqualTo("alice");
        assertThat(msg.getContent()).isEqualTo("Hello!");
        assertThat(msg.getType()).isEqualTo(ChatMessageType.CHAT);
    }

    @Test
    @DisplayName("Should generate proper equals/hashCode with Lombok")
    void shouldRespectEqualsAndHashCode() {
        // given
        ChatMessage m1 = new ChatMessage();
        m1.setChatId("c1");
        m1.setSender("bob");
        m1.setContent("hi");
        m1.setType(ChatMessageType.JOIN);

        ChatMessage m2 = new ChatMessage();
        m2.setChatId("c1");
        m2.setSender("bob");
        m2.setContent("hi");
        m2.setType(ChatMessageType.JOIN);

        // then
        assertThat(m1).isEqualTo(m2).hasSameHashCodeAs(m2);
    }

    @Test
    @DisplayName("Should include fields in toString()")
    void shouldGenerateToString() {
        // given
        ChatMessage msg = new ChatMessage();
        msg.setChatId("xyz");
        msg.setSender("john");
        msg.setContent("Test");
        msg.setType(ChatMessageType.LEAVE);

        // when
        String ts = msg.toString();

        // then
        assertThat(ts).contains("xyz", "john", "Test", "LEAVE");
    }

}