package com.example.msamemberapi.application.service;

public interface EmailVerifyService {

    void sendVerifyCode(String email);
    boolean isVerified(String email, String code);
    void verify(String email, String code);
    void validateEmailIsVerified(String email);
}
