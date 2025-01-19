package com.example.msamemberapi.application.repository;

import com.example.msamemberapi.application.entity.MemberGradeHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberGradeHistoryRepository extends JpaRepository<MemberGradeHistory, Long> {
    List<MemberGradeHistory> findByMemberId(Long memberId);

}