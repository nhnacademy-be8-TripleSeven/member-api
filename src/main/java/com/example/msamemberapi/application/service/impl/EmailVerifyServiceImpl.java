package com.example.msamemberapi.application.service.impl;

import com.example.msamemberapi.application.error.CustomException;
import com.example.msamemberapi.application.error.ErrorCode;
import com.example.msamemberapi.application.service.EmailVerifyService;
import com.example.msamemberapi.common.annotations.secure.SecureKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailVerifyServiceImpl implements EmailVerifyService {

    private static final int VERIFY_CODE_EXPIRATION = 5;
    private static final int VERIFICATION_CODE_RANGE = 999999;
    private static final String VERIFY_EMAIL_TITLE = "Verification Code";
    private static final String EMAIL_KEY_PREFIX = "verify:email:";
    private static final String EMAIL_VERIFY_SUCCESS_KEY_PREFIX = "verify:email:success:";

    @SecureKey("secret.keys.email.account")
    private String USERNAME;
    @SecureKey("secret.keys.email.password")
    private String PASSWORD;
    private final Session EMAIL_SESSION = createEmailSession();
    private final RedisTemplate<String, String> redisTemplate;


    @Override
    public void sendVerifyCode(String email) {
        String verifyCode = generateVerifyCode();
        redisTemplate.opsForValue().set(EMAIL_KEY_PREFIX.concat(email), verifyCode, VERIFY_CODE_EXPIRATION, TimeUnit.MINUTES);
        sendEmail(email, VERIFY_EMAIL_TITLE, String.format("Verification Code : %s", verifyCode));
    }

    @Override
    public boolean isVerified(String email, String code) {
        String value = redisTemplate.opsForValue().get(EMAIL_KEY_PREFIX.concat(email));
        if (value == null) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        return value.equals(code);
    }

    @Override
    public void verify(String email, String code) {
        if (isVerified(email, code)) {
            redisTemplate.opsForList().rightPush(EMAIL_VERIFY_SUCCESS_KEY_PREFIX, email);
            redisTemplate.delete(EMAIL_KEY_PREFIX.concat(email));
        } else {
            throw new CustomException(ErrorCode.INVALID_VERIFICATION_CODE);
        }
    }

    @Override
    public void validateEmailIsVerified(String email) {
        List<String> verifiedEmails = redisTemplate.opsForList().range(EMAIL_VERIFY_SUCCESS_KEY_PREFIX, 0, -1);
        if (!verifiedEmails.contains(email)) {
            throw new CustomException(ErrorCode.INVALIDATE_EMAIL);
        }
    }


    private String generateVerifyCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(VERIFICATION_CODE_RANGE));
    }

    private void sendEmail(String recipientEmail, String subject, String content) {
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
            log.error("Mail Message Error : {}", messagingEx.getCause());
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
