package com.chatapp.chat.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.AuthenticationException;

import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AuthenticationEntryPointImplTest {

    @Test
    @DisplayName("Should return JSON 401 response with error details")
    void shouldReturnUnauthorizedJsonResponse() throws Exception {
        // given
        AuthenticationEntryPointImpl entryPoint = new AuthenticationEntryPointImpl();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AuthenticationException exception = mock(AuthenticationException.class);

        when(request.getServletPath()).thenReturn("/api/test");
        when(exception.getMessage()).thenReturn("Bad credentials");

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        when(response.getOutputStream()).thenReturn(
                new jakarta.servlet.ServletOutputStream() {
                    @Override
                    public boolean isReady() { return true; }

                    @Override
                    public void setWriteListener(jakarta.servlet.WriteListener writeListener) {
                        // No async I/O support needed for this test
                    }

                    @Override
                    public void write(int b) {
                        out.write(b);
                    }
                }
        );

        // when
        entryPoint.commence(request, response, exception);

        // then
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");

        String json = out.toString();

        assertThat(json)
                .contains("\"status\":401")
                .contains("\"error\":\"Unauthorized\"")
                .contains("\"message\":\"Bad credentials\"")
                .contains("\"path\":\"/api/test\"");
    }
}
