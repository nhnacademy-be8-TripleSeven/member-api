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
    @NotBlank
    private String postcode;

    @NotNull
    @NotBlank
    private String roadAddress;

    @NotNull
    @NotBlank
    private String detailAddress;

    @NotNull
    @NotBlank
    private String alias;
}