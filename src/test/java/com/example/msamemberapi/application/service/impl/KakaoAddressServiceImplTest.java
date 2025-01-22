package com.example.msamemberapi.application.service.impl;

import com.example.msamemberapi.application.dto.response.KakaoAddressResponseDto;
import com.example.msamemberapi.application.service.AddressService;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KakaoAddressServiceImplTest {

    private KakaoAddressServiceImpl kakaoAddressService;

    @Mock
    private WebClient webClient;

    @Mock
    private AddressService addressService;

    @Mock
    private WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        kakaoAddressService = new KakaoAddressServiceImpl(WebClient.builder().build());
    }

    @Test
    @DisplayName("카카오 API Key 로깅 테스트")
    void logKakaoApiKey_success() {
        // Act & Assert
        assertDoesNotThrow(() -> kakaoAddressService.logKakaoApiKey());
    }
}

//    @Test
//    @DisplayName("Kakao address search successful")
//    void searchRoadAddress_success() {
//        // Arrange
//        String keyword = "Some Address";
//
//        // Mock response
//        KakaoAddressResponseDto responseDto = KakaoAddressResponseDto.builder()
//                .documents(List.of(
//                        KakaoAddressResponseDto.Document.builder()
//                                .roadAddress(KakaoAddressResponseDto.RoadAddress.builder()
//                                        .addressName("123 Road")
//                                        .zoneNo("54321")
//                                        .build())
//                                .build()
//                ))
//                .build();
//
//        WebClient.RequestHeadersUriSpec<?> request = mock(WebClient.RequestHeadersUriSpec.class);
//        WebClient.RequestHeadersSpec<?> headersSpec = mock(WebClient.RequestHeadersSpec.class);
//        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
//
//        when(webClient.get()).thenReturn(request);
//        when(request.uri(any(Function.class))).thenReturn(headersSpec);
//        when(headersSpec.retrieve()).thenReturn(responseSpec);
//        when(responseSpec.bodyToMono(KakaoAddressResponseDto.class)).thenReturn(Mono.just(responseDto));
//
//        // Act
//        List<KakaoAddressResponseDto.Document> result = addressService.searchRoadAddress(keyword);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        assertEquals("123 Road", result.get(0).getRoadAddress().getAddressName());
//        assertEquals("54321", result.get(0).getRoadAddress().getZoneNo());
//        verify(webClient, times(1)).get();
//    }
//}