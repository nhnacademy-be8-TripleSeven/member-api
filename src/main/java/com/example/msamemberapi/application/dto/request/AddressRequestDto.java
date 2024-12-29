package com.example.msamemberapi.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressRequestDto {

    @NotNull
    @NotBlank(message = "도로명 주소는 필수 입력 항목입니다.")
    @Size(max = 255, message = "도로명 주소는 최대 255자까지 가능합니다.")
    private String roadAddress;

    @NotNull
    @NotBlank(message = "상세 주소는 필수 입력 항목입니다.")
    @Size(max = 255, message = "상세 주소는 최대 255자까지 가능합니다.")
    private String detail;

    @NotNull
    @NotBlank(message = "별칭은 필수 항목입니다.")
    @Size(max = 50, message = "별칭은 최대 50자까지 입력 가능합니다.")
    private String alias;


}