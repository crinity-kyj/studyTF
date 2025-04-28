package com.chatting.chatserviceback.chat.domain;

import com.chatting.chatserviceback.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_message_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Member sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private Member receiver;

    @Column(name = "content", nullable = false, length = 255)
    private String content;

    @Column(name = "send_at")
    private LocalDateTime sentAt;

    @Enumerated(EnumType.STRING)
    private ChatMessageStatus messageStatus;


    @Builder
    public ChatMessage(Long id, Member sender, Member receiver, String content,
        LocalDateTime sentAt) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.sentAt = sentAt;
    }

    public static ChatMessage create(Member sender, Member receiver, String content, LocalDateTime sentAt) {
        return ChatMessage.builder()
            .sender(sender)
            .receiver(receiver)
            .content(content)
            .sentAt(sentAt)
            .build();
    }
}
