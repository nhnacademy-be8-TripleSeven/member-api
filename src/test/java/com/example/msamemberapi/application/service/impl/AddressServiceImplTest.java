package com.example.msamemberapi.application.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import jakarta.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

class AddressServiceImplTest {

    @InjectMocks
    private AddressServiceImpl addressService;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberAddressRepository memberAddressRepository;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Value("${kakao.api.key}")
    private String kakaoApiKey;



    @Test
    @DisplayName("모든 주소 조회 테스트")
    void getAllAddressesById_success() {
        // Arrange
        Long memberId = 1L;
        Address address = mock(Address.class);
        when(addressRepository.findByMemberId(memberId)).thenReturn(List.of(address));
        when(address.getAlias()).thenReturn("Home");

        // Act
        List<AddressResponseDto> addresses = addressService.getAllAddressesById(memberId);

        // Assert
        assertNotNull(addresses);
        assertEquals(1, addresses.size());
        verify(addressRepository, times(1)).findByMemberId(memberId);
    }

    @Test
    @DisplayName("주소 ID로 조회 성공")
    void findAddressById_success() {
        // Arrange
        Long addressId = 1L;
        Address address = mock(Address.class);
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(address));

        // Act
        AddressResponseDto result = addressService.findAddressById(addressId);

        // Assert
        assertNotNull(result);
        verify(addressRepository, times(1)).findById(addressId);
    }

    @Test
    @DisplayName("주소 ID로 조회 실패 - 주소 없음")
    void findAddressById_notFound() {
        // Arrange
        Long addressId = 1L;
        when(addressRepository.findById(addressId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> addressService.findAddressById(addressId));
    }

    @Test
    @DisplayName("새 주소 생성 성공")
    void createAddress_success() {
        // Arrange
        Long memberId = 1L;
        Member member = mock(Member.class);
        Address address = mock(Address.class);
        AddressRequestDto requestDto = AddressRequestDto.builder()
                .alias("Office")
                .roadAddress("456 Office Blvd")
                .detailAddress("Suite 200")
                .postcode("54321")
                .isDefault(true)
                .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        // Act
        AddressResponseDto result = addressService.createAddress(memberId, requestDto);

        // Assert
        assertNotNull(result);
        verify(memberRepository, times(1)).findById(memberId);
        verify(addressRepository, times(1)).save(any(Address.class));
        verify(memberAddressRepository, times(1)).save(any(MemberAddress.class));
    }

    @Test
    @DisplayName("주소 업데이트 성공")
    void updateAddress_success() {
        // Arrange
        Long memberId = 1L;
        Address address = mock(Address.class);
        AddressRequestDto requestDto = AddressRequestDto.builder()
                .id(1L)
                .alias("Updated Office")
                .roadAddress("Updated Blvd")
                .detailAddress("Updated Suite")
                .postcode("99999")
                .isDefault(true)
                .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mock(Member.class)));
        when(addressRepository.findById(requestDto.getId())).thenReturn(Optional.of(address));
        when(addressRepository.save(address)).thenReturn(address);

        // Act
        AddressResponseDto result = addressService.updateAddress(memberId, requestDto);

        // Assert
        assertNotNull(result);
        verify(addressRepository, times(1)).save(address);
    }

    @Test
    @DisplayName("주소 삭제 성공")
    void deleteAddress_success() {
        // Arrange
        Long addressId = 1L;
        when(addressRepository.existsById(addressId)).thenReturn(true);

        // Act
        addressService.deleteAddress(addressId);

        // Assert
        verify(addressRepository, times(1)).deleteById(addressId);
    }
    @Test
    @DisplayName("카카오 주소 검색 및 저장 성공")
    void saveAddressFromKakao_success() {
        // Arrange
        Long memberId = 1L;
        Member member = mock(Member.class);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        MemberAddressRequestDto requestDto = MemberAddressRequestDto.builder()
                .alias("Home")
                .roadAddress("123 Main St")
                .postalCode("54321")
                .detail("Apt 101")
                .isDefault(true)
                .build();

        // Mock Kakao Response
        KakaoAddressResponseDto kakaoResponse = KakaoAddressResponseDto.builder()
                .documents(List.of(
                        KakaoAddressResponseDto.Document.builder()
                                .roadAddress(KakaoAddressResponseDto.RoadAddress.builder()
                                        .addressName("123 Main St")
                                        .zoneNo("54321")
                                        .build())
                                .build()
                ))
                .build();

        // WebClient Mock 설정
        when(webClient.get()).thenAnswer(invocation -> {
            WebClient.RequestHeadersUriSpec<?> uriSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
            when(uriSpecMock.uri(any(Function.class))).thenAnswer(uriInvocation -> {
                WebClient.RequestHeadersSpec<?> headersSpecMock = mock(WebClient.RequestHeadersSpec.class);
                when(headersSpecMock.retrieve()).thenAnswer(retrieveInvocation -> {
                    WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);
                    when(responseSpecMock.bodyToMono(eq(KakaoAddressResponseDto.class)))
                            .thenReturn(Mono.just(kakaoResponse));
                    return responseSpecMock;
                });
                return headersSpecMock;
            });
            return uriSpecMock;
        });

        // Act
        addressService.saveAddressFromKakao(memberId, requestDto);

        // Assert
        verify(addressRepository, times(1)).save(any(Address.class));
        verify(memberAddressRepository, times(1)).save(any(MemberAddress.class));
        verify(memberRepository, times(1)).save(member); // 연관 관계 업데이트 확인
    }



    @Test
    @DisplayName("searchRoadAddress 호출 테스트")
    void searchRoadAddress_emptyResult() {
        // Mock 빈 결과 반환
        KakaoAddressResponseDto kakaoResponse = KakaoAddressResponseDto.builder()
                .documents(Collections.emptyList())
                .build();

        when(webClient.get()).thenAnswer(invocation -> {
            WebClient.RequestHeadersUriSpec<?> uriSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
            when(uriSpecMock.uri(any(Function.class))).thenAnswer(uriInvocation -> {
                WebClient.RequestHeadersSpec<?> headersSpecMock = mock(WebClient.RequestHeadersSpec.class);
                when(headersSpecMock.retrieve()).thenAnswer(retrieveInvocation -> {
                    WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);
                    when(responseSpecMock.bodyToMono(eq(KakaoAddressResponseDto.class)))
                            .thenReturn(Mono.just(kakaoResponse));
                    return responseSpecMock;
                });
                return headersSpecMock;
            });
            return uriSpecMock;
        });

        // Act
        List<KakaoAddressResponseDto.Document> result = addressService.searchRoadAddress("54321");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


    @Test
    @DisplayName("카카오 API 응답 - 도로명 주소 및 우편번호 반환")
    void saveAddressFromKakao_withRoadAddress() {
        // Arrange
        Long memberId = 1L;
        Member member = mock(Member.class);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        MemberAddressRequestDto requestDto = MemberAddressRequestDto.builder()
                .alias("Home")
                .roadAddress("123 Main St")
                .postalCode("54321")
                .detail("Apt 101")
                .isDefault(false)
                .build();

        KakaoAddressResponseDto kakaoResponse = KakaoAddressResponseDto.builder()
                .documents(List.of(
                        KakaoAddressResponseDto.Document.builder()
                                .roadAddress(KakaoAddressResponseDto.RoadAddress.builder()
                                        .addressName("123 Main St")
                                        .zoneNo("54321")
                                        .build())
                                .build()
                ))
                .build();

        when(webClient.get()).thenAnswer(invocation -> {
            WebClient.RequestHeadersUriSpec<?> uriSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
            when(uriSpecMock.uri(any(Function.class))).thenAnswer(uriInvocation -> {
                WebClient.RequestHeadersSpec<?> headersSpecMock = mock(WebClient.RequestHeadersSpec.class);
                when(headersSpecMock.retrieve()).thenAnswer(retrieveInvocation -> {
                    WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);
                    when(responseSpecMock.bodyToMono(eq(KakaoAddressResponseDto.class)))
                            .thenReturn(Mono.just(kakaoResponse));
                    return responseSpecMock;
                });
                return headersSpecMock;
            });
            return uriSpecMock;
        });

        // Act
        addressService.saveAddressFromKakao(memberId, requestDto);

        // Assert
        verify(addressRepository, times(1)).save(any(Address.class));
        verify(memberAddressRepository, times(1)).save(any(MemberAddress.class));
        verify(memberRepository, times(1)).save(member); // 연관 관계 업데이트 확인
    }


    @Test
    @DisplayName("주소 업데이트 - 기본 주소로 설정")
    void updateAddress_setAsDefault() {
        // Arrange
        Long memberId = 1L;
        Address existingAddress = Address.builder()
                .id(1L)
                .roadAddress("Old Address")
                .isDefault(false)
                .build();
        Address updatedAddress = Address.builder()
                .id(1L)
                .roadAddress("Updated Address")
                .isDefault(true)
                .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mock(Member.class)));
        when(addressRepository.findById(1L)).thenReturn(Optional.of(existingAddress));
        when(addressRepository.save(existingAddress)).thenReturn(updatedAddress);

        // Act
        AddressResponseDto result = addressService.updateAddress(memberId, AddressRequestDto.builder()
                .id(1L)
                .alias("Home")
                .roadAddress("Updated Address")
                .postcode("54321")
                .detailAddress("Apt 202")
                .isDefault(true)
                .build());

        // Assert
        assertEquals("Updated Address", result.getRoadAddress());
        assertTrue(updatedAddress.getIsDefault());
        verify(addressRepository, times(1)).save(existingAddress);
    }


}