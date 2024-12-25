package com.example.msamemberapi.common.config.db;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DatabaseCredentials {

    private String url;
    private String username;
    private String password;

}