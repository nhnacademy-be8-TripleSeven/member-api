package com.example.msamemberapi.application.dto.request;

import com.example.msamemberapi.application.enums.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@AllArgsConstructor
@Getter
public class JoinRequestDto {

    private String loginId;
    private String password;
    private String name;
    private String email;
    private String phoneNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birth;
    private Gender gender;
}
