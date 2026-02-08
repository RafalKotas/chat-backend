package com.chatapp.chat.chat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, UUID> {

    List<ChatParticipant> findAllByChatId(UUID chatId);

    List<ChatParticipant> findAllByUserId(UUID userId);

    boolean existsByChatIdAndUserId(UUID chatId, UUID userId);

    void deleteByChatIdAndUserId(UUID chatId, UUID userId);

    Optional<ChatParticipant> findByChatIdAndUserId(UUID chatId, UUID userId);
}

