package com.chatapp.chat.chat.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CreateGroupChatRequestTest {

    @Test
    @DisplayName("Should correctly expose fields from record")
    void shouldExposeFields() {
        // given
        String chatName = "name";

        // when
        CreateGroupChatRequest request = new CreateGroupChatRequest(chatName);

        // then
        assertThat(request.name()).isEqualTo("name");
    }

    @Test
    @DisplayName("Record should support equals/hashCode")
    void shouldRespectEqualsAndHashCode() {
        // given
        String chatName1 = "name";
        String chatName2 = "name";

        CreateGroupChatRequest r1 = new CreateGroupChatRequest(chatName1);
        CreateGroupChatRequest r2 = new CreateGroupChatRequest(chatName2);

        // then
        assertThat(r1).isEqualTo(r2).hasSameHashCodeAs(r2);
    }

    @Test
    @DisplayName("toString() should contain useful fields")
    void shouldContainFieldsInToString() {
        String chatName = "myCustomName";

        CreateGroupChatRequest req = new CreateGroupChatRequest(chatName);

        String ts = req.toString();

        assertThat(ts)
                .contains("myCustomName");
    }
}