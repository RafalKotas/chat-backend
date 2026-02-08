package com.chatapp.chat.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatRepository extends JpaRepository<Chat, UUID> {

    @Query("""
        SELECT c FROM Chat c
        JOIN c.participants p
        WHERE p.userId = :userId
    """)
    List<Chat> findAllByUserId(UUID userId);

    @Query("""
        SELECT c FROM Chat c
        JOIN c.participants p1
        JOIN c.participants p2
        WHERE c.type = 'DIRECT'
          AND p1.userId = :u1
          AND p2.userId = :u2
    """)
    Optional<Chat> findDirectChatBetweenUsers(UUID u1, UUID u2);
}

