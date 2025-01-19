package com.example.msamemberapi.application.service.impl;

import com.example.msamemberapi.application.dto.response.KakaoAddressResponseDto;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import org.springframework.beans.factory.annotation.Value;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class KakaoTest {

    @Value("${kakao.api.key:default_value}")
    private String kakaoApiKey;

    private KakaoAddressServiceImpl kakaoAddressService;

    @MockBean
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        // KakaoAddressServiceImpl 초기화, @MockBean은 자동으로 WebClient를 주입합니다.
        kakaoAddressService = new KakaoAddressServiceImpl(webClient);
    }

    @Test
    void testSearchAddress_Success() {
        // Arrange
        String query = "서울시 강남구 삼성동";

        // Mock Document 객체 생성
        KakaoAddressResponseDto.Document document = KakaoAddressResponseDto.Document.builder()
                .address(KakaoAddressResponseDto.Address.builder()
                        .addressName("서울시 강남구 삼성동")
                        .zoneNo("12345")
                        .build())
                .build();

        KakaoAddressResponseDto responseDto = KakaoAddressResponseDto.builder()
                .documents(List.of(document))
                .build();

        // WebClient의 behavior를 mock
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(KakaoAddressResponseDto.class)).thenReturn(Mono.just(responseDto));

        // Act
        KakaoAddressResponseDto result = kakaoAddressService.searchAddress(query);

        // Assert
        assertNotNull(result);
        assertEquals("서울시 강남구 삼성동", result.getDocuments().get(0).getAddress().getAddressName());
    }

    @Test
    void testSearchAddress_Failure() {
        // Arrange
        String query = "InvalidQuery";

        // 카카오 API의 응답이 401 Unauthorized인 경우를 mock
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(KakaoAddressResponseDto.class))
                .thenReturn(Mono.error(new WebClientResponseException(
                        "401 Unauthorized", 401, "Unauthorized",
                        HttpHeaders.EMPTY, new byte[0], null
                )));

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            kakaoAddressService.searchAddress(query);
        });

        assertEquals("카카오 주소 검색 API 호출 실패: 401 Unauthorized", thrown.getMessage());
    }
}