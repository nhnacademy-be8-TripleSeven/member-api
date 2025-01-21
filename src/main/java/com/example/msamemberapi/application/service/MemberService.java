package com.example.msamemberapi.application.service;

import com.example.msamemberapi.application.dto.request.JoinRequestDto;
import com.example.msamemberapi.application.dto.request.UpdatePasswordRequestDto;
import com.example.msamemberapi.application.dto.response.MemberAccountInfo;
import com.example.msamemberapi.application.dto.response.MemberAuthInfo;
import com.example.msamemberapi.application.dto.response.MemberDto;
import com.example.msamemberapi.application.dto.response.MemberGradeDto;
import com.example.msamemberapi.application.dto.response.MemberGradeHistoryDto;
import com.example.msamemberapi.application.entity.Member;
import com.example.msamemberapi.application.enums.MemberGrade;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface MemberService {

    MemberDto join(JoinRequestDto joinRequestDto);
    MemberAuthInfo findByMemberId(String memberId);

    MemberDto getMemberDTOById(Long memberId);

    MemberAuthInfo findByMemberId(Long memberId);
    void quitMember(Long memberId);
    MemberAccountInfo getMemberAccountByPhoneNumber(String phoneNumber);
    MemberAccountInfo getMemberAccountByEmail(String email);
    void validateMatchingLoginIdAndEmail(String email, String loginId);
    void updateMemberPassword(UpdatePasswordRequestDto updatePasswordRequestDto);

    Page<MemberDto> getMembers(String name, MemberGrade memberGrade, Pageable pageable);
    void updateLastLoggedInAt(Long userId);


    MemberDto getMember(Long id);

    void deleteMember(Long id);

    MemberDto findMemberInfoByUserId(Long s);
    boolean verifyPassword(Long memberId, String password);
    MemberDto updateMember(Long userId, MemberDto memberDto);
    MemberDto updateMemberInfo(Long userId, MemberDto memberDto);

    MemberGradeDto getMemberGrade(Long memberId);
    List<MemberGradeHistoryDto> getGradeHistory(Long memberId);

    MemberDto getMemberInfo(Long userId);
    void saveMember(Member member);

    Member getMemberById(Long userId);

    @Transactional
    void updateMemberGrade();
}