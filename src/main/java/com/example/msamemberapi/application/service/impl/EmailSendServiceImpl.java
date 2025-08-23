package com.example.msamemberapi.application.service.impl;

import com.example.msamemberapi.application.error.application.CustomException;
import com.example.msamemberapi.application.error.application.ErrorCode;
import com.example.msamemberapi.application.service.EmailSendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailSendServiceImpl implements EmailSendService {

    private String USERNAME = "tlswlfk@gmail.com";
    private String PASSWORD = "hgadoqeeqdxhawmo";
    private final Session EMAIL_SESSION = createEmailSession();

    @Override
    @Async
    public void sendEmail(String recipientEmail, String subject, String content) {
        try {
            Message message = new MimeMessage(EMAIL_SESSION);
            message.setFrom(new InternetAddress(USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setText(content);

            Transport.send(message);
        }  catch (AddressException addressEx) {
            throw new CustomException(ErrorCode.EMAIL_NOT_FOUND);
        } catch (MessagingException messagingEx) {
            log.error("Mail Message Error : {}", messagingEx.getMessage());
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private Session createEmailSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // SMTP Session 생성 (싱글톤)
        return Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });
    }
}
