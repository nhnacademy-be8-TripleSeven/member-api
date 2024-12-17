package com.example.msamemberapi.application.service.impl;

import com.example.msamemberapi.application.dto.request.JoinRequestDto;
import com.example.msamemberapi.application.dto.response.MemberAuthInfo;
import com.example.msamemberapi.application.dto.response.MemberDto;
import com.example.msamemberapi.application.entity.Member;
import com.example.msamemberapi.application.entity.MemberAccount;
import com.example.msamemberapi.application.entity.MemberGradeHistory;
import com.example.msamemberapi.application.enums.MemberGrade;
import com.example.msamemberapi.application.enums.MemberRole;
import com.example.msamemberapi.application.repository.MemberRepository;
import com.example.msamemberapi.application.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Member join(JoinRequestDto joinRequestDto) {

        Optional<Member> optionalMember = memberRepository.findByMemberAccount_Id(joinRequestDto.getLoginId());
        if (optionalMember.isPresent()) {
            throw new IllegalArgumentException();
        }

        MemberAccount memberAccount = createMemberAccount(joinRequestDto);
        Member member = createMember(joinRequestDto, memberAccount);
        member.addRole(MemberRole.USER);
        return memberRepository.save(member);
    }



    @Override
    @Transactional(readOnly = true)
    public MemberAuthInfo findByMemberId(String loginId) {
        Member member = memberRepository.findByMemberAccount_Id(loginId).orElseThrow(() -> new IllegalArgumentException());
        return new MemberAuthInfo(member);
    }

    @Override
    @Transactional
    public void deleteByMemberId(Long memberId) {
        memberRepository.deleteById(memberId);
    }

    private Member createMember(JoinRequestDto joinRequestDto, MemberAccount memberAccount) {
        Member member = Member.builder()
                .memberAccount(memberAccount)
                .birth(joinRequestDto.getBirth())
                .gender(joinRequestDto.getGender())
                .email(joinRequestDto.getEmail())
                .phoneNumber(joinRequestDto.getPhoneNumber())
                .name(joinRequestDto.getName())
                .build();


        MemberGradeHistory gradeHistory = MemberGradeHistory.builder()
                .createdAt(new Date())
                .grade(MemberGrade.ROYAL)
                .member(member).build();

        member.addGradeHistory(gradeHistory);

        return member;
    }

    private MemberAccount createMemberAccount(JoinRequestDto joinRequestDto) {
        return MemberAccount.builder()
                .id(joinRequestDto.getLoginId())
                .password(passwordEncoder.encode(joinRequestDto.getPassword()))
                .build();
    }
}
