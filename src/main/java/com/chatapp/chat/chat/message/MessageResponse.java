package com.chatapp.chat.chat.message;

import java.time.Instant;
import java.util.UUID;

public record MessageResponse(
        UUID id,
        String chatId,
        String sender,
        String content,
        Instant createdAt
) {
    public static MessageResponse fromEntity(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getChatId(),
                message.getSender(),
                message.getContent(),
                message.getCreatedAt()
        );
    }
}