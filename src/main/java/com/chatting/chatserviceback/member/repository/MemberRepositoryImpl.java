package com.chatting.chatserviceback.member.repository;


import com.chatting.chatserviceback.member.domain.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl {

    private final MemeberJpaRepository memeberJpaRepository;

    private final JPAQueryFactory jpaQueryFactory;

    public Member save(Member member) {
        return memeberJpaRepository.save(member);
    }

    public Optional<Member> findById(Long id) {
        return memeberJpaRepository.findById(id);
    }

    public List<Member> findAll() {
        return memeberJpaRepository.findAll();
    }

    public List<Member> saveAll(List<Member> members) {
        return memeberJpaRepository.saveAll(members);
    }
}
