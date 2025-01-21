package com.example.msamemberapi.application.service.impl;

import com.example.msamemberapi.application.dto.request.MemberAddressRequestDto;
import com.example.msamemberapi.application.dto.response.MemberAddressResponseDto;
import com.example.msamemberapi.application.entity.Address;
import com.example.msamemberapi.application.entity.Member;
import com.example.msamemberapi.application.entity.MemberAddress;
import com.example.msamemberapi.application.repository.AddressRepository;
import com.example.msamemberapi.application.repository.MemberAddressRepository;
import com.example.msamemberapi.application.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MemberAddressServiceImplTest {

    @InjectMocks
    private MemberAddressServiceImpl memberAddressService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberAddressRepository memberAddressRepository;

    @Mock
    private AddressRepository addressRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("멤버 ID로 주소 목록 조회 성공")
    void getAddressesByMemberId_success() {
        // Arrange
        Long memberId = 1L;
        MemberAddress memberAddress = mock(MemberAddress.class);
        when(memberAddress.getId()).thenReturn(1L);
        when(memberAddress.getAlias()).thenReturn("Home");
        when(memberAddress.getIsDefault()).thenReturn(true);

        Address address = mock(Address.class);
        when(memberAddress.getAddress()).thenReturn(address);
        when(address.getDetailAddress()).thenReturn("Apt 101");
        when(address.getRoadAddress()).thenReturn("123 Main St");
        when(address.getPostcode()).thenReturn("12345");

        when(memberAddressRepository.findByMemberId(memberId)).thenReturn(List.of(memberAddress));

        // Act
        List<MemberAddressResponseDto> result = memberAddressService.getAddressesByMemberId(memberId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Home", result.get(0).getAlias());
    }

    @Test
    @DisplayName("멤버 주소 추가 성공")
    void addAddressToMember_success() {
        // Arrange
        Long memberId = 1L;
        Member member = mock(Member.class);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        MemberAddressRequestDto requestDto = MemberAddressRequestDto.builder()
                .alias("Home")
                .detail("Apt 101")
                .roadAddress("123 Main St")
                .postalCode("12345")
                .isDefault(true)
                .build();

        Address address = mock(Address.class);
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        MemberAddress memberAddress = mock(MemberAddress.class);
        when(memberAddressRepository.save(any(MemberAddress.class))).thenReturn(memberAddress);
        when(memberAddress.getId()).thenReturn(1L);

        // Act
        MemberAddressResponseDto result = memberAddressService.addAddressToMember(memberId, requestDto);

        // Assert
        assertNotNull(result);
        assertEquals("Home", result.getAlias());
        verify(memberRepository, times(1)).findById(memberId);
        verify(addressRepository, times(1)).save(any(Address.class));
        verify(memberAddressRepository, times(1)).save(any(MemberAddress.class));
    }

    @Test
    @DisplayName("멤버 주소 삭제 성공")
    void removeAddressFromMember_success() {
        // Arrange
        Long memberId = 1L;
        Long addressId = 1L;

        MemberAddress memberAddress = mock(MemberAddress.class);
        when(memberAddressRepository.findByMemberIdAndAddressId(memberId, addressId)).thenReturn(
                Optional.of(memberAddress));

        // Act
        memberAddressService.removeAddressFromMember(memberId, addressId);

        // Assert
        verify(memberAddressRepository, times(1)).delete(memberAddress);
    }

    @Test
    @DisplayName("멤버 주소 삭제 실패 - 주소가 없을 때")
    void removeAddressFromMember_addressNotFound() {
        // Arrange
        Long memberId = 1L;
        Long addressId = 1L;

        when(memberAddressRepository.findByMemberIdAndAddressId(memberId, addressId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> memberAddressService.removeAddressFromMember(memberId, addressId));
        verify(memberAddressRepository, never()).delete(any());
    }


    @Test
    @DisplayName("주소별 별칭 업데이트 실패 - 주소가 없을 때")
    void updateAlias_addressNotFound() {
        // Arrange
        Long memberAddressId = 1L;
        String alias = "Office";
        Boolean isDefault = true;

        when(memberAddressRepository.findById(memberAddressId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> memberAddressService.updateAlias(memberAddressId, alias, isDefault));
    }

    @Test
    @DisplayName("주소별 별칭 업데이트 성공")
    void updateAlias_success() {
        // Arrange
        Long memberAddressId = 1L;
        String alias = "Office";
        Boolean isDefault = true;

        // Mock Member
        Member member = mock(Member.class);
        when(member.getId()).thenReturn(1L);

        // Mock MemberAddress
        MemberAddress memberAddress = mock(MemberAddress.class);
        when(memberAddress.getId()).thenReturn(memberAddressId);
        when(memberAddress.getMember()).thenReturn(member);
        when(memberAddress.createUpdatedCopy(alias, isDefault)).thenReturn(memberAddress);

        // Mock Updated MemberAddress
        MemberAddress updatedMemberAddress = mock(MemberAddress.class);

        // Mock Repository Calls
        when(memberAddressRepository.findById(memberAddressId)).thenReturn(Optional.of(memberAddress));
        when(memberAddressRepository.findByMemberId(1L)).thenReturn(List.of(memberAddress));
        when(memberAddressRepository.save(any(MemberAddress.class))).thenReturn(updatedMemberAddress);

        // Act
        memberAddressService.updateAlias(memberAddressId, alias, isDefault);

        // Assert
        verify(memberAddressRepository, times(1)).findById(memberAddressId);
        verify(memberAddressRepository, times(1)).findByMemberId(1L);
        verify(memberAddressRepository, times(1)).save(memberAddress);
    }


    @Test
    @DisplayName("기본 주소 리셋 성공")
    void resetDefaultAddress_success() {
        // Arrange
        Long memberId = 1L;
        Long memberAddressId = 1L;

        // Mock Member
        Member member = Member.builder()
                .id(memberId)
                .build();

        // Mock Address
        Address address = Address.builder()
                .id(100L)
                .roadAddress("123 Main St")
                .build();

        // Mock MemberAddress 1
        MemberAddress memberAddress1 = MemberAddress.builder()
                .id(memberAddressId)
                .member(member)
                .address(address)
                .alias("Home")
                .isDefault(false)
                .build();

        // Mock MemberAddress 2
        MemberAddress memberAddress2 = MemberAddress.builder()
                .id(2L)
                .member(member)
                .address(address)
                .alias("Office")
                .isDefault(false)
                .build();

        // Mock Repository Behavior
        when(memberAddressRepository.findById(memberAddressId)).thenReturn(Optional.of(memberAddress1));
        when(memberAddressRepository.findByMemberId(memberId)).thenReturn(List.of(memberAddress1, memberAddress2));
        when(memberAddressRepository.save(any(MemberAddress.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        memberAddressService.updateAlias(memberAddressId, "Alias", true);

        // Assert
        verify(memberAddressRepository, times(1)).findById(memberAddressId);
        verify(memberAddressRepository, times(1)).findByMemberId(memberId);
        verify(memberAddressRepository, times(2)).save(any(MemberAddress.class)); // save called for both addresses
    }
}