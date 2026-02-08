package com.chatapp.chat.chat.ws;

import com.chatapp.chat.chat.message.MessageResponse;
import lombok.Builder;

@Builder
public record WsOutboundMessage(
        WsMessageType type,
        MessageResponse data
) {}
