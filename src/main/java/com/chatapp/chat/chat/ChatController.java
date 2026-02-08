package com.chatapp.chat.chat;

import com.chatapp.chat.chat.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/direct")
    public ChatResponse createDirect(@RequestBody CreateDirectChatRequest req) {
        Chat chat = chatService.createDirectChat(req.user1(), req.user2());
        return ChatResponse.fromEntity(chat);
    }

    @PostMapping("/group")
    public ChatResponse createGroup(@RequestBody CreateGroupChatRequest req,
                                    @RequestParam UUID creatorId) {
        Chat chat = chatService.createGroupChat(req.name(), creatorId);
        return ChatResponse.fromEntity(chat);
    }

    @PostMapping("/{chatId}/participants/{userId}")
    public void addUser(@PathVariable UUID chatId, @PathVariable UUID userId) {
        chatService.addUserToGroup(chatId, userId);
    }

    @DeleteMapping("/{chatId}/participants/{userId}")
    public void removeUser(@PathVariable UUID chatId, @PathVariable UUID userId) {
        chatService.removeUserFromGroup(chatId, userId);
    }

    @GetMapping("/{chatId}")
    public ChatResponse getChat(@PathVariable UUID chatId) {
        return ChatResponse.fromEntity(chatService.getChat(chatId));
    }

    @GetMapping("/user/{userId}")
    public List<ChatResponse> getUserChats(@PathVariable UUID userId) {
        return chatService.getUserChats(userId).stream()
                .map(ChatResponse::fromEntity)
                .toList();
    }
}
