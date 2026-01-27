package com.chatapp.chat.user.dto;

import com.chatapp.chat.user.User;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record UserResponse (
        UUID id,
        String email,
        String displayName,
        Instant createdAt
) {
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
