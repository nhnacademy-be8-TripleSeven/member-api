package com.example.msamemberapi.application.service.impl;

import com.example.msamemberapi.application.dto.request.AddressRequestDto;
import com.example.msamemberapi.application.dto.response.AddressResponseDto;
import com.example.msamemberapi.application.entity.Address;
import com.example.msamemberapi.application.entity.Member;
import com.example.msamemberapi.application.repository.AddressRepository;
import com.example.msamemberapi.application.repository.MemberRepository;
import com.example.msamemberapi.application.service.impl.AddressServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private AddressServiceImpl addressService;

    private Member mockMember;
    private Address mockAddress;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockMember = Member.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        mockAddress = Address.builder()
                .id(1L)
                .member(mockMember)
                .alias("Home")
                .roadAddress("123 Main Street")
                .detailAddress("Apt 101")
                .postcode("12345")
                .isDefault(true)
                .build();
    }

    @Test
    void testGetAllAddressesById() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(mockMember));
        when(addressRepository.findByMemberId(1L)).thenReturn(List.of(mockAddress));

        List<AddressResponseDto> addresses = addressService.getAllAddressesById(1L);

        assertThat(addresses).hasSize(1);
        assertThat(addresses.get(0).getRoadAddress()).isEqualTo("123 Main Street");

        verify(addressRepository, times(1)).findByMemberId(1L);
    }

//    @Test
//    void testCreateAddress() {
//        AddressRequestDto addressRequestDto = AddressRequestDto.builder()
//                .alias("Office")
//                .roadAddress("456 Office Blvd")
//                .detailAddress("Suite 200")
//                .postcode("54321")
//                .isDefault(false)
//                .build();
//
//        when(memberRepository.findById(1L)).thenReturn(Optional.of(mockMember));
//        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        AddressResponseDto response = addressService.createAddress(1L, addressRequestDto);
//
//        assertThat(response.getAlias()).isEqualTo("Office");
//        assertThat(response.getRoadAddress()).isEqualTo("456 Office Blvd");
//
//        verify(addressRepository, times(1)).save(any(Address.class));
//    }

    @Test
    void testDeleteAddress() {
        when(addressRepository.existsById(1L)).thenReturn(true);

        addressService.deleteAddress(1L);

        verify(addressRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteAddress_NotFound() {
        when(addressRepository.existsById(1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> addressService.deleteAddress(1L));

        verify(addressRepository, times(0)).deleteById(anyLong());
    }


}