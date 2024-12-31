package com.example.msamemberapi.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KakaoAddressResponseDto {

    private String addressName;
    private String buildingName;
    private String mainBuildingNo;
    private String region1DepthName;
    private String region2DepthName;
    private String region3DepthName;
}