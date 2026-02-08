package com.chatapp.chat.chat.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CreateDirectChatRequestTest {

    @Test
    @DisplayName("Should correctly expose fields from record")
    void shouldExposeFields() {
        // given
        UUID u1 = UUID.randomUUID();
        UUID u2 = UUID.randomUUID();

        // when
        CreateDirectChatRequest request = new CreateDirectChatRequest(u1, u2);

        // then
        assertThat(request.user1()).isEqualTo(u1);
        assertThat(request.user2()).isEqualTo(u2);
    }

    @Test
    @DisplayName("Record should support equals/hashCode")
    void shouldRespectEqualsAndHashCode() {
        // given
        UUID a1 = UUID.randomUUID();
        UUID a2 = UUID.randomUUID();

        CreateDirectChatRequest r1 = new CreateDirectChatRequest(a1, a2);
        CreateDirectChatRequest r2 = new CreateDirectChatRequest(a1, a2);

        // then
        assertThat(r1).isEqualTo(r2).hasSameHashCodeAs(r2);
    }

    @Test
    @DisplayName("toString() should contain useful fields")
    void shouldContainFieldsInToString() {
        UUID a1 = UUID.fromString("00000000-0000-0000-0000-000000000111");
        UUID a2 = UUID.fromString("00000000-0000-0000-0000-000000000222");

        CreateDirectChatRequest req = new CreateDirectChatRequest(a1, a2);

        String ts = req.toString();

        assertThat(ts)
                .contains("00000000-0000-0000-0000-000000000111")
                .contains("00000000-0000-0000-0000-000000000222");
    }
}