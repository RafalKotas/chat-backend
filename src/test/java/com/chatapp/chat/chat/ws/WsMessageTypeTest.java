package com.chatapp.chat.chat.ws;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WsMessageTypeTest {

    @Test
    @DisplayName("Should contain exactly CHAT, JOIN, LEAVE values")
    void shouldContainAllEnumValues() {
        // given
        // enum is static, no setup needed

        // when
        WsMessageType[] values = WsMessageType.values();

        // then
        assertThat(values)
                .containsExactly(
                        WsMessageType.CHAT,
                        WsMessageType.JOIN,
                        WsMessageType.LEAVE
                );
    }

    @Test
    @DisplayName("Should resolve enum constant from name")
    void shouldResolveEnumWithValueOf() {
        // given
        // no setup

        // when
        WsMessageType chat = WsMessageType.valueOf("CHAT");
        WsMessageType join = WsMessageType.valueOf("JOIN");
        WsMessageType leave = WsMessageType.valueOf("LEAVE");

        // then
        assertThat(chat).isEqualTo(WsMessageType.CHAT);
        assertThat(join).isEqualTo(WsMessageType.JOIN);
        assertThat(leave).isEqualTo(WsMessageType.LEAVE);
    }

    @Test
    @DisplayName("Enum valueOf should throw error for invalid name")
    void shouldRejectInvalidEnumName() {
        // given
        String invalidName = "INVALID";

        // when + then
        assertThatThrownBy(() -> WsMessageType.valueOf(invalidName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(invalidName);
    }

}