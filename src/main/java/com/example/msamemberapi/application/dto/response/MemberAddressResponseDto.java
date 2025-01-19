package com.example.msamemberapi.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("alias")
    private String alias;

    @JsonProperty("detailAddress")
    private String detail;

    @JsonProperty("roadAddress")
    private String roadAddress;

    @JsonProperty("postcode")
    private String postalCode;
    
    private Boolean isDefault;
}