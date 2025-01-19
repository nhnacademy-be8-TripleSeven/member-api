package com.example.msamemberapi.application.repository;

import com.example.msamemberapi.application.entity.Member;
import com.example.msamemberapi.application.repository.querydsl.MemberRepositoryCustom;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
    Optional<Member> findByUserPhoneNumber(String phoneNumber);
    Optional<Member> findByEmail(String email);
    Optional<Member> findByMemberAccount_Id(String memberAccountId);

    boolean existsByMemberAccount_Id(String memberAccountId);


    boolean existsByEmail(String email);
    boolean existsByUserPhoneNumber(String phoneNumber);

    Page<Member> findByEmailContaining(String email, Pageable pageable);

}

