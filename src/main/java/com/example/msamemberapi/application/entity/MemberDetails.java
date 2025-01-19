package com.example.msamemberapi.application.entity;

import static org.aspectj.weaver.tools.cache.SimpleCacheFactory.enabled;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberDetails {
    private final Long id;
    private final String username;
    private final String password;
    private final String email;
    private final String role;


    public boolean isEnabled() {
        return enabled;
    }
}