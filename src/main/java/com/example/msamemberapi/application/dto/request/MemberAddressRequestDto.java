package com.example.msamemberapi.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MemberAddressRequestDto {
    @NotNull
    @NotBlank(message = "별칭은 필수 입력 항목입니다.")
    @Size(max = 50, message = "별칭은 최대 50자까지 가능합니다.")
    private String alias;

    @NotNull
    @NotBlank(message = "도로명주소는 필수 입력 항목입니다.")
    private String roadAddress;

    @NotNull
    @NotBlank(message = "우편번호는 필수 입력 항목입니다.")
    private String postalCode;

    @NotNull
    @NotBlank(message = "상세 주소는 필수 입력 항목입니다.")
    private String detail;
}
