package com.example.msamemberapi.application.service.impl;

import com.example.msamemberapi.application.dto.request.Oauth2MemberRequestDto;
import com.example.msamemberapi.application.dto.response.MemberAccountInfo;
import com.example.msamemberapi.application.entity.Member;
import com.example.msamemberapi.application.error.CustomException;
import com.example.msamemberapi.application.error.ErrorCode;
import com.example.msamemberapi.application.repository.MemberRepository;
import com.example.msamemberapi.application.service.Oauth2MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class PaycoMemberService implements Oauth2MemberService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public MemberAccountInfo saveMemberDetail(Oauth2MemberRequestDto memberRequestDto) {

        Optional<Member> optionalMember = memberRepository.findByMemberAccount_Id(memberRequestDto.getIdNo());
        if (!optionalMember.isPresent()) {
            new MemberAccountInfo(optionalMember.get().getMemberAccount());
        }

        Member member = memberRepository.save(memberRequestDto.toPaycoMemberEntity());
        return new MemberAccountInfo(member.getMemberAccount());
    }

}
