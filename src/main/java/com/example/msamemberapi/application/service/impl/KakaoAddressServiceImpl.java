package com.example.msamemberapi.application.service.impl;

import com.example.msamemberapi.application.dto.response.KakaoAddressResponseDto;
import com.example.msamemberapi.application.service.KakaoAddressService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;


@Service
@RequiredArgsConstructor
public class KakaoAddressServiceImpl implements KakaoAddressService {

    private final WebClient webClient;

    @Value("${kakao.api.key:default_value}")
    private String kakaoApiKey;

    @PostConstruct
    public void logKakaoApiKey() {
        System.out.println("Loaded Kakao API Key: " + kakaoApiKey);
    }

    @Override
    public KakaoAddressResponseDto searchAddress(String query) {
        WebClient localWebClient = WebClient.builder()
                .baseUrl("https://dapi.kakao.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoApiKey)
                .build();

        try {
            return localWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v2/local/search/address.json")
                            .queryParam("query", query)
                            .build())
                    .retrieve()
                    .bodyToMono(KakaoAddressResponseDto.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("카카오 주소 검색 API 호출 실패: " + e.getMessage(), e);
        }
    }
}