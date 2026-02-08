package com.chatapp.chat.chat;

import com.chatapp.chat.chat.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChatServiceTest {

    ChatRepository chatRepository;
    ChatParticipantRepository participantRepository;

    ChatService subject;

    @BeforeEach
    void setUp() {
        chatRepository = mock(ChatRepository.class);
        participantRepository = mock(ChatParticipantRepository.class);
        subject = new ChatService(chatRepository, participantRepository);
    }

    @Test
    @DisplayName("createDirectChat() should return existing direct chat if one already exists")
    void shouldReturnExistingDirectChat() {
        // given
        UUID u1 = UUID.randomUUID();
        UUID u2 = UUID.randomUUID();

        Chat existing = Chat.builder()
                .id(UUID.randomUUID())
                .type(ChatType.DIRECT)
                .build();

        when(chatRepository.findDirectChatBetweenUsers(u1, u2))
                .thenReturn(Optional.of(existing));

        // when
        Chat result = subject.createDirectChat(u1, u2);

        // then
        assertThat(result).isEqualTo(existing);
        verify(chatRepository, never()).save(any());
        verify(participantRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("createDirectChat() should create a new chat and 2 participants if none exists")
    void shouldCreateNewDirectChat() {
        // given
        UUID u1 = UUID.randomUUID();
        UUID u2 = UUID.randomUUID();

        Chat newChat = Chat.builder()
                .id(UUID.randomUUID())
                .type(ChatType.DIRECT)
                .build();

        when(chatRepository.findDirectChatBetweenUsers(u1, u2))
                .thenReturn(Optional.empty());

        when(chatRepository.save(any(Chat.class)))
                .thenReturn(newChat);

        // when
        Chat result = subject.createDirectChat(u1, u2);

        // then
        assertThat(result).isEqualTo(newChat);

        ArgumentCaptor<List<ChatParticipant>> captor = ArgumentCaptor.forClass(List.class);
        verify(participantRepository).saveAll(captor.capture());

        List<ChatParticipant> saved = captor.getValue();
        assertThat(saved).hasSize(2);

        assertThat(saved.get(0).getUserId()).isEqualTo(u1);
        assertThat(saved.get(1).getUserId()).isEqualTo(u2);
    }

    @Test
    @DisplayName("createGroupChat() should create a group chat and add creator as ADMIN")
    void shouldCreateGroupChat() {
        // given
        UUID creator = UUID.randomUUID();

        Chat created = Chat.builder()
                .id(UUID.randomUUID())
                .type(ChatType.GROUP)
                .name("MyGroup")
                .build();

        when(chatRepository.save(any(Chat.class))).thenReturn(created);

        // when
        Chat result = subject.createGroupChat("MyGroup", creator);

        // then
        assertThat(result).isEqualTo(created);

        ArgumentCaptor<ChatParticipant> captor = ArgumentCaptor.forClass(ChatParticipant.class);
        verify(participantRepository).save(captor.capture());

        ChatParticipant saved = captor.getValue();
        assertThat(saved.getUserId()).isEqualTo(creator);
        assertThat(saved.getRole()).isEqualTo(ChatRole.ADMIN);
    }

    @Test
    @DisplayName("addUserToGroup() should throw if chat does not exist")
    void addUserShouldFailIfChatNotFound() {
        // given
        UUID chatId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(chatRepository.findById(chatId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> subject.addUserToGroup(chatId, userId))
                .isInstanceOf(ChatNotFoundException.class)
                .hasMessage("Chat not found");
    }

    @Test
    @DisplayName("addUserToGroup() should reject adding users to direct chat")
    void addUserShouldFailForDirectChat() {
        // given
        UUID chatId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Chat directChat = Chat.builder()
                .type(ChatType.DIRECT)
                .build();

        when(chatRepository.findById(chatId)).thenReturn(Optional.of(directChat));

        // when & then
        assertThatThrownBy(() -> subject.addUserToGroup(chatId, userId))
                .isInstanceOf(UnauthorizedChatOperationException.class)
                .hasMessage("Cannot add users to direct chat");
    }

    @Test
    @DisplayName("addUserToGroup() should throw if user is already in chat")
    void addUserShouldRejectExistingParticipant() {
        // given
        UUID chatId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Chat group = Chat.builder()
                .type(ChatType.GROUP)
                .build();

        when(chatRepository.findById(chatId)).thenReturn(Optional.of(group));
        when(participantRepository.existsByChatIdAndUserId(chatId, userId))
                .thenReturn(true);

        // when & then
        assertThatThrownBy(() -> subject.addUserToGroup(chatId, userId))
                .isInstanceOf(AlreadyInChatException.class)
                .hasMessage("User already in chat");
    }

    @Test
    @DisplayName("addUserToGroup() should add user as MEMBER")
    void shouldAddUserToGroup() {
        // given
        UUID chatId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Chat group = Chat.builder()
                .type(ChatType.GROUP)
                .build();

        when(chatRepository.findById(chatId)).thenReturn(Optional.of(group));
        when(participantRepository.existsByChatIdAndUserId(chatId, userId))
                .thenReturn(false);

        // when
        subject.addUserToGroup(chatId, userId);

        // then
        ArgumentCaptor<ChatParticipant> captor = ArgumentCaptor.forClass(ChatParticipant.class);
        verify(participantRepository).save(captor.capture());

        ChatParticipant saved = captor.getValue();
        assertThat(saved.getUserId()).isEqualTo(userId);
        assertThat(saved.getRole()).isEqualTo(ChatRole.MEMBER);
    }

    @Test
    @DisplayName("removeUserFromGroup() should throw if chat does not exist")
    void removeUserShouldFailIfChatNotFound() {
        // given
        UUID chatId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(chatRepository.findById(chatId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> subject.removeUserFromGroup(chatId, userId))
                .isInstanceOf(ChatNotFoundException.class)
                .hasMessage("Chat not found");
    }

    @Test
    @DisplayName("removeUserFromGroup() should reject removing from direct chat")
    void removeUserShouldFailForDirectChat() {
        // given
        UUID chatId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Chat direct = Chat.builder().type(ChatType.DIRECT).build();

        when(chatRepository.findById(chatId)).thenReturn(Optional.of(direct));

        // then
        assertThatThrownBy(() -> subject.removeUserFromGroup(chatId, userId))
                .isInstanceOf(UnauthorizedChatOperationException.class)
                .hasMessage("Cannot remove users from direct chat");
    }

    @Test
    @DisplayName("removeUserFromGroup() should throw if user is not in chat")
    void removeUserShouldFailIfUserNotInChat() {
        // given
        UUID chatId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Chat group = Chat.builder().type(ChatType.GROUP).build();

        when(chatRepository.findById(chatId)).thenReturn(Optional.of(group));
        when(participantRepository.findByChatIdAndUserId(chatId, userId))
                .thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> subject.removeUserFromGroup(chatId, userId))
                .isInstanceOf(UserNotInChatException.class)
                .hasMessage("User not in chat");
    }

    @Test
    @DisplayName("removeUserFromGroup() should remove user")
    void shouldRemoveUserFromGroup() {
        // given
        UUID chatId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Chat group = Chat.builder().type(ChatType.GROUP).build();
        ChatParticipant participant = ChatParticipant.builder()
                .userId(userId)
                .chat(group)
                .build();

        when(chatRepository.findById(chatId)).thenReturn(Optional.of(group));
        when(participantRepository.findByChatIdAndUserId(chatId, userId))
                .thenReturn(Optional.of(participant));

        // when
        subject.removeUserFromGroup(chatId, userId);

        // then
        verify(participantRepository).delete(participant);
    }

    // -------------------------------------------------------------------------
    // getChat()
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getChat() should return chat if exists")
    void shouldGetChat() {
        // given
        UUID id = UUID.randomUUID();
        Chat chat = Chat.builder().id(id).build();

        when(chatRepository.findById(id)).thenReturn(Optional.of(chat));

        // when
        Chat result = subject.getChat(id);

        // then
        assertThat(result).isEqualTo(chat);
    }

    @Test
    @DisplayName("getChat() should throw if chat not found")
    void getChatShouldFailIfMissing() {
        // given
        UUID id = UUID.randomUUID();
        when(chatRepository.findById(id)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> subject.getChat(id))
                .isInstanceOf(ChatNotFoundException.class)
                .hasMessage("Chat not found");
    }

    @Test
    @DisplayName("getUserChats() should return chats for user")
    void shouldReturnUserChats() {
        // givenen
        UUID userId = UUID.randomUUID();
        Chat chat = Chat.builder().id(UUID.randomUUID()).build();

        when(chatRepository.findAllByUserId(userId))
                .thenReturn(List.of(chat));

        // when
        List<Chat> result = subject.getUserChats(userId);

        // then
        assertThat(result).containsExactly(chat);
    }
}
