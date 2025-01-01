package com.example.msamemberapi.application.service;

import com.example.msamemberapi.application.dto.request.JoinRequestDto;
import com.example.msamemberapi.application.dto.request.MemberUpdateRequestDto;
import com.example.msamemberapi.application.dto.response.MemberAccountInfo;
import com.example.msamemberapi.application.dto.response.MemberAuthInfo;
import com.example.msamemberapi.application.dto.response.MemberDto;


public interface MemberService {

    MemberDto join(JoinRequestDto joinRequestDto);
    MemberAuthInfo findByMemberId(String memberId);
    void deleteByMemberId(Long memberId);
    MemberAccountInfo getMemberAccountByPhoneNumber(String phoneNumber);
    MemberAccountInfo getMemberAccountByEmail(String email);

    boolean verifyPassword(String userId, String password);
    void updateMember(MemberUpdateRequestDto requestDto);
    MemberDto getMemberInfo(Long userId);


}
