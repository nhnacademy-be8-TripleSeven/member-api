package com.example.msamemberapi.application.repository;

import com.example.msamemberapi.application.entity.Member;
import com.example.msamemberapi.application.repository.querydsl.MemberRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    Optional<Member> findByMemberAccount_Id(String memberAccountId);
    Optional<Member> findByUserPhoneNumber(String phoneNumber);
    Optional<Member> findByEmail(String email);

    boolean existsByMemberAccount_Id(String memberAccountId);

    boolean existsByEmail(String email);
    boolean existsByUserPhoneNumber(String phoneNumber);
}
