package com.chatting.chatserviceback.chat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageDto {
    private Long receiverId;
    private String content;
    private Long senderId;
}
