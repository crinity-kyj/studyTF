package com.chatting.chatserviceback.chat.controller;

import com.chatting.chatserviceback.chat.domain.ChatMessage;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatMessageResponse {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String content;
    private LocalDateTime sentAt;

    public static ChatMessageResponse fromEntity(ChatMessage message) {
        return new ChatMessageResponse(
            message.getId(),
            message.getSender().getId(),
            message.getReceiver().getId(),
            message.getContent(),
            message.getSentAt()
        );
    }
}
