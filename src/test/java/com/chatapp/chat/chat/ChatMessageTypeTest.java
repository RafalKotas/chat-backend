package com.chatapp.chat.chat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ChatMessageTypeTest {

    @Test
    @DisplayName("Should contain exactly CHAT, JOIN, LEAVE values")
    void shouldContainAllEnumValues() {
        // given
        // enum is static, no setup needed

        // when
        ChatMessageType[] values = ChatMessageType.values();

        // then
        assertThat(values)
                .containsExactly(
                        ChatMessageType.CHAT,
                        ChatMessageType.JOIN,
                        ChatMessageType.LEAVE
                );
    }

    @Test
    @DisplayName("Should resolve enum constant from name")
    void shouldResolveEnumWithValueOf() {
        // given
        // no setup

        // when
        ChatMessageType chat = ChatMessageType.valueOf("CHAT");
        ChatMessageType join = ChatMessageType.valueOf("JOIN");
        ChatMessageType leave = ChatMessageType.valueOf("LEAVE");

        // then
        assertThat(chat).isEqualTo(ChatMessageType.CHAT);
        assertThat(join).isEqualTo(ChatMessageType.JOIN);
        assertThat(leave).isEqualTo(ChatMessageType.LEAVE);
    }

    @Test
    @DisplayName("Enum valueOf should throw error for invalid name")
    void shouldRejectInvalidEnumName() {
        // given
        String invalidName = "INVALID";

        // when + then
        assertThatThrownBy(() -> ChatMessageType.valueOf(invalidName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(invalidName);
    }

}