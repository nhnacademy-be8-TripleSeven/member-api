package com.example.msamemberapi.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonProperty;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MemberAddressRequestDto {

    @NotNull
    @NotBlank(message = "별칭을 적어주세요.")
    @Size(max = 50)
    @JsonProperty("alias")
    private String alias;

    @NotNull
    @NotBlank(message = "도로명주소는 필수 입력 항목입니다.")
    @JsonProperty("roadAddress")
    private String roadAddress;

    @NotNull
    @NotBlank(message = "우편번호는 필수 입력 항목입니다.")
    @JsonProperty("postcode")
    private String postalCode;

    @NotNull
    @NotBlank(message = "상세 주소는 필수 입력 항목입니다.")
    @JsonProperty("detailAddress")
    private String detail;

    @NotNull
    @JsonProperty("isDefault") 
    private Boolean isDefault;
}