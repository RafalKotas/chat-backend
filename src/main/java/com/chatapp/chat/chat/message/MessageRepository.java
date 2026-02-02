package com.chatapp.chat.chat.message;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    List<Message> findAllByChatIdOrderByCreatedAtAsc(String chatId);
}
