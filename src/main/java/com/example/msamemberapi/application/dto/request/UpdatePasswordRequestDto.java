package com.example.msamemberapi.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdatePasswordRequestDto {

    @NotNull
    private String email;
    @NotNull
    private String code;
    @NotNull
    private String newPassword;
}
