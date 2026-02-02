package com.chatapp.chat.chat.message;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/{chatId}")
    public List<MessageResponse> getChatHistory(@PathVariable String chatId) {
        return messageService.getChatHistory(chatId)
                .stream()
                .map(MessageResponse::fromEntity)
                .toList();
    }
}
