package com.example.msamemberapi.application.service;

import com.example.msamemberapi.application.dto.request.AddressRequestDto;
import com.example.msamemberapi.application.dto.response.AddressResponseDto;

import com.example.msamemberapi.application.dto.response.KakaoAddressResponseDto;
import java.util.List;

public interface AddressService {
    List<AddressResponseDto> getAllAddresses();
    AddressResponseDto createAddress(AddressRequestDto requestDto);
    AddressResponseDto updateAddress(Long id, AddressRequestDto requestDto);
    void deleteAddress(Long id);
    List<KakaoAddressResponseDto> searchRoadAddress(String keyword);
}