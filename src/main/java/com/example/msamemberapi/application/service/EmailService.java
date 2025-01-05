package com.example.msamemberapi.application.service;

public interface EmailService {

    void sendVerifyCode(String email);
    boolean isVerified(String email, String code);
    void verify(String email, String code);
    void validateEmailIsVerified(String email);
    void sendPasswordResetEmail(String email);
    void validateResetPasswordCode(String email, String code);
    void sendAccountActiveEmail(String loginId);
    void verifyAccountActiveCode(String email, String code);
}
