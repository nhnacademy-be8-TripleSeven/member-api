package com.example.msamemberapi.application.service.impl;

import com.example.msamemberapi.application.entity.Member;
import com.example.msamemberapi.application.enums.MemberRole;
import com.example.msamemberapi.application.error.CustomException;
import com.example.msamemberapi.application.error.ErrorCode;
import com.example.msamemberapi.application.repository.MemberRepository;
import com.example.msamemberapi.application.service.EmailService;
import com.example.msamemberapi.common.annotations.secure.SecureKey;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class EmailServiceImpl implements EmailService {

    private static final int VERIFY_CODE_EXPIRATION = 5;
    private static final int VERIFICATION_CODE_RANGE = 999999;
    private static final String VERIFY_EMAIL_TITLE = "Verification Code";
    private static final String EMAIL_KEY_PREFIX = "verify:email:";
    private static final String CHANGE_PASSWORD_VERIFY_EMAIL_PREFIX = "verify:password:email:";
    private static final String EMAIL_VERIFY_SUCCESS_KEY_PREFIX = "verify:email:success:";
    private static final String ACCOUNT_ACTIVE_EMAIL_KEY_PREFIX = "verify:account:active:";

    @SecureKey("secret.keys.email.account")
    private String USERNAME;
    @SecureKey("secret.keys.email.password")
    private String PASSWORD;
    private final Session EMAIL_SESSION = createEmailSession();
    private final RedisTemplate<String, String> redisTemplate;
    private final MemberRepository memberRepository;


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

    @Override
    public void sendPasswordResetEmail(String email) {
        String resetCode = generateVerifyCode();
        redisTemplate.opsForValue().set(CHANGE_PASSWORD_VERIFY_EMAIL_PREFIX.concat(email), resetCode, VERIFY_CODE_EXPIRATION, TimeUnit.MINUTES);

        String resetUrl = String.format("https://nhn24.store/frontend/reset-password?email=%s&code=%s", email, resetCode);
        String emailContent = String.format(
                "비밀번호 변경 요청이 들어왔습니다.\n%s\n\n" +
                        "만약 비밀번호 변경 요청을 하지 않으셨다면 보안을 위해 비밀번호를 변경해주세요.",
                resetUrl
        );

        sendEmail(email, "Nhn24.store 비밀번호 변경", emailContent);
    }

    @Override
    public void validateResetPasswordCode(String email, String code) {
        String storedCode = redisTemplate.opsForValue().get(CHANGE_PASSWORD_VERIFY_EMAIL_PREFIX.concat(email));
        if (storedCode == null || !storedCode.equals(code)) {
            throw new CustomException(ErrorCode.INVALID_VERIFICATION_CODE);
        }

        redisTemplate.delete(CHANGE_PASSWORD_VERIFY_EMAIL_PREFIX.concat(email));
    }

    @Override
    public void sendAccountActiveEmail(String loginId) {

        Member member = memberRepository.findByMemberAccount_Id(loginId).orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));

        String resetCode = generateVerifyCode();
        redisTemplate.opsForValue().set(ACCOUNT_ACTIVE_EMAIL_KEY_PREFIX.concat(member.getEmail()), resetCode, VERIFY_CODE_EXPIRATION, TimeUnit.MINUTES);

        String resetUrl = String.format("https://nhn24.store/frontend/active-account?email=%s&code=%s", member.getEmail(), resetCode);
        String emailContent = String.format(
                "사용자의 계정이 일시적으로 잠겼습니다.\n%s\n\n" +
                        "계정 잠금을 해제하려면 아래 링크를 클릭하세요",
                resetUrl
        );

        sendEmail(member.getEmail(), "Nhn24.store 휴면계정 해제", emailContent);
    }

    @Override
    @Transactional
    public void verifyAccountActiveCode(String email, String code) {
        String storedCode = redisTemplate.opsForValue().get(ACCOUNT_ACTIVE_EMAIL_KEY_PREFIX.concat(email));
        if (storedCode == null || !storedCode.equals(code)) {
            throw new CustomException(ErrorCode.INVALID_VERIFICATION_CODE);
        }

        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));
        member.removeRole(MemberRole.INACTIVE);
        member.addRole(MemberRole.USER);
        redisTemplate.delete(CHANGE_PASSWORD_VERIFY_EMAIL_PREFIX.concat(email));
    }

    protected void sendEmail(String recipientEmail, String subject, String content) {
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

    private String generateVerifyCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(VERIFICATION_CODE_RANGE));
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
