package com.example.msamemberapi.application.service;

import com.example.msamemberapi.application.dto.request.MemberAddressRequestDto;
import com.example.msamemberapi.application.dto.response.MemberAddressResponseDto;

import java.util.List;

public interface MemberAddressService {
    List<MemberAddressResponseDto> getAddressesByMemberId(Long memberId);
    MemberAddressResponseDto addAddressToMember(Long memberId, MemberAddressRequestDto requestDto);
    void removeAddressFromMember(Long memberId, Long addressId);
    void updateAlias(Long memberAddressId, String alias, Boolean isDefault);
}