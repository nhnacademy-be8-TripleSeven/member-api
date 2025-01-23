package com.example.msamemberapi.application.service.impl;

import com.example.msamemberapi.application.dto.request.AddressRequestDto;
import com.example.msamemberapi.application.dto.request.MemberAddressRequestDto;
import com.example.msamemberapi.application.dto.response.AddressResponseDto;
import com.example.msamemberapi.application.dto.response.KakaoAddressResponseDto;
import com.example.msamemberapi.application.entity.Address;
import com.example.msamemberapi.application.entity.Member;
import com.example.msamemberapi.application.entity.MemberAddress;
import com.example.msamemberapi.application.repository.AddressRepository;
import com.example.msamemberapi.application.repository.MemberAddressRepository;
import com.example.msamemberapi.application.repository.MemberRepository;
import com.example.msamemberapi.application.service.AddressService;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private static final Logger log = LoggerFactory.getLogger(AddressServiceImpl.class);

    private final AddressRepository addressRepository;
    private final WebClient webClient;
    private final MemberRepository memberRepository;
    private final MemberAddressRepository memberAddressRepository;


    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    @PostConstruct
    public void logKakaoApiKey() {
        if (kakaoApiKey != null && !kakaoApiKey.isEmpty()) {
            log.info("Kakao API Key is set.");
        } else {
            log.warn("Kakao API Key is not set.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponseDto> getAllAddressesById(Long memberId) {
        List<Address> addresses = addressRepository.findByMemberId(memberId);
        return addresses.stream()
                .map(AddressResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AddressResponseDto findAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("주소를 찾을 수 없습니다."));
        return AddressResponseDto.fromEntity(address);
    }

    @Override
    @Transactional
    public AddressResponseDto createAddress(Long memberId, AddressRequestDto requestDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Address address = Address.builder()
                .member(member)
                .alias(requestDto.getAlias())
                .roadAddress(requestDto.getRoadAddress())
                .postcode(requestDto.getPostcode())
                .detailAddress(requestDto.getDetailAddress())
                .isDefault(requestDto.getIsDefault() != null && requestDto.getIsDefault())
                .build();
        Address savedAddress = addressRepository.save(address);

        MemberAddress memberAddress = MemberAddress.builder()
                .member(member)
                .address(savedAddress)
                .alias(requestDto.getAlias())
                .isDefault(requestDto.getIsDefault() != null && requestDto.getIsDefault())
                .build();
        memberAddressRepository.save(memberAddress);

        return AddressResponseDto.fromEntity(savedAddress);
    }

    @Override
    @Transactional
    public AddressResponseDto updateAddress(Long memberId, AddressRequestDto requestDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        Address address = requestDto.getId() != null
                ? addressRepository.findById(requestDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("주소가 존재하지 않습니다."))
                : new Address(member, requestDto.getPostcode(), requestDto.getRoadAddress(),
                requestDto.getDetailAddress(), requestDto.getAlias(), requestDto.getIsDefault());

        if (Boolean.TRUE.equals(requestDto.getIsDefault())) {
            resetDefaultAddress(memberId);
        }

        address.updateDetails(requestDto.getRoadAddress(), requestDto.getDetailAddress(),
                requestDto.getAlias(), requestDto.getPostcode(), requestDto.getIsDefault());

        Address savedAddress = addressRepository.save(address);
        return AddressResponseDto.fromEntity(savedAddress);
    }

    private void resetDefaultAddress(Long memberId) {
        List<Address> addresses = addressRepository.findByMemberId(memberId);
        addresses.forEach(address -> address.updateDetails(
                address.getRoadAddress(), address.getDetailAddress(),
                address.getAlias(), address.getPostcode(), false));
        addressRepository.saveAll(addresses);
    }

    @Override
    @Transactional
    public void deleteAddress(Long id) {
        if (!addressRepository.existsById(id)) {
            throw new IllegalArgumentException("삭제할 주소가 존재하지 않습니다.");
        }
        addressRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void saveAddressFromKakao(Long memberId, MemberAddressRequestDto requestDto) {
        // 1. 사용자 검증
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));

        // 2. 카카오 주소 검색
//        List<KakaoAddressResponseDto.Document> documents = searchRoadAddress(requestDto.getPostalCode());
//        if (documents.isEmpty()) {
//            throw new IllegalStateException("카카오 API를 통해 유효한 주소를 찾을 수 없습니다.");
//        }

        // 3. 주소 데이터 생성
//        KakaoAddressResponseDto.Document document = documents.get(0);
//        String roadAddress = getRoadAddress(document);
//        String zoneCode = getZoneCode(document);

        // 4. Address 엔티티 생성
        Address address = Address.builder()
                .member(member)
                .alias(requestDto.getAlias())
                .roadAddress(requestDto.getRoadAddress())
                .postcode(requestDto.getPostalCode())
                .detailAddress(requestDto.getDetail())
                .isDefault(requestDto.getIsDefault() != null && requestDto.getIsDefault())
                .build();
        addressRepository.save(address);

        // 5. MemberAddress 엔티티 생성
        MemberAddress memberAddress = MemberAddress.builder()
                .member(member)
                .address(address)
                .alias(requestDto.getAlias())
                .isDefault(requestDto.getIsDefault() != null && requestDto.getIsDefault())
                .build();
        memberAddressRepository.save(memberAddress);
        member.getMemberAddresses().add(memberAddress);
        memberRepository.save(member);

        // 6. 기본 주소 설정 처리
        if (Boolean.TRUE.equals(requestDto.getIsDefault())) {
            resetDefaultAddress(memberId, address);
        }

        log.info("주소 저장 성공. Member ID: {}, Postcode: {}", memberId, requestDto.getPostalCode());
    }

    private void resetDefaultAddress(Long memberId, Address newDefaultAddress) {
        List<Address> addresses = addressRepository.findByMemberId(memberId);
        for (Address address : addresses) {
            boolean isDefault = address.getId().equals(newDefaultAddress.getId());
            address.updateDetails(
                    address.getRoadAddress(),
                    address.getDetailAddress(),
                    address.getAlias(),
                    address.getPostcode(),
                    isDefault
            );
        }
        addressRepository.saveAll(addresses);
    }


//    @Override
//    @Transactional
//    public void saveAddressFromKakao(Long userId, String postcode, String alias, String detailAddress) {
//
//        Member member = memberRepository.findById(userId)
//                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));
//
//        List<KakaoAddressResponseDto.Document> documents = searchRoadAddress(postcode);
//        if (documents.isEmpty()) {
//            throw new IllegalStateException("유효한 주소를 찾을 수 없습니다.");
//        }
//
//        KakaoAddressResponseDto.Document document = documents.get(0);
//        String roadAddress = document.getRoadAddress() != null
//                ? document.getRoadAddress().getAddressName()
//                : document.getAddress().getAddressName();
//        String postcode = document.getRoadAddress() != null
//                ? document.getRoadAddress().getZoneNo()
//                : document.getAddress().getZoneNo();
//
//        Address address = Address.builder()
//                .member(member)
//                .alias(alias)
//                .roadAddress(roadAddress)
//                .postcode(postcode)
//                .detailAddress(detailAddress)
//                .isDefault(false)
//                .build();
//        addressRepository.save(address);
//
//        MemberAddress memberAddress = MemberAddress.builder()
//                .member(member)
//                .address(address)
//                .alias(alias)
//                .isDefault(false)
//                .build();
//        memberAddressRepository.save(memberAddress);
//
//        member.getAddresses().add(address);
//        member.getMemberAddresses().add(memberAddress);
//        memberRepository.save(member);
//
//    }

    @Override
    public List<KakaoAddressResponseDto.Document> searchRoadAddress(String keyword) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/local/search/address.json")
                        .queryParam("query", keyword)
                        .build())
                .retrieve()
                .bodyToMono(KakaoAddressResponseDto.class)
                .map(KakaoAddressResponseDto::getDocuments)
                .block();
    }

    private String getRoadAddress(KakaoAddressResponseDto.Document document) {
        if (document.getRoadAddress() != null) {
            return document.getRoadAddress().getAddressName(); // 도로명 주소 반환
        } else if (document.getAddress() != null) {
            return document.getAddress().getAddressName(); // 지번 주소 반환
        } else {
            throw new IllegalStateException("주소 정보를 찾을 수 없습니다.");
        }
    }

    private String getZoneCode(KakaoAddressResponseDto.Document document) {
        if (document.getRoadAddress() != null) {
            return document.getRoadAddress().getZoneNo(); // 도로명 주소의 우편번호 반환
        } else if (document.getAddress() != null) {
            return document.getAddress().getZoneNo(); // 지번 주소의 우편번호 반환
        } else {
            throw new IllegalStateException("우편번호를 찾을 수 없습니다.");
        }
    }

    
}