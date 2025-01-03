package com.example.msamemberapi.application.repository.querydsl;

import com.example.msamemberapi.application.dto.response.MemberDto;
import com.example.msamemberapi.application.enums.MemberGrade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberRepositoryCustom {
    Page<MemberDto> findMembers(String name, MemberGrade memberGrade, Pageable pageable);
}
