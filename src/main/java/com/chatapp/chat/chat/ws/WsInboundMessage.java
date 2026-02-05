package com.chatapp.chat.chat.ws;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WsInboundMessage {
    private String chatId;
    private String content;
}