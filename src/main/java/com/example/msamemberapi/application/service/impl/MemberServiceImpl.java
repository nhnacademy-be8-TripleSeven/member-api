package com.example.msamemberapi.application.service.impl;

import com.example.msamemberapi.application.dto.request.JoinRequestDto;
import com.example.msamemberapi.application.dto.request.UpdatePasswordRequestDto;
import com.example.msamemberapi.application.dto.response.MemberAccountInfo;
import com.example.msamemberapi.application.dto.response.MemberAuthInfo;
import com.example.msamemberapi.application.dto.response.MemberDto;
import com.example.msamemberapi.application.entity.Member;
import com.example.msamemberapi.application.entity.MemberAccount;
import com.example.msamemberapi.application.entity.MemberGradeHistory;
import com.example.msamemberapi.application.entity.User;
import com.example.msamemberapi.application.enums.AccountType;
import com.example.msamemberapi.application.enums.MemberGrade;
import com.example.msamemberapi.application.enums.MemberRole;
import com.example.msamemberapi.application.error.CustomException;
import com.example.msamemberapi.application.error.ErrorCode;
import com.example.msamemberapi.application.repository.MemberRepository;
import com.example.msamemberapi.application.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

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
        User user = createUser(joinRequestDto);
        Member member = createMember(joinRequestDto, memberAccount, user);
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

        if (!member.getMemberAccount().getAccountType().equals(AccountType.REGISTERED)) {
            throw new CustomException(ErrorCode.EMAIL_NOT_FOUND);
        }

        return new MemberAccountInfo(member.getMemberAccount());
    }

    @Override
    @Transactional(readOnly = true)
    public MemberAccountInfo getMemberAccountByPhoneNumber(String phoneNumber) {
        Member member = memberRepository.findByUserPhoneNumber(phoneNumber)
                .orElseThrow(() -> new CustomException(ErrorCode.PHONE_NOT_FOUND));

        if (!member.getMemberAccount().getAccountType().equals(AccountType.REGISTERED)) {
            throw new CustomException(ErrorCode.PHONE_NOT_FOUND);
        }

        return new MemberAccountInfo(member.getMemberAccount());
    }

    @Override
    @Transactional(readOnly = true)
    public void validateMatchingLoginIdAndEmail(String email, String loginId) {
        if (!getMemberAccountByEmail(email).getLoginId().equals(loginId)) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
    }

    @Override
    @Transactional
    public void updateMemberPassword(UpdatePasswordRequestDto updatePasswordRequestDto) {
        Member member = memberRepository.findByEmail(updatePasswordRequestDto.getEmail()).orElseThrow(() -> new CustomException(ErrorCode.EMAIL_NOT_FOUND));

        if (!member.getMemberAccount().getAccountType().equals(AccountType.REGISTERED)) {
            throw new CustomException(ErrorCode.EMAIL_NOT_FOUND);
        }

        MemberAccount memberAccount = member.getMemberAccount();
        memberAccount.changePassword(passwordEncoder.encode(updatePasswordRequestDto.getNewPassword()));
    }

    @Override
    public Collection<MemberDto> getMembers(String name, Pageable pageable) {
        return memberRepository.findMembers(name, pageable);
    }

    private Member createMember(JoinRequestDto joinRequestDto, MemberAccount memberAccount, User user) {
        Member member = Member.builder()
                .memberAccount(memberAccount)
                .user(user)
                .birth(joinRequestDto.getBirth())
                .gender(joinRequestDto.getGender())
                .email(joinRequestDto.getEmail())
                .gradeHistories(new ArrayList<>())
                .build();

        MemberGradeHistory gradeHistory = createMemberGradeHistory(member);
        member.addGradeHistory(gradeHistory);
        member.addRole(MemberRole.USER);

        return member;
    }

    private User createUser(JoinRequestDto joinRequestDto) {
        return User.builder()
                .name(joinRequestDto.getName())
                .phoneNumber(joinRequestDto.getPhoneNumber())
                .build();
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
                .accountType(AccountType.REGISTERED)
                .build();
    }

    private void validateUniqueMember(JoinRequestDto joinRequestDto) {

        if (memberRepository.existsByMemberAccount_Id(joinRequestDto.getLoginId())) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_LOGIN_ID);
        }

        if (memberRepository.existsByEmail(joinRequestDto.getEmail())) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_EMAIL);
        }

        if (memberRepository.existsByUserPhoneNumber(joinRequestDto.getPhoneNumber())) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_PHONE);
        }
    }
}
