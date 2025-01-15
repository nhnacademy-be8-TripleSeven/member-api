package com.example.msamemberapi.application.service.impl;

import com.example.msamemberapi.application.dto.request.AddressRequestDto;
import com.example.msamemberapi.application.dto.response.AddressResponseDto;
import com.example.msamemberapi.application.dto.response.KakaoAddressResponseDto;
import com.example.msamemberapi.application.entity.Address;
import com.example.msamemberapi.application.entity.Member;
import com.example.msamemberapi.application.repository.AddressRepository;
import com.example.msamemberapi.application.repository.MemberRepository;
import com.example.msamemberapi.application.service.AddressService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private static final Logger log = LoggerFactory.getLogger(AddressServiceImpl.class);

    private final AddressRepository addressRepository;
    private final WebClient webClient;
    private final MemberRepository memberRepository;


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
    public List<AddressResponseDto> getAllAddressesByUserId(Long userId) {
        List<Address> addresses = addressRepository.findByMemberId(userId);
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
    public AddressResponseDto createAddress(Long userId, AddressRequestDto requestDto) {
        if (requestDto == null ||
                requestDto.getPostcode() == null ||
                requestDto.getRoadAddress() == null ||
                requestDto.getAlias() == null) {
            throw new IllegalArgumentException("필수 값이 누락되었습니다.");
        }

        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Address address = Address.builder()
                .member(member)
                .alias(requestDto.getAlias())
                .roadAddress(requestDto.getRoadAddress())
                .postcode(requestDto.getPostcode())
                .detailAddress(requestDto.getDetailAddress())
                .isDefault(requestDto.getIsDefault() != null ? requestDto.getIsDefault() : false)
                .build();

        Address savedAddress = addressRepository.save(address);
        return AddressResponseDto.fromEntity(savedAddress);
    }

    @Override
    @Transactional
    public AddressResponseDto updateAddress(Long id, AddressRequestDto requestDto) {
        Address existingAddress = addressRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("주소를 찾을 수 없습니다."));

        existingAddress.updateDetails(
                requestDto.getRoadAddress() != null ? requestDto.getRoadAddress() : existingAddress.getRoadAddress(),
                requestDto.getDetailAddress() != null ? requestDto.getDetailAddress() : existingAddress.getDetailAddress(),
                requestDto.getAlias() != null ? requestDto.getAlias() : existingAddress.getAlias(),
                requestDto.getPostcode() != null ? requestDto.getPostcode() : existingAddress.getPostcode(),
                requestDto.getIsDefault() != null ? requestDto.getIsDefault() : existingAddress.getIsDefault()
        );

        Address updatedAddress = addressRepository.save(existingAddress);
        return AddressResponseDto.fromEntity(updatedAddress);
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
    public void saveAddressFromKakao(String userId, String query, String alias, String detailAddress) {
        log.info("카카오 주소 저장 로직 시작. userId: {}, query: {}", userId, query);

        Long userIdLong = Long.parseLong(userId);
        Member member = memberRepository.findById(userIdLong)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<KakaoAddressResponseDto.Document> documents = searchRoadAddress(query);
        if (documents.isEmpty()) {
            throw new IllegalStateException("유효한 주소를 찾을 수 없습니다.");
        }

        KakaoAddressResponseDto.Document document = documents.get(0);
        String roadAddress = document.getRoadAddress() != null
                ? document.getRoadAddress().getAddressName()
                : document.getAddress().getAddressName();
        String postcode = document.getRoadAddress() != null
                ? document.getRoadAddress().getZoneNo()
                : document.getAddress().getZoneNo();

        Address address = Address.builder()
                .member(member)
                .alias(alias)
                .roadAddress(roadAddress)
                .postcode(postcode)
                .detailAddress(detailAddress)
                .isDefault(false)
                .build();

        addressRepository.save(address);
        log.info("주소 저장 성공. userId: {}, query: {}", userId, query);
    }

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


    @Override
    public boolean isAddressOwnedByUser(Long addressId, Long userId) {
        return addressRepository.existsByIdAndMemberId(addressId, userId);
    }

    private void validateAddressRequest(AddressRequestDto requestDto) {
        if (requestDto == null ||
                requestDto.getPostcode() == null ||
                requestDto.getRoadAddress() == null ||
                requestDto.getAlias() == null) {
            throw new IllegalArgumentException("필수 값이 누락되었습니다.");
        }
    }

    private Address buildAddress(AddressRequestDto requestDto, Member member, boolean isDefault) {
        return Address.builder()
                .member(member)
                .roadAddress(requestDto.getRoadAddress())
                .postcode(requestDto.getPostcode())
                .detailAddress(requestDto.getDetailAddress())
                .alias(requestDto.getAlias())
                .isDefault(isDefault)
                .build();
    }


    @Override
    public AddressResponseDto addOrUpdateAddress(Long userId, Address updatedData) {
        if (updatedData.getId() != null) {
            Address existingAddress = addressRepository.findById(updatedData.getId())
                    .orElseThrow(() -> new RuntimeException("Address not found"));

            existingAddress.updateDetails(
                    updatedData.getRoadAddress(),
                    updatedData.getDetailAddress(),
                    updatedData.getAlias(),
                    updatedData.getPostcode(),
                    updatedData.getIsDefault()
            );

            Address savedAddress = addressRepository.save(existingAddress);
            return AddressResponseDto.fromEntity(savedAddress);
        } else {
            // 추가 로직
            Member member = memberRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Address newAddress = Address.builder()
                    .member(member) // Member 객체 직접 참조
                    .roadAddress(updatedData.getRoadAddress())
                    .detailAddress(updatedData.getDetailAddress())
                    .alias(updatedData.getAlias())
                    .postcode(updatedData.getPostcode())
                    .isDefault(updatedData.getIsDefault())
                    .build();

            Address savedAddress = addressRepository.save(newAddress);
            return AddressResponseDto.fromEntity(savedAddress);
        }
    }
}