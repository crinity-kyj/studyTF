package com.chatting.chatserviceback.chat.repository;

import com.chatting.chatserviceback.chat.domain.ChatMessage;
import com.chatting.chatserviceback.chat.domain.QChatMessage;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatMessageRepositoryImpl {

    private final ChatMessageJpaRepository chatMessageJpaRepository;

    private final JPAQueryFactory jpaQueryFactory;

    public ChatMessage save(ChatMessage chatMessage) {
        return chatMessageJpaRepository.save(chatMessage);
    }

    public List<ChatMessage> findMessagesBetween(Long senderId, Long receiverId) {
        QChatMessage chat = QChatMessage.chatMessage;

        return jpaQueryFactory.selectFrom(chat)
            .where(
                chat.sender.id.eq(senderId).and(chat.receiver.id.eq(receiverId))
                    .or(chat.sender.id.eq(receiverId).and(chat.receiver.id.eq(senderId)))
            )
            .orderBy(chat.sentAt.asc())
            .fetch();
    }

    public Optional<ChatMessage> findById(Long id) {
        return chatMessageJpaRepository.findById(id);
    }

    public Optional<ChatMessage> findByIdWithMember(Long id) {
        return chatMessageJpaRepository.findByIdWithMember(id);
    }

    public List<ChatMessage> findChatMessagesBySender(Long senderId) {
        return chatMessageJpaRepository.findChatMessagesBySender(senderId);
    }

    public List<ChatMessage> findAll() {
        return chatMessageJpaRepository.findAll();
    }
}
