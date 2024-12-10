package com.example.msamemberapi.application.repository;

import com.example.msamemberapi.application.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String> {

}
