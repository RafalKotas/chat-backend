package com.chatapp.chat.chat.dto;

import com.chatapp.chat.chat.Chat;

import java.util.List;
import java.util.UUID;

public record ChatResponse(
        UUID id,
        String name,
        String type,
        List<ChatParticipantResponse> participants
) {

    public static ChatResponse fromEntity(Chat chat) {
        return new ChatResponse(
                chat.getId(),
                chat.getName(),
                chat.getType().name(),
                chat.getParticipants().stream()
                        .map(cp -> new ChatParticipantResponse(cp.getUserId(), cp.getRole()))
                        .toList()
        );
    }
}
