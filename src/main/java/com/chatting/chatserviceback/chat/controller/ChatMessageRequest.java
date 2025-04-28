package com.chatting.chatserviceback.chat.controller;

import lombok.Getter;

@Getter
public class ChatMessageRequest {
    private Long senderId;
    private Long receiverId;
    private String content;
}
