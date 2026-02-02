package com.chatapp.chat.chat.message;

import com.chatapp.chat.chat._testconfig.MockedSecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(MockedSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(MessageController.class)
class MessageControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    MessageService messageService;

    @Test
    @DisplayName("Should return chat message history for given chatId")
    void shouldReturnChatHistory() throws Exception {
        // given
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        Message m1 = Message.builder()
                .id(id1)
                .chatId("room1")
                .sender("alice")
                .content("Hello")
                .createdAt(Instant.parse("2024-01-01T10:00:00Z"))
                .build();

        Message m2 = Message.builder()
                .id(id2)
                .chatId("room1")
                .sender("bob")
                .content("Hi!")
                .createdAt(Instant.parse("2024-01-01T10:01:00Z"))
                .build();

        when(messageService.getChatHistory("room1"))
                .thenReturn(List.of(m1, m2));

        // when + then
        mockMvc.perform(get("/api/messages/room1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id1.toString()))
                .andExpect(jsonPath("$[0].chatId").value("room1"))
                .andExpect(jsonPath("$[0].sender").value("alice"))
                .andExpect(jsonPath("$[0].content").value("Hello"))
                .andExpect(jsonPath("$[0].createdAt").value("2024-01-01T10:00:00Z"))

                .andExpect(jsonPath("$[1].id").value(id2.toString()))
                .andExpect(jsonPath("$[1].sender").value("bob"))
                .andExpect(jsonPath("$[1].content").value("Hi!"))
                .andExpect(jsonPath("$[1].createdAt").value("2024-01-01T10:01:00Z"));
    }

    @Test
    @DisplayName("Should return empty list when chat history is empty")
    void shouldReturnEmptyListWhenNoMessagesExist() throws Exception {
        // given
        when(messageService.getChatHistory("emptyRoom")).thenReturn(List.of());

        // when + then
        mockMvc.perform(get("/api/messages/emptyRoom")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("Should correctly call service with provided chatId")
    void shouldCallServiceWithCorrectChatId() throws Exception {
        // given
        when(messageService.getChatHistory("roomX")).thenReturn(List.of());

        // when
        mockMvc.perform(get("/api/messages/roomX"))
                .andExpect(status().isOk());

        // then
        Mockito.verify(messageService).getChatHistory("roomX");
    }
}