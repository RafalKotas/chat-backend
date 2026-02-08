package com.chatapp.chat.chat.dto;

import java.util.UUID;

public record CreateDirectChatRequest(
        UUID user1,
        UUID user2
) {}
