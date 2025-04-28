package com.chatting.chatserviceback.chat;

import com.chatting.chatserviceback.chat.domain.ChatMessage;
import com.chatting.chatserviceback.chat.repository.ChatMessageRepositoryImpl;
import com.chatting.chatserviceback.member.domain.Member;
import com.chatting.chatserviceback.member.domain.QMember;
import com.chatting.chatserviceback.member.repository.MemberRepositoryImpl;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
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

    @Autowired
    private JPAQueryFactory queryFactory;

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
    @DisplayName("쿼리 DSL 조회")
    void queryDslSelect(){
        //given
        QMember member = QMember.member;
        //when
        Member findMemberQueryDsl = queryFactory
            .selectFrom(member).where(member.id.eq(receiver.getId()))
            .fetchOne();

        //then
        System.out.println(findMemberQueryDsl.getUsername());
    }

    @Test
    @DisplayName("JPQL 조회")
    void JPQLSelect(){
        //given
        String qlString =
            "select m from Member m " +
                "where m.id = :id";
        //when
        Member findMember = em.createQuery(qlString, Member.class)
            .setParameter("id", receiver.getId()).getSingleResult();
        //then
        System.out.println(findMember.getUsername());
    }


    @Test
    @DisplayName("쿼리 DSL 기본 문법")
    void queryDslBasic(){
        //given
        QMember member = QMember.member;
        //when
        Member findMemberQueryDsl = queryFactory
            .selectFrom(member).where(member.id.eq(receiver.getId()))
            .fetchOne();
        Member test = queryFactory
            .selectFrom(member).where(member.id.eq(receiver.getId()))
            .fetchOne();


        //then
        System.out.println(findMemberQueryDsl.getUsername());
        member.username.eq("member1") ;// username = 'member1'
        member.username.ne("member1") ;//username != 'member1'
        member.username.eq("member1").not() ;// username != 'member1'
        member.username.isNotNull() ;//이름이 is not null
        member.id.in(10, 20) ;// id in (10,20)
        member.id.notIn(10, 20) ;// id not in (10, 20)
        member.id.between(10,30) ;//between 10, 30
        member.id.goe(30); // id >= 30
        member.id.gt(30); // id > 30
        member.id.loe(30); // id <= 30
        member.id.lt(30);// id < 30
        member.username.like("member%");//like 검색
        member.username.contains("member"); // like ‘%member%’ 검색
        member.username.startsWith("member"); //like ‘member%’ 검색

        //List
        List<Member> fetch = queryFactory
            .selectFrom(member)
            .fetch();
        //단 건
        Member findMember1 = queryFactory
            .selectFrom(member)
            .fetchOne();
        //처음 한 건 조회
        Member findMember2 = queryFactory
            .selectFrom(member)
            .fetchFirst();


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