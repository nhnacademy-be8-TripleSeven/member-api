package com.example.msamemberapi.application.service.impl;

import com.example.msamemberapi.application.dto.request.MemberAddressRequestDto;
import com.example.msamemberapi.application.dto.response.MemberAddressResponseDto;
import com.example.msamemberapi.application.entity.Member;
import com.example.msamemberapi.application.entity.MemberAddress;
import com.example.msamemberapi.application.repository.MemberAddressRepository;
import com.example.msamemberapi.application.repository.MemberRepository;
import com.example.msamemberapi.application.service.MemberAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberAddressServiceImpl implements MemberAddressService {

    private final MemberRepository memberRepository;
    private final MemberAddressRepository memberAddressRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MemberAddressResponseDto> getAddressesByMemberId(Long memberId) {
        List<MemberAddress> memberAddresses = memberAddressRepository.findByMemberId(memberId);

        return memberAddresses.stream()
                .map(memberAddress -> new MemberAddressResponseDto(
                        memberAddress.getId(),
                        memberAddress.getAlias(),
                        "상세 주소 정보", // 값-->API
                        "도로명 주소 정보", // 값-->API
                        "우편번호",        // 값-->API
                        memberAddress.getIsDefault()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MemberAddressResponseDto addAddressToMember(Long memberId, MemberAddressRequestDto requestDto) {
        // 회원 존재 여부 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        // 주소 등록 제한 확인
        if (memberAddressRepository.findByMemberId(memberId).size() >= 10) {
            throw new IllegalStateException("주소는 최대 10개까지만 등록할 수 있습니다.");
        }

        if (requestDto.getIsDefault()) {
            List<MemberAddress> existingAddresses = memberAddressRepository.findByMemberId(memberId);
            existingAddresses.forEach(address -> address.setDefault(false));
            memberAddressRepository.saveAll(existingAddresses);
        }

        MemberAddress memberAddress = MemberAddress.builder()
                .member(member)
                .addressId(null)
                .alias(requestDto.getAlias())
                .isDefault(requestDto.getIsDefault())
                .build();

        memberAddressRepository.save(memberAddress);

        return new MemberAddressResponseDto(
                memberAddress.getId(),
                requestDto.getAlias(),
                requestDto.getDetail(),
                requestDto.getRoadAddress(),
                requestDto.getPostalCode(),
                requestDto.getIsDefault()
        );
    }

    @Override
    @Transactional
    public void removeAddressFromMember(Long memberId, Long addressId) {
        MemberAddress memberAddress = memberAddressRepository.findByMemberIdAndAddressId(memberId, addressId)
                .orElseThrow(() -> new IllegalArgumentException("주소를 찾을 수 없습니다."));

        memberAddressRepository.delete(memberAddress);
    }

    @Override
    @Transactional
    public void updateAlias(Long memberAddressId, String alias, Boolean isDefault) {
        MemberAddress memberAddress = memberAddressRepository.findById(memberAddressId)
                .orElseThrow(() -> new IllegalArgumentException("주소를 찾을 수 없습니다."));

        memberAddress.updateAlias(alias);

        if (Boolean.TRUE.equals(isDefault)) {
            List<MemberAddress> addresses = memberAddressRepository.findByMemberId(memberAddress.getMember().getId());
            addresses.forEach(address -> address.setDefault(false));
        }

        memberAddress.setDefault(isDefault);
        memberAddressRepository.save(memberAddress);
    }
}