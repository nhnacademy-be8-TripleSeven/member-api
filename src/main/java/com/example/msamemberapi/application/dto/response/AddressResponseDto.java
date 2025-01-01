package com.example.msamemberapi.application.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class AddressResponseDto {
    private Long id;
    private String postcode;
    private String roadAddress;
    private String detailAddress;
    private String alias;
}