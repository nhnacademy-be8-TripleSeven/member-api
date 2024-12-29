package com.example.msamemberapi.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class MemberAddressResponseDto {
    private Long id;
    private String alias;
    private String detail;
    private String roadAddress;
    private String postalCode;
}