package com.example.msamemberapi.application.service;

import com.example.msamemberapi.application.dto.request.JoinRequestDto;
import com.example.msamemberapi.application.dto.request.UpdatePasswordRequestDto;
import com.example.msamemberapi.application.dto.response.MemberAccountInfo;
import com.example.msamemberapi.application.dto.response.MemberAuthInfo;
import com.example.msamemberapi.application.dto.response.MemberDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;

public interface MemberService {

    MemberDto join(JoinRequestDto joinRequestDto);
    MemberAuthInfo findByMemberId(String memberId);
    void deleteByMemberId(Long memberId);
    MemberAccountInfo getMemberAccountByPhoneNumber(String phoneNumber);
    MemberAccountInfo getMemberAccountByEmail(String email);
    void validateMatchingLoginIdAndEmail(String email, String loginId);
    void updateMemberPassword(UpdatePasswordRequestDto updatePasswordRequestDto);

    Page<MemberDto> getMembers(String name, Pageable pageable);

}
