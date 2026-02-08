package com.chatapp.chat.chat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ChatTest {

    @Test
    @DisplayName("Builder should correctly set fields")
    void builderShouldSetFields() {
        // given & when
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();

        Chat chat = Chat.builder()
                .id(id)
                .type(ChatType.DIRECT)
                .name("Test Chat")
                .participants(List.of())
                .createdAt(now)
                .build();

        // then
        assertThat(chat.getId()).isEqualTo(id);
        assertThat(chat.getType()).isEqualTo(ChatType.DIRECT);
        assertThat(chat.getName()).isEqualTo("Test Chat");
        assertThat(chat.getParticipants()).isEmpty();
        assertThat(chat.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Setters and getters should work correctly")
    void settersAndGettersShouldWork() {
        // given & when
        Chat chat = new Chat();

        UUID id = UUID.randomUUID();
        chat.setId(id);
        chat.setType(ChatType.GROUP);
        chat.setName("Group A");

        // then
        assertThat(chat.getId()).isEqualTo(id);
        assertThat(chat.getType()).isEqualTo(ChatType.GROUP);
        assertThat(chat.getName()).isEqualTo("Group A");
    }

    @Test
    @DisplayName("Should include fields in toString() for debugging")
    void toStringShouldContainFields() {
        // given
        Chat chat = Chat.builder()
                .id(UUID.fromString("00000000-0000-0000-0000-000000000111"))
                .type(ChatType.GROUP)
                .name("DebugChat")
                .participants(List.of())
                .build();

        // when
        String ts = chat.toString();

        // then
        assertThat(ts)
                .contains("00000000-0000-0000-0000-000000000111")
                .contains("GROUP")
                .contains("DebugChat");
    }
}
