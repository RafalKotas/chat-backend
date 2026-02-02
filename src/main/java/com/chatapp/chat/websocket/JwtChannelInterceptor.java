package com.chatapp.chat.websocket;

import com.chatapp.chat.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtUtils jwtUtils;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (!StompCommand.CONNECT.equals(accessor.getCommand())) {
            return message;
        }

        String token = accessor.getFirstNativeHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            log.debug("Missing or invalid auth header in WS CONNECT");
            return message;
        }

        token = token.substring(7);

        if (!jwtUtils.isValid(token)) {
            log.debug("Invalid JWT token in WS CONNECT");
            return null;
        }

        String username = jwtUtils.extractUserName(token);

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(username, null, null);

        log.debug("Websocket user authenticated");

        SecurityContextHolder.getContext().setAuthentication(auth);
        accessor.setUser(auth);

        return org.springframework.messaging.support.MessageBuilder
                .createMessage(message.getPayload(), accessor.getMessageHeaders());
    }
}
