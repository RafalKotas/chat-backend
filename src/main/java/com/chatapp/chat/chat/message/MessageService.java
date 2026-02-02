package com.chatapp.chat.chat.message;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public Message save(Message message) {
        return messageRepository.save(message);
    }

    public List<Message> getChatHistory(String chatId) {
        return messageRepository.findAllByChatIdOrderByCreatedAtAsc(chatId);
    }
}
