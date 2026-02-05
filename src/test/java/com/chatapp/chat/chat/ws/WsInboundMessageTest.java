package com.chatapp.chat.chat.ws;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WsInboundMessageTest {

    @Test
    @DisplayName("Should correctly set and get fields")
    void shouldSetAndGetFields() {
        // given
        WsInboundMessage msg = new WsInboundMessage();
        msg.setChatId("chat-123");
        msg.setContent("Hello!");

        // then
        assertThat(msg.getChatId()).isEqualTo("chat-123");
        assertThat(msg.getContent()).isEqualTo("Hello!");
    }

    @Test
    @DisplayName("Should implement equals/hashCode correctly")
    void shouldRespectEqualsAndHashCode() {
        // given
        WsInboundMessage m1 = new WsInboundMessage();
        m1.setChatId("room");
        m1.setContent("hi");

        WsInboundMessage m2 = new WsInboundMessage();
        m2.setChatId("room");
        m2.setContent("hi");

        // then
        assertThat(m1).isEqualTo(m2).hasSameHashCodeAs(m2);
    }

    @Test
    @DisplayName("Should include fields in toString()")
    void shouldGenerateToString() {
        // given
        WsInboundMessage msg = new WsInboundMessage();
        msg.setChatId("abc");
        msg.setContent("Test");

        // when
        String ts = msg.toString();

        // then
        assertThat(ts).contains("abc", "Test");
    }
}
