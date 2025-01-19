package com.example.msamemberapi.application.service.impl;

import com.example.msamemberapi.application.dto.request.MemberAddressRequestDto;
import com.example.msamemberapi.application.dto.response.MemberAddressResponseDto;
import com.example.msamemberapi.application.entity.Address;
import com.example.msamemberapi.application.entity.Member;
import com.example.msamemberapi.application.entity.MemberAddress;
import com.example.msamemberapi.application.repository.AddressRepository;
import com.example.msamemberapi.application.repository.MemberAddressRepository;
import com.example.msamemberapi.application.repository.MemberRepository;
import com.example.msamemberapi.application.service.MemberAddressService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberAddressServiceImpl implements MemberAddressService {

    private final MemberRepository memberRepository;
    private final MemberAddressRepository memberAddressRepository;
    private final AddressRepository addressRepository;
    private static final Logger logger = LoggerFactory.getLogger(MemberAddressServiceImpl.class);

    @Override
    @Transactional(readOnly = true)
    public List<MemberAddressResponseDto> getAddressesByMemberId(Long memberId) {
        logger.debug("멤버 ID로 주소 조회 요청: {}", memberId);

        List<MemberAddress> memberAddresses = memberAddressRepository.findByMemberId(memberId);

        return memberAddresses.stream()
                .map(memberAddress -> new MemberAddressResponseDto(
                        memberAddress.getId(),
                        memberAddress.getAlias(),
                        memberAddress.getAddress().getDetailAddress(),
                        memberAddress.getAddress().getRoadAddress(),
                        memberAddress.getAddress().getPostcode(),
                        memberAddress.getIsDefault()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeAddressFromMember(Long memberId, Long addressId) {
        logger.debug("주소 삭제 요청 - 멤버 ID: {}, 주소 ID: {}", memberId, addressId);

        MemberAddress memberAddress = memberAddressRepository.findByMemberIdAndAddressId(memberId, addressId)
                .orElseThrow(() -> new IllegalArgumentException("주소를 찾을 수 없습니다."));

        memberAddressRepository.delete(memberAddress);
        logger.debug("멤버 ID {}의 주소 ID {}가 삭제되었습니다.", memberId, addressId);
    }

    @Override
    @Transactional
    public MemberAddressResponseDto addAddressToMember(Long memberId, MemberAddressRequestDto requestDto) {
        // 1. 회원 정보 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        // 2. 기본 주소 처리
        if (Boolean.TRUE.equals(requestDto.getIsDefault())) {
            resetDefaultAddress(memberId);
        }

        // 3. 주소 객체 생성 및 저장
        Address address = Address.builder()
                .member(member)
                .postcode(requestDto.getPostalCode())
                .roadAddress(requestDto.getRoadAddress())
                .detailAddress(requestDto.getDetail())
                .alias(requestDto.getAlias())
                .isDefault(requestDto.getIsDefault())
                .build();

        addressRepository.save(address);  // Address 저장

        // 4. MemberAddress 객체 생성 및 저장
        MemberAddress memberAddress = MemberAddress.builder()
                .member(member)
                .address(address)  // address_id가 연결되어야 함
                .alias(requestDto.getAlias())
                .isDefault(requestDto.getIsDefault())
                .build();

        memberAddressRepository.save(memberAddress);  // MemberAddress 저장

        // 로그 추가
        logger.debug("Address 저장 성공. Address ID: {}", address.getId());  // Address ID 확인
        logger.debug("MemberAddress 저장 성공. MemberAddress ID: {}", memberAddress.getId());  // MemberAddress ID 확인

        // 5. 응답 DTO 반환
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
    public void updateAlias(Long memberAddressId, String alias, Boolean isDefault) {
        MemberAddress memberAddress = memberAddressRepository.findById(memberAddressId)
                .orElseThrow(() -> new IllegalArgumentException("주소를 찾을 수 없습니다."));

        memberAddressRepository.save(memberAddress.createUpdatedCopy(alias, isDefault));

        if (Boolean.TRUE.equals(isDefault)) {
            resetDefaultAddress(memberAddress.getMember().getId(), memberAddressId);
        }
    }

    private void resetDefaultAddress(Long memberId) {
        List<MemberAddress> existingAddresses = memberAddressRepository.findByMemberId(memberId);
        existingAddresses.forEach(existing -> memberAddressRepository.save(existing.createUpdatedCopy(existing.getAlias(), false)));
    }

    private void resetDefaultAddress(Long memberId, Long excludedAddressId) {
        List<MemberAddress> existingAddresses = memberAddressRepository.findByMemberId(memberId);
        existingAddresses.stream()
                .filter(existing -> !existing.getId().equals(excludedAddressId))
                .forEach(existing -> memberAddressRepository.save(existing.createUpdatedCopy(existing.getAlias(), false)));
    }
}