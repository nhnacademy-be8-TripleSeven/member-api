package com.example.msamemberapi.application.repository;

import com.example.msamemberapi.application.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByMemberAccount_Id(String memberAccountId);
}
