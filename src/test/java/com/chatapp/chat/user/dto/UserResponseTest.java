package com.chatapp.chat.user.dto;

import com.chatapp.chat.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserResponseTest {

    @Test
    @DisplayName("Should correctly map User to UserResponse")
    void shouldMapUserToUserResponse() {

        // given
        UUID id = UUID.randomUUID();
        Instant createdAt = Instant.now();

        User user = User.builder()
                .id(id)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .createdAt(createdAt)
                .build();

        // when
        UserResponse response = UserResponse.from(user);

        // then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(id);
        assertThat(response.email()).isEqualTo("test@example.com");
        assertThat(response.displayName()).isEqualTo("John Doe");
        assertThat(response.createdAt()).isEqualTo(createdAt);
    }
}