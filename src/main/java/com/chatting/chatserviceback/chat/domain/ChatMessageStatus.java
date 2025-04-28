package com.chatting.chatserviceback.chat.domain;

public enum ChatMessageStatus {

    DISABLED("비활성화"),
    ENABLED("활성화");

    private String description;

    ChatMessageStatus(String description) {
        this.description = description;
    }

}
