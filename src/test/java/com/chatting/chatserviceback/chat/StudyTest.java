package com.chatting.chatserviceback.chat;

import com.chatting.chatserviceback.chat.domain.ChatMessage;
import com.chatting.chatserviceback.chat.repository.ChatMessageRepositoryImpl;
import com.chatting.chatserviceback.member.domain.Member;
import com.chatting.chatserviceback.member.repository.MemberRepositoryImpl;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class StudyTest {

    @Autowired
    private ChatMessageRepositoryImpl chatMessageRepositoryImpl;

    @Autowired
    private MemberRepositoryImpl memberRepositoryImpl;

    @Autowired
    private EntityManager em;

    private Member sender;
    private Member receiver;

    @BeforeEach
    void setUp() {
        Member member = Member.create("member1", "member@gmail.com");
        sender = memberRepositoryImpl.save(member);
        Member member2 = Member.create("member2", "member2@gmail.com");
        receiver = memberRepositoryImpl.save(member2);

        List<Member> saveList = new ArrayList<>();

        for(int i = 0 ; i < 10 ; i++) {
            Member memberSave = Member.create("memberTest" + i, "memberTest"+ i +"@gmail.com");
            saveList.add(memberSave);
        }
        memberRepositoryImpl.saveAll(saveList);

        // ChatMessage 여러 개 저장
        for (int i = 0; i < 10; i++) {
            ChatMessage chatMessage = ChatMessage.create(
                sender,
                receiver,
                "메시지 내용 " + i,
                LocalDateTime.now().plusMinutes(i)
            );
            chatMessageRepositoryImpl.save(chatMessage);
        }
        em.flush();  // DB에 쿼리 반영
        em.clear();  // 1차 캐시 비워버림
    }

    @Test
    @DisplayName("채팅 저장")
    void saveChatMessage() {

        //given
        ChatMessage chatMessage = ChatMessage.create(sender,receiver,"content", LocalDateTime.of(2020,1,1,1,1));
        //when
        ChatMessage save = chatMessageRepositoryImpl.save(chatMessage);

        //then
        Assertions.assertThat(save).isNotNull();
        Assertions.assertThat(save.getSentAt()).isEqualTo(LocalDateTime.of(2020, 1, 1, 1, 1));
        Assertions.assertThat(save.getReceiver()).isEqualTo(receiver);
        Assertions.assertThat(save.getSender()).isEqualTo(sender);

        Assertions.assertThat(save.getContent()).isEqualTo("content");
    }

    @Test
    @DisplayName("회원 정보 업데이트")
    void updateMember(){
        //given
        Member member = memberRepositoryImpl.findById(sender.getId()).get();
        //when
        member.setUsername("updateMember");
        //then
        em.flush();  // DB에 쿼리 반영
        em.clear();  // 1차 캐시 비워버림

        Member updateMember = memberRepositoryImpl.findById(sender.getId()).get();
        Assertions.assertThat(updateMember.getUsername()).isEqualTo("updateMember");

    }

    @Test
    @DisplayName("Lazy 지연 로딩 스터디")
    void studyLazyLoading(){
        //given
        ChatMessage chatMessage = ChatMessage.create(sender,receiver,"content", LocalDateTime.of(2020,1,1,1,1));
        //when
        ChatMessage save = chatMessageRepositoryImpl.save(chatMessage);
        // 지금까지 작업 DB 반영


        ChatMessage findChatMessage = chatMessageRepositoryImpl.findById(save.getId()).get();
        //then
        System.out.println("findChatMessage.getContent() = " + findChatMessage.getContent());
        System.out.println("findChatMessage.getSender().getName() = " + findChatMessage.getSender().getUsername());
    }


    @Test
    @DisplayName("Eager 즉시 로딩 스터디 (실무 사용 X)")
    void studyEagerLoading(){
        //given
        ChatMessage chatMessage = ChatMessage.create(sender,receiver,"content", LocalDateTime.of(2020,1,1,1,1));
        //when
        ChatMessage save = chatMessageRepositoryImpl.save(chatMessage);
        // 지금까지 작업 DB 반영
        em.flush();  // DB에 쿼리 반영
        em.clear();  // 1차 캐시 비워버림

        ChatMessage findChatMessage = chatMessageRepositoryImpl.findById(save.getId()).get();
        //then
        System.out.println("findChatMessage.getContent() = " + findChatMessage.getContent());
        System.out.println("findChatMessage.getReceiver().getName() = " + findChatMessage.getReceiver().getUsername());
    }

    @Test
    @DisplayName("페치 조인 스터디")
    void studyFetchJoin(){
        //given

        em.flush();  // DB에 쿼리 반영
        em.clear();  // 1차 캐시 비워버림
        //when
        // Sender Lazy 지연 로딩이지만 한번에 가져온다.
        List<ChatMessage> chatMessagesBySender = chatMessageRepositoryImpl.findChatMessagesBySender(
            sender.getId());
        //then
        Assertions.assertThat(chatMessagesBySender).hasSize(10);
    }

    @Test
    @DisplayName("N+1 문제 재현 테스트")
    void nPlusOneProblemTest() {
        // given

        // when 총 12번의 조회 쿼리 발생 보낸 메시지
        // N+1 인 회원 모두를 조회하는 쿼리 1 + 회원수(N)만큼 메시지 조회
        List<Member> all = memberRepositoryImpl.findAll();

        // then
        // (여기서는 쿼리 로그를 눈으로 확인)
    }



    @Test
    @DisplayName("채팅 queryDsl 로 조회")
    void searchChatMessage() {
        //given

        ChatMessage chatMessage = ChatMessage.create(sender,receiver,"content", LocalDateTime.of(2020,1,1,1,1));
        //when
        ChatMessage save = chatMessageRepositoryImpl.save(chatMessage);
        List<ChatMessage> messagesBetween = chatMessageRepositoryImpl.findMessagesBetween(
            sender.getId(), receiver.getId());

        //then

    }
}