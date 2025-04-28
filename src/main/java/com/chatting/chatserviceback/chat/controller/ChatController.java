package com.chatting.chatserviceback.chat.controller;

import com.chatting.chatserviceback.chat.domain.ChatMessage;
import com.chatting.chatserviceback.chat.dto.ChatMessageDto;
import com.chatting.chatserviceback.chat.repository.ChatMessageRepositoryImpl;
import com.chatting.chatserviceback.member.domain.Member;
import com.chatting.chatserviceback.member.repository.MemberRepositoryImpl;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepositoryImpl chatMessageRepository;

    private final MemberRepositoryImpl memberRepository;

    @MessageMapping("/chat/send")
    public void send(ChatMessageDto dto) {
        Member sender = memberRepository.findById(dto.getSenderId()).orElseThrow();
        Member receiver = memberRepository.findById(dto.getReceiverId()).orElseThrow();

        ChatMessage message = ChatMessage.create(sender, receiver, dto.getContent(), LocalDateTime.now());
        chatMessageRepository.save(message); // DB 저장

        // 저장된 메시지를 DTO로 만들어서 전송
        messagingTemplate.convertAndSend("/topic/chat/" + dto.getReceiverId(), ChatMessageResponse.fromEntity(message));
    }


    @GetMapping("/messages")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(
        @RequestParam Long senderId,
        @RequestParam Long receiverId) {

        List<ChatMessage> messages = chatMessageRepository.findMessagesBetween(senderId, receiverId);

        List<ChatMessageResponse> response = messages.stream()
            .map(ChatMessageResponse::fromEntity)
            .toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/messages")
    public ResponseEntity<Void> sendMessage(@RequestBody ChatMessageRequest request) {
        Member sender = memberRepository.findById(request.getSenderId()).orElseThrow();
        Member receiver = memberRepository.findById(request.getReceiverId()).orElseThrow();

        ChatMessage message = ChatMessage.create(sender, receiver, request.getContent(), LocalDateTime.now());
        chatMessageRepository.save(message);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/messages/{id}")
    public ResponseEntity<ChatMessageResponse> getMessageById(@PathVariable Long id) {
        ChatMessage message = chatMessageRepository.findById(id).orElseThrow();
        return ResponseEntity.ok(ChatMessageResponse.fromEntity(message));
    }

}

