package com.chatapp.chat.chat;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatMessage {

    private String chatId;
    private String sender;
    private String content;
    private ChatMessageType type;

}
