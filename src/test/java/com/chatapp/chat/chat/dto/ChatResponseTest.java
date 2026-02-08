package com.chatapp.chat.chat.dto;

import com.chatapp.chat.chat.Chat;
import com.chatapp.chat.chat.ChatParticipant;
import com.chatapp.chat.chat.ChatRole;
import com.chatapp.chat.chat.ChatType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ChatResponseTest {

    @Test
    @DisplayName("fromEntity() should correctly map Chat to ChatResponse")
    void shouldMapFromEntity() {
        // given
        UUID chatId = UUID.randomUUID();
        Chat chat = Chat.builder()
                .id(chatId)
                .name("Test Chat")
                .type(ChatType.GROUP)
                .build();

        ChatParticipant p1 = ChatParticipant.builder()
                .id(UUID.randomUUID())
                .chat(chat)
                .userId(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                .role(ChatRole.ADMIN)
                .build();

        ChatParticipant p2 = ChatParticipant.builder()
                .id(UUID.randomUUID())
                .chat(chat)
                .userId(UUID.fromString("00000000-0000-0000-0000-000000000002"))
                .role(ChatRole.MEMBER)
                .build();

        chat.setParticipants(new ArrayList<>(List.of(p1, p2)));

        // when
        ChatResponse response = ChatResponse.fromEntity(chat);

        // then
        assertThat(response.id()).isEqualTo(chatId);
        assertThat(response.name()).isEqualTo("Test Chat");
        assertThat(response.type()).isEqualTo("GROUP");

        assertThat(response.participants())
                .hasSize(2)
                .extracting(ChatParticipantResponse::role)
                .containsExactlyInAnyOrder(ChatRole.ADMIN, ChatRole.MEMBER);

        assertThat(response.participants())
                .extracting(ChatParticipantResponse::userId)
                .containsExactlyInAnyOrder(
                        UUID.fromString("00000000-0000-0000-0000-000000000001"),
                        UUID.fromString("00000000-0000-0000-0000-000000000002")
                );
    }

    @Test
    @DisplayName("Record should support equals/hashCode")
    void shouldRespectEqualsAndHashCode() {
        // given
        UUID id = UUID.randomUUID();
        ChatParticipantResponse p = new ChatParticipantResponse(UUID.randomUUID(), ChatRole.MEMBER);
        List<ChatParticipantResponse> participants = List.of(p);

        ChatResponse r1 = new ChatResponse(id, "Chat", "GROUP", participants);
        ChatResponse r2 = new ChatResponse(id, "Chat", "GROUP", participants);

        // then
        assertThat(r1).isEqualTo(r2).hasSameHashCodeAs(r2);
    }

    @Test
    @DisplayName("toString() should contain main fields")
    void shouldContainFieldsInToString() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000099");

        ChatResponse response = new ChatResponse(
                id,
                "Example",
                "DIRECT",
                List.of()
        );

        String ts = response.toString();

        assertThat(ts)
                .contains("Example")
                .contains("DIRECT")
                .contains("00000000-0000-0000-0000-000000000099");
    }
}
