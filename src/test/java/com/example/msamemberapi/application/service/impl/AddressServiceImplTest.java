package com.example.msamemberapi.application.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.msamemberapi.application.dto.request.AddressRequestDto;
import com.example.msamemberapi.application.dto.response.AddressResponseDto;
import com.example.msamemberapi.application.dto.response.KakaoAddressResponseDto;
import com.example.msamemberapi.application.entity.Address;
import com.example.msamemberapi.application.entity.Member;
import com.example.msamemberapi.application.entity.MemberAddress;
import com.example.msamemberapi.application.repository.AddressRepository;
import com.example.msamemberapi.application.repository.MemberAddressRepository;
import com.example.msamemberapi.application.repository.MemberRepository;
import com.example.msamemberapi.application.service.AddressService;
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
    private WebClient.RequestHeadersSpec<?> headersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

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
    void saveAddressFromKakao_success_withAnswer() {
        // Arrange
        Long userId = 1L;
        String query = "Some Address";
        String alias = "Home";
        String detailAddress = "123 Suite";

        // Mock Member
        Member member = mock(Member.class);
        when(memberRepository.findById(userId)).thenReturn(Optional.of(member));

        // Mock KakaoAddressResponseDto
        KakaoAddressResponseDto kakaoResponse = KakaoAddressResponseDto.builder()
                .documents(List.of(
                        KakaoAddressResponseDto.Document.builder()
                                .roadAddress(KakaoAddressResponseDto.RoadAddress.builder()
                                        .addressName("123 Road")
                                        .zoneNo("54321")
                                        .build())
                                .build()
                ))
                .build();

        // WebClient Mock 설정
        when(webClient.get()).thenAnswer(invocation -> {
            WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
            when(requestHeadersUriSpec.uri(any(Function.class))).thenAnswer(uriInvocation -> {
                WebClient.RequestHeadersSpec<?> requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
                when(requestHeadersSpec.retrieve()).thenAnswer(retrieveInvocation -> {
                    WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
                    when(responseSpec.bodyToMono(KakaoAddressResponseDto.class)).thenReturn(Mono.just(kakaoResponse));
                    return responseSpec;
                });
                return requestHeadersSpec;
            });
            return requestHeadersUriSpec;
        });

        // Act
        addressService.saveAddressFromKakao(userId, query, alias, detailAddress);

        // Assert
        verify(addressRepository, times(1)).save(any(Address.class));
        verify(memberAddressRepository, times(1)).save(any(MemberAddress.class));
        verify(memberRepository, times(1)).save(member);
    }
}



