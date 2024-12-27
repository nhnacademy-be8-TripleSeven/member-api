package com.example.msamemberapi.application.dto.request;

import com.example.msamemberapi.application.enums.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@AllArgsConstructor
@Getter
public class JoinRequestDto {

    @NotNull
    private String loginId;
    @NotNull
    private String password;
    @NotNull
    private String name;
    @NotNull
    private String email;
    @NotNull
    private String phoneNumber;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birth;
    @NotNull
    private Gender gender;
}
