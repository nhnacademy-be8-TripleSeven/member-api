package com.example.msamemberapi.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class JoinRequestDto {

    private String id;
    private String password;
}
