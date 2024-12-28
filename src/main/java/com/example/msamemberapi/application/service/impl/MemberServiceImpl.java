package com.example.msamemberapi.application.service.impl;

import com.example.msamemberapi.application.dto.request.JoinRequestDto;
import com.example.msamemberapi.application.dto.response.MemberAccountInfo;
import com.example.msamemberapi.application.dto.response.MemberAuthInfo;
import com.example.msamemberapi.application.dto.response.MemberDto;
import com.example.msamemberapi.application.entity.Member;
import com.example.msamemberapi.application.entity.MemberAccount;
import com.example.msamemberapi.application.entity.MemberGradeHistory;
import com.example.msamemberapi.application.enums.MemberGrade;
import com.example.msamemberapi.application.enums.MemberRole;
import com.example.msamemberapi.application.error.CustomException;
import com.example.msamemberapi.application.error.ErrorCode;
import com.example.msamemberapi.application.repository.MemberRepository;
import com.example.msamemberapi.application.service.EmailVerifyService;
import com.example.msamemberapi.application.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public MemberDto join(JoinRequestDto joinRequestDto) {

        validateUniqueMember(joinRequestDto);
        MemberAccount memberAccount = createMemberAccount(joinRequestDto);
        Member member = createMember(joinRequestDto, memberAccount);
        return new MemberDto(memberRepository.save(member));
    }

    @Override
    @Transactional(readOnly = true)
    public MemberAuthInfo findByMemberId(String loginId) {
        Member member = memberRepository.findByMemberAccount_Id(loginId).orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_ID_NOT_FOUND));
        return new MemberAuthInfo(member);
    }

    @Override
    @Transactional
    public void deleteByMemberId(Long memberId) {
        memberRepository.deleteById(memberId);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberAccountInfo getMemberAccountByEmail(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.EMAIL_NOT_FOUND));

        return new MemberAccountInfo(member.getMemberAccount());
    }

    @Override
    @Transactional(readOnly = true)
    public MemberAccountInfo getMemberAccountByPhoneNumber(String phoneNumber) {
        Member member = memberRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new CustomException(ErrorCode.PHONE_NOT_FOUND));

        return new MemberAccountInfo(member.getMemberAccount());
    }

    private Member createMember(JoinRequestDto joinRequestDto, MemberAccount memberAccount) {
        Member member = Member.builder()
                .memberAccount(memberAccount)
                .birth(joinRequestDto.getBirth())
                .gender(joinRequestDto.getGender())
                .email(joinRequestDto.getEmail())
                .phoneNumber(joinRequestDto.getPhoneNumber())
                .name(joinRequestDto.getName())
                .gradeHistories(new ArrayList<>())
                .build();

        MemberGradeHistory gradeHistory = createMemberGradeHistory(member);
        member.addGradeHistory(gradeHistory);
        member.addRole(MemberRole.USER);

        return member;
    }

    private MemberGradeHistory createMemberGradeHistory(Member member) {
        return MemberGradeHistory.builder()
                .createdAt(new Date())
                .grade(MemberGrade.ROYAL)
                .member(member).build();
    }

    private MemberAccount createMemberAccount(JoinRequestDto joinRequestDto) {
        return MemberAccount.builder()
                .id(joinRequestDto.getLoginId())
                .password(passwordEncoder.encode(joinRequestDto.getPassword()))
                .build();
    }

    private void validateUniqueMember(JoinRequestDto joinRequestDto) {

        if (memberRepository.existsByMemberAccount_Id(joinRequestDto.getLoginId())) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_LOGIN_ID);
        }

        if (memberRepository.existsByEmail(joinRequestDto.getEmail())) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_EMAIL);
        }

        if (memberRepository.existsByPhoneNumber(joinRequestDto.getPhoneNumber())) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_PHONE);
        }
    }
}
