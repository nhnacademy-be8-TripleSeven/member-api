package com.example.msamemberapi.application.service;

import com.example.msamemberapi.application.dto.response.KakaoAddressResponseDto;

public interface KakaoAddressService {
    KakaoAddressResponseDto searchAddress(String query);
}