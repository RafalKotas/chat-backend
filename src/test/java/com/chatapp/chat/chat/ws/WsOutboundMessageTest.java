package com.chatapp.chat.chat.ws;

import com.chatapp.chat.chat.message.MessageResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class WsOutboundMessageTest {

    @Test
    @DisplayName("Should correctly expose type and data")
    void shouldExposeFields() {
        UUID id = UUID.randomUUID();
        MessageResponse resp = new MessageResponse(
                id,
                "chat1",
                "alice",
                "hello",
                Instant.now()
        );

        WsOutboundMessage msg = new WsOutboundMessage(WsMessageType.CHAT, resp);

        assertThat(msg.type()).isEqualTo(WsMessageType.CHAT);
        assertThat(msg.data()).isEqualTo(resp);
    }

    @Test
    @DisplayName("Should implement equals/hashCode correctly")
    void shouldRespectEqualsAndHashCode() {
        MessageResponse data = new MessageResponse(
                UUID.randomUUID(),
                "c",
                "x",
                "msg",
                Instant.now()
        );

        WsOutboundMessage m1 = new WsOutboundMessage(WsMessageType.JOIN, data);
        WsOutboundMessage m2 = new WsOutboundMessage(WsMessageType.JOIN, data);

        assertThat(m1).isEqualTo(m2).hasSameHashCodeAs(m2);
    }

    @Test
    @DisplayName("Should include fields in toString()")
    void shouldGenerateToString() {
        MessageResponse data = new MessageResponse(
                UUID.randomUUID(),
                "c",
                "x",
                "msg",
                Instant.now()
        );

        WsOutboundMessage msg = new WsOutboundMessage(WsMessageType.LEAVE, data);

        String ts = msg.toString();

        assertThat(ts).contains("LEAVE", "msg", "x");
    }
}

