package com.chatapp.chat.chat.ws;

import com.chatapp.chat.chat._testconfig.MockedSecurityConfig;
import com.chatapp.chat.chat.message.Message;
import com.chatapp.chat.chat.message.MessageResponse;
import com.chatapp.chat.chat.message.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Import(MockedSecurityConfig.class)
@WebMvcTest(WsChatController.class)
class WsChatControllerTest {

    @MockitoBean
    SimpMessagingTemplate simpMessagingTemplate;

    @MockitoBean
    MessageService messageService;

    WsChatController subject;

    @BeforeEach
    void setUp() {
        subject = new WsChatController(simpMessagingTemplate, messageService);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("Alice", null)
        );
    }

    @Test
    @DisplayName("sendMessage() should save entity and broadcast WsOutboundMessage with CHAT type")
    void shouldSaveMessageAndBroadcastOutbound() {
        // given
        WsInboundMessage inbound = new WsInboundMessage();
        inbound.setChatId("room-1");
        inbound.setContent("Hello!");

        Message savedEntity = Message.builder()
                .id(UUID.randomUUID())
                .chatId("room-1")
                .sender("Alice")
                .content("Hello!")
                .build();

        when(messageService.save(any(Message.class))).thenReturn(savedEntity);

        // when
        subject.sendMessage(inbound);

        // then
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(messageService).save(messageCaptor.capture());
        Message stored = messageCaptor.getValue();

        assertThat(stored.getChatId()).isEqualTo("room-1");
        assertThat(stored.getSender()).isEqualTo("Alice");
        assertThat(stored.getContent()).isEqualTo("Hello!");

        ArgumentCaptor<WsOutboundMessage> outboundCaptor = ArgumentCaptor.forClass(WsOutboundMessage.class);

        verify(simpMessagingTemplate).convertAndSend(
                org.mockito.ArgumentMatchers.eq("/topic/chat.room-1"),
                outboundCaptor.capture()
        );

        WsOutboundMessage outbound = outboundCaptor.getValue();

        assertThat(outbound.type()).isEqualTo(WsMessageType.CHAT);

        MessageResponse data = outbound.data();
        assertThat(data.chatId()).isEqualTo("room-1");
        assertThat(data.sender()).isEqualTo("Alice");
        assertThat(data.content()).isEqualTo("Hello!");
    }

    @Test
    @DisplayName("addUser() should broadcast JOIN event to /topic/system")
    void shouldBroadcastJoinEvent() {
        // when
        subject.addUser();

        // then
        ArgumentCaptor<WsOutboundMessage> captor = ArgumentCaptor.forClass(WsOutboundMessage.class);

        verify(simpMessagingTemplate).convertAndSend(
                org.mockito.ArgumentMatchers.eq("/topic/system"),
                captor.capture()
        );

        WsOutboundMessage outbound = captor.getValue();

        assertThat(outbound.type()).isEqualTo(WsMessageType.JOIN);
        assertThat(outbound.data()).isNull();
    }
}
