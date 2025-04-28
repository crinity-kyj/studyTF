package com.chatting.chatserviceback.chat.service;

import com.chatting.chatserviceback.chat.repository.ChatMessageRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepositoryImpl chatMessageRepository;
}
