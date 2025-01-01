package com.example.msamemberapi.application.service.impl;

import com.example.msamemberapi.application.dto.request.AddressRequestDto;
import com.example.msamemberapi.application.dto.response.AddressResponseDto;
import com.example.msamemberapi.application.dto.response.KakaoAddressResponseDto;
import com.example.msamemberapi.application.entity.Address;
import com.example.msamemberapi.application.repository.AddressRepository;
import com.example.msamemberapi.application.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final RestTemplate restTemplate;

    private static final String KAKAO_API_URL = "https://dapi.kakao.com/v2/local/search/address.json";

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponseDto> getAllAddresses() {
        return addressRepository.findAll().stream()
                .map(address -> AddressResponseDto.builder()
                        .id(address.getId())
                        .postcode(address.getPostcode())
                        .roadAddress(address.getRoadAddress())
                        .detailAddress(address.getDetailAddress())
                        .alias(address.getAlias())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AddressResponseDto createAddress(AddressRequestDto requestDto) {
        Address savedAddress = addressRepository.save(Address.builder()
                .postcode(requestDto.getPostcode())
                .roadAddress(requestDto.getRoadAddress())
                .detailAddress(requestDto.getDetailAddress())
                .alias(requestDto.getAlias())
                .build());

        return AddressResponseDto.builder()
                .id(savedAddress.getId())
                .postcode(savedAddress.getPostcode())
                .roadAddress(savedAddress.getRoadAddress())
                .detailAddress(savedAddress.getDetailAddress())
                .alias(savedAddress.getAlias())
                .build();
    }

    @Override
    @Transactional
    public AddressResponseDto updateAddress(Long id, AddressRequestDto requestDto) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 주소를 찾을 수 없습니다."));

        address.updateDetails(requestDto.getRoadAddress(), requestDto.getDetailAddress(), requestDto.getAlias());
        return AddressResponseDto.builder()
                .id(address.getId())
                .postcode(address.getPostcode())
                .roadAddress(address.getRoadAddress())
                .detailAddress(address.getDetailAddress())
                .alias(address.getAlias())
                .build();
    }

    @Override
    @Transactional
    public void deleteAddress(Long id) {
        if (!addressRepository.existsById(id)) {
            throw new IllegalArgumentException("해당 주소를 찾을 수 없습니다.");
        }
        addressRepository.deleteById(id);
    }

    @Override
    public List<KakaoAddressResponseDto.Document> searchRoadAddress(String keyword) {
        String apiUrl = KAKAO_API_URL + "?query=" + keyword;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<KakaoAddressResponseDto> response;
        try {
            response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    entity,
                    KakaoAddressResponseDto.class
            );
        } catch (Exception e) {
            throw new IllegalStateException("카카오 API 호출 중 문제가 발생했습니다: " + e.getMessage(), e);
        }

        if (response.getBody() != null && response.getBody().getDocuments() != null) {
            return response.getBody().getDocuments();
        } else {
            throw new IllegalStateException("카카오 API 응답이 유효하지 않습니다.");
        }
    }
}