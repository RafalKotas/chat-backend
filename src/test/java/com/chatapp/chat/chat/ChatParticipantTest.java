package com.chatapp.chat.chat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ChatParticipantTest {

    @Test
    @DisplayName("Should correctly set and get fields")
    void shouldSetAndGetFields() {
        // given
        Chat chat = Chat.builder()
                .id(UUID.randomUUID())
                .name("TestChat")
                .type(ChatType.GROUP)
                .build();

        UUID userId = UUID.randomUUID();
        ChatRole role = ChatRole.MEMBER;

        // when
        ChatParticipant participant = new ChatParticipant();
        participant.setChat(chat);
        participant.setUserId(userId);
        participant.setRole(role);

        // then
        assertThat(participant.getChat()).isSameAs(chat);
        assertThat(participant.getUserId()).isEqualTo(userId);
        assertThat(participant.getRole()).isEqualTo(role);
    }

    @Test
    @DisplayName("Builder should construct valid instance")
    void shouldBuildCorrectly() {
        // given
        Chat chat = Chat.builder()
                .id(UUID.randomUUID())
                .name("Room")
                .type(ChatType.GROUP)
                .build();

        UUID userId = UUID.randomUUID();

        // when
        ChatParticipant participant = ChatParticipant.builder()
                .chat(chat)
                .userId(userId)
                .role(ChatRole.ADMIN)
                .build();

        // then
        assertThat(participant.getChat()).isEqualTo(chat);
        assertThat(participant.getUserId()).isEqualTo(userId);
        assertThat(participant.getRole()).isEqualTo(ChatRole.ADMIN);
    }

    @Test
    @DisplayName("toString() should include essential fields")
    void shouldGenerateUsefulToString() {
        // given
        Chat chat = Chat.builder()
                .id(UUID.randomUUID())
                .name("ChatX")
                .type(ChatType.GROUP)
                .build();

        UUID userId = UUID.randomUUID();

        ChatParticipant participant = ChatParticipant.builder()
                .chat(chat)
                .userId(userId)
                .role(ChatRole.ADMIN)
                .build();

        // when
        String ts = participant.toString();

        // then
        assertThat(ts)
                .contains("ChatX")
                .contains(userId.toString())
                .contains("ADMIN");
    }
}
