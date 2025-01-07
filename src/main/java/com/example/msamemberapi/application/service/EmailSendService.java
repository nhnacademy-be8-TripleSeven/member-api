package com.example.msamemberapi.application.service;

public interface EmailSendService {

    void sendEmail(String recipientEmail, String subject, String content);
}
