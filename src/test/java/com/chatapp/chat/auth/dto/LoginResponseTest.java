package com.chatapp.chat.auth.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoginResponseTest {

    @Test
    @DisplayName("Constructor with explicit tokenType should store accessToken and tokenType")
    void shouldStoreAccessTokenAndTokenType() {
        // given
        String token = "testToken";
        String type = "Bearer";

        // when
        LoginResponse loginResponse = new LoginResponse(token, type);

        // then
        assertThat(loginResponse.accessToken()).isEqualTo("testToken");
        assertThat(loginResponse.tokenType()).isEqualTo("Bearer");
    }

    @Test
    @DisplayName("Constructor with only accessToken should set tokenType to Bearer by default")
    void shouldSetBearerAsDefaultTokenType() {
        // given
        String token = "testToken2";

        // when
        LoginResponse loginResponse = new LoginResponse(token);

        // then
        assertThat(loginResponse.accessToken()).isEqualTo("testToken2");
        assertThat(loginResponse.tokenType()).isEqualTo("Bearer");
    }
}