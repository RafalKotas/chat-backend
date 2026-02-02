package com.chatapp.chat.websocket;

import com.chatapp.chat.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.security.Principal;

@ExtendWith(MockitoExtension.class)
class JwtChannelInterceptorTest {

    @Mock
    JwtUtils jwtUtils;

    @Mock
    MessageChannel channel;

    JwtChannelInterceptor subject;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        subject = new JwtChannelInterceptor(jwtUtils);
    }

    private Message<?> buildMessage(StompCommand command, String token) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(command);

        if (token != null) {
            accessor.addNativeHeader("Authorization", token);
        }

        return MessageBuilder.withPayload("test")
                .setHeaders(accessor)
                .build();
    }

    @Test
    @DisplayName("Should ignore message when command is not CONNECT")
    void shouldIgnoreWhenNotConnect() {
        // given
        Message<?> msg = buildMessage(StompCommand.SEND, "Bearer abc");

        // when
        Message<?> result = subject.preSend(msg, channel);

        // then
        assertThat(result).isSameAs(msg);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verifyNoInteractions(jwtUtils);
    }

    @Test
    @DisplayName("Should ignore and NOT authenticate when Authorization header is missing")
    void shouldIgnoreWhenHeaderMissing() {
        // given
        Message<?> msg = buildMessage(StompCommand.CONNECT, null);

        // when
        Message<?> result = subject.preSend(msg, channel);

        // then
        assertThat(result).isSameAs(msg);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verifyNoInteractions(jwtUtils);
    }

    @Test
    @DisplayName("Should ignore when header does not start with Bearer")
    void shouldIgnoreWhenHeaderNotBearer() {
        // given
        Message<?> msg = buildMessage(StompCommand.CONNECT, "Basic abc");

        // when
        Message<?> result = subject.preSend(msg, channel);

        // then
        assertThat(result).isSameAs(msg);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verifyNoInteractions(jwtUtils);
    }

    @Test
    @DisplayName("Should reject message (return null) when JWT is invalid")
    void shouldRejectWhenTokenInvalid() {
        // given
        Message<?> msg = buildMessage(StompCommand.CONNECT, "Bearer token123");

        when(jwtUtils.isValid("token123")).thenReturn(false);

        // when
        Message<?> result = subject.preSend(msg, channel);

        // then
        assertThat(result).isNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Should authenticate user when token is valid")
    void shouldAuthenticateWhenTokenValid() {
        // given
        Message<?> msg = buildMessage(StompCommand.CONNECT, "Bearer token123");

        when(jwtUtils.isValid("token123")).thenReturn(true);
        when(jwtUtils.extractUserName("token123")).thenReturn("Alice");

        // when
        Message<?> result = subject.preSend(msg, channel);

        // then
        assertThat(result).isNotNull();

        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNotNull()
                .extracting(Principal::getName)
                .isEqualTo("Alice");

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(result);
        assertThat(accessor.getUser()).isNotNull();
        assertThat(accessor.getUser().getName()).isEqualTo("Alice");
    }
}
