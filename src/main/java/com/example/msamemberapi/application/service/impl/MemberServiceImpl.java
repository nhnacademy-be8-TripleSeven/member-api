package com.example.msamemberapi.application.service.impl;

import com.example.msamemberapi.application.entity.Member;
import com.example.msamemberapi.application.repository.MemberRepository;
import com.example.msamemberapi.application.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public Member join(Member member) {

        Optional<Member> optionalMember = memberRepository.findById(member.getId());
        if (optionalMember.isPresent())
            throw new IllegalArgumentException();


        String encodedPassword = passwordEncoder.encode(member.getPassword());
        member.setPassword(encodedPassword);
        return memberRepository.save(member);
    }

    @Override
    public Member findByMemberId(String memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException());
    }

    @Override
    public void deleteByMemberId(String memberId) {
        memberRepository.deleteById(memberId);
    }
}
