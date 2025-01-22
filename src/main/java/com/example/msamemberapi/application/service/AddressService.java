package com.example.msamemberapi.application.service;

import com.example.msamemberapi.application.dto.request.AddressRequestDto;
import com.example.msamemberapi.application.dto.request.MemberAddressRequestDto;
import com.example.msamemberapi.application.dto.response.AddressResponseDto;
import com.example.msamemberapi.application.dto.response.KakaoAddressResponseDto;
import com.example.msamemberapi.application.entity.Address;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public interface AddressService {
    List<AddressResponseDto> getAllAddressesById(Long memberId);
    AddressResponseDto findAddressById(Long addressId);
    AddressResponseDto createAddress(Long userId, AddressRequestDto requestDto);
    AddressResponseDto updateAddress(Long id, AddressRequestDto requestDto);
    void deleteAddress(Long id);


    List<KakaoAddressResponseDto.Document> searchRoadAddress(String keyword);

    void saveAddressFromKakao(Long memberId, @Valid MemberAddressRequestDto requestDto);
}
