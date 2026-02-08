package com.chatapp.chat.chat.dto;

import com.chatapp.chat.chat.ChatRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ChatParticipantResponseTest {

    ChatParticipantResponse subject;

    @Test
    @DisplayName("Should correctly store fields in record")
    void shouldHoldFieldsCorrectly() {
        // given
        UUID uuid = UUID.randomUUID();
        ChatRole chatRole = ChatRole.MEMBER;

        // when
        subject = new ChatParticipantResponse(uuid, chatRole);

        // then
        assertThat(subject.userId()).isEqualTo(uuid);
        assertThat(subject.role()).isEqualTo(ChatRole.MEMBER);
    }

    @Test
    @DisplayName("Should support equality and hash code")
    void shouldRespectEqualsAndHashCode() {
        // given
        UUID id = UUID.randomUUID();

        ChatParticipantResponse r1 = new ChatParticipantResponse(id, ChatRole.ADMIN);
        ChatParticipantResponse r2 = new ChatParticipantResponse(id, ChatRole.ADMIN);

        // then
        assertThat(r1).isEqualTo(r2).hasSameHashCodeAs(r2);
    }

    @Test
    @DisplayName("toString() should contain record fields")
    void shouldGenerateToString() {
        // given
        ChatParticipantResponse response =
                new ChatParticipantResponse(UUID.fromString("00000000-0000-0000-0000-000000000001"), ChatRole.MEMBER);

        // when
        String ts = response.toString();

        // then
        assertThat(ts)
                .contains("00000000-0000-0000-0000-000000000001")
                .contains("MEMBER");
    }
}