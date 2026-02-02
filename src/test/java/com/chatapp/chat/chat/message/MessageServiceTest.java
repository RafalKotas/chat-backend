package com.chatapp.chat.chat.message;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    MessageService subject;

    @BeforeEach
    void setUp() {
        subject = new MessageService(messageRepository);
    }

    @Test
    @DisplayName("Should save message using repository")
    void shouldSaveMessage() {
        // given
        Message input = Message.builder()
                .id(UUID.randomUUID())
                .chatId("chat-1")
                .sender("Alice")
                .content("Hello!")
                .createdAt(Instant.now())
                .build();

        when(messageRepository.save(input)).thenReturn(input);

        // when
        Message result = subject.save(input);

        // then
        assertThat(result).isEqualTo(input);

        ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
        verify(messageRepository).save(captor.capture());
        assertThat(captor.getValue()).isEqualTo(input);
    }

    @Test
    @DisplayName("Should return chat history ordered by createdAt using repository")
    void shouldReturnChatHistory() {
        // given
        String chatId = "chat-xyz";

        List<Message> expectedHistory = List.of(
                Message.builder()
                        .id(UUID.randomUUID())
                        .chatId(chatId)
                        .sender("Bob")
                        .content("First")
                        .createdAt(Instant.now())
                        .build(),
                Message.builder()
                        .id(UUID.randomUUID())
                        .chatId(chatId)
                        .sender("Alice")
                        .content("Second")
                        .createdAt(Instant.now().plusSeconds(1))
                        .build()
        );

        when(messageRepository.findAllByChatIdOrderByCreatedAtAsc(chatId)).thenReturn(expectedHistory);

        // when
        List<Message> result = subject.getChatHistory(chatId);

        // then
        assertThat(result).hasSize(2).containsExactlyElementsOf(expectedHistory);
        verify(messageRepository).findAllByChatIdOrderByCreatedAtAsc(chatId);
    }
}