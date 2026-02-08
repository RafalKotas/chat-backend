package com.chatapp.chat.chat.dto;

import com.chatapp.chat.chat.ChatRole;
import java.util.UUID;

public record ChatParticipantResponse(
        UUID userId,
        ChatRole role
) {}
