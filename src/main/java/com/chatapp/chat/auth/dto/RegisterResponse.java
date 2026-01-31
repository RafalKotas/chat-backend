package com.chatapp.chat.auth.dto;

import java.util.UUID;

public record RegisterResponse(
        UUID id,
        String email,
        String username
) { }
