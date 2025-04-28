package com.chatting.chatserviceback.chat.repository;

import com.chatting.chatserviceback.chat.domain.ChatMessage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatMessageJpaRepository extends JpaRepository<ChatMessage, Long> {

    @Query("SELECT cm FROM ChatMessage cm " +
        "JOIN FETCH cm.sender " +
        "JOIN FETCH cm.receiver " +
        "WHERE cm.id = :chatMessageId")
    Optional<ChatMessage> findByIdWithMember(@Param("chatMessageId") Long chatMessageId);

    @Query("SELECT cm FROM ChatMessage cm " +
        "JOIN FETCH cm.sender " +
        "WHERE cm.sender.id = :senderId")
    List<ChatMessage> findChatMessagesBySender(@Param("senderId") Long senderId);

}
