package com.example.msamemberapi.application.service.impl;

import com.example.msamemberapi.application.dto.request.JoinRequestDto;
import com.example.msamemberapi.application.dto.response.MemberAuthInfo;
import com.example.msamemberapi.application.dto.response.MemberDto;
import com.example.msamemberapi.application.entity.Member;
import com.example.msamemberapi.application.enums.MemberRole;
import com.example.msamemberapi.application.repository.MemberRepository;
import com.example.msamemberapi.application.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    @Transactional
    public Member join(JoinRequestDto joinRequestDto) {

        Optional<Member> optionalMember = memberRepository.findById(joinRequestDto.getId());
        if (optionalMember.isPresent()) {
            throw new IllegalArgumentException();
        }


        Member member = Member.builder()
                .id(joinRequestDto.getId())
                .password(passwordEncoder.encode(joinRequestDto.getPassword()))
                .build();

        member.addRole(MemberRole.USER.toString());
        return memberRepository.save(member);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberAuthInfo findByMemberId(String memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException());
        return new MemberAuthInfo(member);
    }

    @Override
    @Transactional
    public void deleteByMemberId(String memberId) {
        memberRepository.deleteById(memberId);
    }
}
