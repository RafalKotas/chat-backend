package com.chatapp.chat.chat;

import com.chatapp.chat.chat._testconfig.MockedSecurityConfig;
import com.chatapp.chat.chat.message.Message;
import com.chatapp.chat.chat.message.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Import(MockedSecurityConfig.class)
@WebMvcTest(ChatController.class)
class ChatControllerTest {

    @MockitoBean
    SimpMessagingTemplate simpMessagingTemplate;

    @MockitoBean
    MessageService messageService;

    ChatController subject;

    @BeforeEach
    void setUp() {
        subject = new ChatController(simpMessagingTemplate, messageService);
    }

    @Test
    @DisplayName("sendMessage() should save message and forward it to the correct topic")
    void shouldSaveMessageAndForward() {
        // given
        ChatMessage incoming = new ChatMessage();
        incoming.setChatId("room-1");
        incoming.setSender("Alice");
        incoming.setContent("Hello!");

        Message savedEntity = Message.builder()
                .id(UUID.randomUUID())
                .chatId("room-1")
                .content("Hello!")
                .build();

        when(messageService.save(any(Message.class))).thenReturn(savedEntity);

        // when
        subject.sendMessage(incoming);

        // then
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);

        verify(messageService).save(messageCaptor.capture());
        Message savedArg = messageCaptor.getValue();

        assertThat(savedArg.getChatId()).isEqualTo("room-1");
        assertThat(savedArg.getSender()).isEqualTo("Alice");
        assertThat(savedArg.getContent()).isEqualTo("Hello!");

        verify(simpMessagingTemplate).convertAndSend(
                "/topic/chat.room-1",
                incoming
        );
    }

    @Test
    @DisplayName("addUser() should set type JOIN and broadcast message to correct topic")
    void shouldSetJoinTypeAndBroadcast() {
        // given
        ChatMessage msg = new ChatMessage();
        msg.setChatId("room-22");
        msg.setSender("bob");

        // when
        subject.addUser(msg);

        // then
        assertThat(msg.getType()).isEqualTo(ChatMessageType.JOIN);

        verify(simpMessagingTemplate).convertAndSend(
                "/topic/chat.room-22",
                msg
        );
    }
}