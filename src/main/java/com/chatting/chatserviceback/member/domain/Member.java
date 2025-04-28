package com.chatting.chatserviceback.member.domain;

import com.chatting.chatserviceback.chat.domain.ChatMessage;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    // 양방향 연관 관계 주인 sender
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "sender")
    private List<ChatMessage> sendChatMessageList = new ArrayList<>();

    @Builder
    public Member(Long id, String email, String username, String password,
        List<ChatMessage> sendChatMessageList) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.sendChatMessageList = sendChatMessageList;
    }

    public static Member create(String username, String  email, String password) {
        return Member.builder()
            .username(username)
            .email(email)
            .password(password)
            .build();
    }
}

