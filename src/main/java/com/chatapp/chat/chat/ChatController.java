package com.chatapp.chat.chat;

import com.chatapp.chat.chat.message.Message;
import com.chatapp.chat.chat.message.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    private final MessageService messageService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage message) {

        log.debug("Received WS message: {}", message);

        Message saved = messageService.save(
                Message.builder()
                        .chatId(message.getChatId())
                        .sender(message.getSender())
                        .content(message.getContent())
                        .build()
        );

        messagingTemplate.convertAndSend(
                "/topic/chat." + saved.getChatId(),
                message
        );
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessage chatMessage) {
        chatMessage.setType(ChatMessageType.JOIN);

        messagingTemplate.convertAndSend(
                "/topic/chat." + chatMessage.getChatId(),
                chatMessage
        );
    }
}
