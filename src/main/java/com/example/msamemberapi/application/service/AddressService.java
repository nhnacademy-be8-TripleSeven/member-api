package com.example.msamemberapi.application.service;

import com.example.msamemberapi.application.dto.request.AddressRequestDto;
import com.example.msamemberapi.application.dto.response.AddressResponseDto;
import com.example.msamemberapi.application.dto.response.KakaoAddressResponseDto;
import com.example.msamemberapi.application.entity.Address;
import java.util.List;

public interface AddressService {
    List<AddressResponseDto> getAllAddressesById(Long memberId);
    AddressResponseDto findAddressById(Long addressId);
    AddressResponseDto createAddress(Long userId, AddressRequestDto requestDto);
    AddressResponseDto updateAddress(Long id, AddressRequestDto requestDto);
    void deleteAddress(Long id);
    void saveAddressFromKakao(Long memberId, String query, String alias, String detailAddress);
    List<KakaoAddressResponseDto.Document> searchRoadAddress(String keyword);
}
