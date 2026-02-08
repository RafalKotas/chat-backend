package com.chatapp.chat.chat;

import com.chatapp.chat.chat.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatService {

    private static final String CHAT_NOT_FOUND = "Chat not found";

    private final ChatRepository chatRepository;
    private final ChatParticipantRepository participantRepository;

    @Transactional
    public Chat createDirectChat(UUID user1, UUID user2) {

        Optional<Chat> existing = chatRepository.findDirectChatBetweenUsers(user1, user2);
        if (existing.isPresent()) {
            return existing.get();
        }

        Chat chat = Chat.builder()
                .type(ChatType.DIRECT)
                .name(null)
                .build();

        Chat saved = chatRepository.save(chat);

        ChatParticipant p1 = ChatParticipant.builder()
                .chat(saved)
                .userId(user1)
                .role(ChatRole.MEMBER)
                .build();

        ChatParticipant p2 = ChatParticipant.builder()
                .chat(saved)
                .userId(user2)
                .role(ChatRole.MEMBER)
                .build();

        participantRepository.saveAll(List.of(p1, p2));

        return saved;
    }

    @Transactional
    public Chat createGroupChat(String name, UUID creatorId) {

        Chat chat = Chat.builder()
                .type(ChatType.GROUP)
                .name(name)
                .build();

        Chat saved = chatRepository.save(chat);

        ChatParticipant creator = ChatParticipant.builder()
                .chat(saved)
                .userId(creatorId)
                .role(ChatRole.ADMIN)
                .build();

        participantRepository.save(creator);

        return saved;
    }

    @Transactional
    public void addUserToGroup(UUID chatId, UUID userId) {

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException(CHAT_NOT_FOUND));

        if (chat.getType() != ChatType.GROUP) {
            throw new UnauthorizedChatOperationException("Cannot add users to direct chat");
        }

        boolean alreadyInChat = participantRepository.existsByChatIdAndUserId(chatId, userId);
        if (alreadyInChat) {
            throw new AlreadyInChatException("User already in chat");
        }

        ChatParticipant newParticipant = ChatParticipant.builder()
                .chat(chat)
                .userId(userId)
                .role(ChatRole.MEMBER)
                .build();

        participantRepository.save(newParticipant);
    }

    @Transactional
    public void removeUserFromGroup(UUID chatId, UUID userId) {

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException(CHAT_NOT_FOUND));

        if (chat.getType() != ChatType.GROUP) {
            throw new UnauthorizedChatOperationException("Cannot remove users from direct chat");
        }

        ChatParticipant participant = participantRepository
                .findByChatIdAndUserId(chatId, userId)
                .orElseThrow(() -> new UserNotInChatException("User not in chat"));

        participantRepository.delete(participant);
    }

    public Chat getChat(UUID chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException(CHAT_NOT_FOUND));
    }

    public List<Chat> getUserChats(UUID userId) {
        return chatRepository.findAllByUserId(userId);
    }
}
