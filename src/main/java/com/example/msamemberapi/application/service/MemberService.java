package com.example.msamemberapi.application.service;

import com.example.msamemberapi.application.dto.request.JoinRequestDto;
import com.example.msamemberapi.application.dto.request.UpdatePasswordRequestDto;
import com.example.msamemberapi.application.dto.response.MemberAccountInfo;
import com.example.msamemberapi.application.dto.response.MemberAuthInfo;
import com.example.msamemberapi.application.dto.response.MemberDto;
import com.example.msamemberapi.application.dto.response.MemberGradeDto;
import com.example.msamemberapi.application.dto.response.MemberGradeHistoryDto;
import com.example.msamemberapi.application.enums.MemberGrade;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;

public interface MemberService {

    MemberDto join(JoinRequestDto joinRequestDto);
    MemberAuthInfo findByMemberId(String memberId);
    MemberAuthInfo findByMemberId(Long memberId);
    void quitMember(Long memberId);
    MemberAccountInfo getMemberAccountByPhoneNumber(String phoneNumber);
    MemberAccountInfo getMemberAccountByEmail(String email);
    void validateMatchingLoginIdAndEmail(String email, String loginId);
    void updateMemberPassword(UpdatePasswordRequestDto updatePasswordRequestDto);

    Page<MemberDto> getMembers(String name, MemberGrade memberGrade, Pageable pageable);
    void updateLastLoggedInAt(Long userId);


    MemberDto getMember(Long id);
    void updateMember(Long id, MemberDto memberDto);
    void deleteMember(Long id);

    void updateMemberInfo(String s, MemberDto memberDto);

    MemberDto findMemberInfoByUserId(String s);
    MemberGradeDto getMemberGrade(Long memberId); // 회원 등급 조회
    List<MemberGradeHistoryDto> getGradeHistory(Long memberId); // 등급 변경 기록 조회
}
