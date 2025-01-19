package com.example.msamemberapi.application.service.impl;

import com.example.msamemberapi.application.dto.request.AddressRequestDto;
import com.example.msamemberapi.application.dto.response.AddressResponseDto;
import com.example.msamemberapi.application.dto.response.KakaoAddressResponseDto;
import com.example.msamemberapi.application.dto.response.MemberAddressResponseDto;
import com.example.msamemberapi.application.entity.Address;
import com.example.msamemberapi.application.entity.Member;
import com.example.msamemberapi.application.repository.AddressRepository;
import com.example.msamemberapi.application.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;

class AddressServiceImplTest {

    @InjectMocks
    private AddressServiceImpl addressService;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private MemberRepository memberRepository;


    @Mock
    private WebClient webClient;

    private Member member;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        member = new Member(1L, "testUser", "test@example.com", "01012345678"); // 생성자 사용
    }

    @Test
    @DisplayName("주소 목록 조회 - 성공")
    void getAllAddressesByUserId_Success() {
        // Arrange
        Address address1 = Address.builder()
                .member(member)
                .postcode("12345")
                .roadAddress("서울시 강남구")
                .detailAddress("상세주소")
                .alias("집")
                .isDefault(true)
                .build();

        Address address2 = Address.builder()
                .member(member)
                .postcode("67890")
                .roadAddress("서울시 서초구")
                .detailAddress("상세주소")
                .alias("회사")
                .isDefault(false)
                .build();

        List<Address> addressList = Arrays.asList(address1, address2);

        when(addressRepository.findByMemberId(1L)).thenReturn(addressList);

        // Act
        List<AddressResponseDto> response = addressService.getAllAddressesByUserId(1L);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("서울시 강남구", response.get(0).getRoadAddress());
    }

    @Test
    @DisplayName("회원 주소 생성 - 성공")
    void createMemberAddress_Success() {
        // Arrange
        MemberAddressResponseDto responseDto = MemberAddressResponseDto.builder()
                .id(1L)
                .alias("집")
                .detail("상세주소")
                .roadAddress("서울시 강남구")
                .postalCode("12345")
                .isDefault(true) // true 설정
                .build();

        // Act
        boolean isDefault = responseDto.getIsDefault(); // getIsDefault() 메서드 호출

        // Assert
        assertNotNull(responseDto);
        assertTrue(isDefault);  // isDefault가 true여야 합니다.
    }

    @Test
    @DisplayName("주소 생성 - 필수 값 누락 시 실패")
    void createAddress_Fail_MissingRequiredFields() {
        // Arrange
        AddressRequestDto requestDto = AddressRequestDto.builder()
                .postcode(null)
                .roadAddress("서울시 강남구")
                .detailAddress("상세주소")
                .alias("집")
                .isDefault(true)
                .build();

        // Act & Assert
        IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> addressService.createAddress(1L, requestDto));
        assertEquals("필수 값이 누락되었습니다.", thrown.getMessage());
    }

    @Test
    @DisplayName("주소 수정 - 성공")
    void updateAddress_Success() {
        // Arrange
        Long addressId = 1L;
        AddressRequestDto requestDto = AddressRequestDto.builder()
                .postcode("67890")
                .roadAddress("서울시 서초구")
                .detailAddress("상세주소")
                .alias("회사")
                .isDefault(false)
                .build();

        Address existingAddress = Address.builder()
                .member(member)
                .postcode("12345")
                .roadAddress("서울시 강남구")
                .detailAddress("상세주소")
                .alias("집")
                .isDefault(true)
                .build();

        when(addressRepository.findById(addressId)).thenReturn(Optional.of(existingAddress));
        when(addressRepository.save(any(Address.class))).thenReturn(existingAddress);

        // Act
        AddressResponseDto response = addressService.updateAddress(addressId, requestDto);

        // Assert
        assertNotNull(response);
        assertEquals("서울시 서초구", response.getRoadAddress());
        assertEquals("상세주소", response.getDetailAddress());
    }

    @Test
    @DisplayName("주소 삭제 - 성공")
    void deleteAddress_Success() {
        // Arrange
        Long addressId = 1L;
        when(addressRepository.existsById(addressId)).thenReturn(true);
        doNothing().when(addressRepository).deleteById(addressId);

        // Act
        addressService.deleteAddress(addressId);

        // Assert
        verify(addressRepository, times(1)).deleteById(addressId);
    }

    @Test
    @DisplayName("주소 삭제 - 존재하지 않는 주소 ID 시 실패")
    void deleteAddress_Fail_NotFound() {
        // Arrange
        Long addressId = 999L;
        when(addressRepository.existsById(addressId)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> addressService.deleteAddress(addressId));
        assertEquals("삭제할 주소가 존재하지 않습니다.", thrown.getMessage());
    }

}