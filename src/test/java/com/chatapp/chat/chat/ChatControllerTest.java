package com.chatapp.chat.chat;

import com.chatapp.chat.chat._testconfig.MockedSecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(MockedSecurityConfig.class)
class ChatControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ChatService chatService;

    Chat exampleChat;

    @BeforeEach
    void setUp() {
        exampleChat = Chat.builder()
                .id(UUID.randomUUID())
                .name("Test Chat")
                .type(ChatType.GROUP)
                .participants(List.of())
                .build();
    }

    @Test
    @DisplayName("POST /api/chats/direct — should create a direct chat")
    void shouldCreateDirectChat() throws Exception {
        // given
        UUID u1 = UUID.randomUUID();
        UUID u2 = UUID.randomUUID();
        when(chatService.createDirectChat(any(), any())).thenReturn(exampleChat);

        // when / then (HTTP)
        mockMvc.perform(post("/api/chats/direct")
                        .contentType("application/json")
                        .content("""
                                {
                                  "user1": "%s",
                                  "user2": "%s"
                                }
                                """.formatted(u1, u2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Chat"))
                .andExpect(jsonPath("$.type").value("GROUP"));

        // then (service interaction)
        ArgumentCaptor<UUID> captor1 = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<UUID> captor2 = ArgumentCaptor.forClass(UUID.class);

        verify(chatService).createDirectChat(captor1.capture(), captor2.capture());

        assertThat(captor1.getValue()).isEqualTo(u1);
        assertThat(captor2.getValue()).isEqualTo(u2);
    }

    @Test
    @DisplayName("POST /api/chats/group — should create group chat")
    void shouldCreateGroupChat() throws Exception {
        // given
        UUID creator = UUID.randomUUID();
        when(chatService.createGroupChat(any(), any())).thenReturn(exampleChat);

        // when / then (HTTP)
        mockMvc.perform(post("/api/chats/group")
                        .queryParam("creatorId", creator.toString())
                        .contentType("application/json")
                        .content("""
                                { "name": "MyGroup" }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Chat"));

        // then (business call)
        ArgumentCaptor<String> nameCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UUID> creatorCap = ArgumentCaptor.forClass(UUID.class);

        verify(chatService).createGroupChat(nameCap.capture(), creatorCap.capture());
        assertThat(nameCap.getValue()).isEqualTo("MyGroup");
        assertThat(creatorCap.getValue()).isEqualTo(creator);
    }

    @Test
    @DisplayName("POST /api/chats/{chatId}/participants/{userId} — should add user to group")
    void shouldAddUserToGroup() throws Exception {
        // given
        UUID chatId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        // when / then
        mockMvc.perform(post("/api/chats/" + chatId + "/participants/" + userId))
                .andExpect(status().isOk());

        // then (verify)
        verify(chatService).addUserToGroup(chatId, userId);
    }

    @Test
    @DisplayName("DELETE /api/chats/{chatId}/participants/{userId} — should remove user from group")
    void shouldRemoveUserFromGroup() throws Exception {
        // given
        UUID chatId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        // when / then
        mockMvc.perform(delete("/api/chats/" + chatId + "/participants/" + userId))
                .andExpect(status().isOk());

        // then
        verify(chatService).removeUserFromGroup(chatId, userId);
    }

    @Test
    @DisplayName("GET /api/chats/{chatId} — should return chat details")
    void shouldGetChat() throws Exception {
        // given
        UUID id = UUID.randomUUID();
        exampleChat.setId(id);

        when(chatService.getChat(id)).thenReturn(exampleChat);

        // when / then
        mockMvc.perform(get("/api/chats/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Test Chat"));
    }

    @Test
    @DisplayName("GET /api/chats/user/{userId} — should return chat list")
    void shouldGetUserChats() throws Exception {
        // given
        UUID userId = UUID.randomUUID();

        when(chatService.getUserChats(userId)).thenReturn(List.of(exampleChat));

        // when / then
        mockMvc.perform(get("/api/chats/user/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Chat"));
    }
}
