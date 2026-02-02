package com.chatapp.chat.auth.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterResponseTest {

    @Test
    @DisplayName("Should correctly store fields in record")
    void shouldHoldFieldsCorrectly() {
        // given
        UUID uuid = UUID.randomUUID();
        String email = "bob@example.com";
        String username = "bob321";

        // when
        RegisterResponse registerResponse = new RegisterResponse(uuid, email, username);

        // then
        assertThat(registerResponse.id()).isEqualTo(uuid);
        assertThat(registerResponse.email()).isEqualTo(email);
        assertThat(registerResponse.username()).isEqualTo(username);
    }
}