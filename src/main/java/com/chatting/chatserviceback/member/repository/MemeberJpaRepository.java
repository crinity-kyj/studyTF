package com.chatting.chatserviceback.member.repository;

import com.chatting.chatserviceback.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemeberJpaRepository extends JpaRepository<Member, Long> {

}
