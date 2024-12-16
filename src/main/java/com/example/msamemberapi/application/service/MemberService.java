package com.example.msamemberapi.application.service;

import com.example.msamemberapi.application.dto.request.JoinRequestDto;
import com.example.msamemberapi.application.dto.response.MemberAuthInfo;
import com.example.msamemberapi.application.dto.response.MemberDto;
import com.example.msamemberapi.application.entity.Member;

public interface MemberService {

    Member join(JoinRequestDto joinRequestDto);
    MemberAuthInfo findByMemberId(String memberId);
    void deleteByMemberId(Long memberId);

}
