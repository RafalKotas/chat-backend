package com.chatapp.chat.chat.ws;

import com.chatapp.chat.chat.message.Message;
import com.chatapp.chat.chat.message.MessageResponse;
import com.chatapp.chat.chat.message.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WsChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload WsInboundMessage inbound) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String sender = (auth != null) ? auth.getName() : "anonymous";

        log.debug("Received WS message from {}: {}", sender, inbound);

        Message saved = messageService.save(
                Message.builder()
                        .chatId(inbound.getChatId())
                        .sender(sender)
                        .content(inbound.getContent())
                        .build()
        );

        MessageResponse response = MessageResponse.fromEntity(saved);

        WsOutboundMessage outbound = WsOutboundMessage.builder()
                .type(WsMessageType.CHAT)
                .data(response)
                .build();

        messagingTemplate.convertAndSend(
                "/topic/chat." + saved.getChatId(),
                outbound
        );
    }

    @MessageMapping("/chat.addUser")
    public void addUser() {

        WsOutboundMessage outbound = WsOutboundMessage.builder()
                .type(WsMessageType.JOIN)
                .data(null)
                .build();

        messagingTemplate.convertAndSend("/topic/system", outbound);
    }
}
