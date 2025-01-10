package com.example.msamemberapi.application.service.impl;

import com.example.msamemberapi.application.entity.Member;
import com.example.msamemberapi.application.enums.MemberRole;
import com.example.msamemberapi.application.error.CustomException;
import com.example.msamemberapi.application.error.ErrorCode;
import com.example.msamemberapi.application.repository.MemberRepository;
import com.example.msamemberapi.application.service.EmailSendService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailServiceImplTest {

    private static final long VERIFY_CODE_EXPIRATION = 5;
    @InjectMocks
    private EmailServiceImpl emailVerifyService;

    @Mock
    private EmailSendService emailSendService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private ListOperations<String, String> listOperations;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForList()).thenReturn(listOperations);
    }

    @Test
    @DisplayName("이메일 인증 확인 성공")
    void isVerified_success() {
        // Arrange
        String email = "test@example.com";
        String code = "123456";
        when(valueOperations.get("verify:email:" + email)).thenReturn(code);

        // Act
        boolean result = emailVerifyService.isVerified(email, code);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("이메일 인증 확인 실패 - 잘못된 코드")
    void isVerified_fail_invalidCode() {
        // Arrange
        String email = "test@example.com";
        String code = "123456";
        when(valueOperations.get("verify:email:" + email)).thenReturn("654321");

        // Act & Assert
        boolean result = emailVerifyService.isVerified(email, code);
        assertFalse(result);
    }

    @Test
    @DisplayName("이메일 인증 확인 실패 - 코드 없음")
    void isVerified_fail_noCode() {
        // Arrange
        String email = "test@example.com";
        when(valueOperations.get("verify:email:" + email)).thenReturn(null);

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> emailVerifyService.isVerified(email, "123456"));
        assertEquals(ErrorCode.BAD_REQUEST, exception.getErrorCode());
    }

    @Test
    @DisplayName("이메일 인증 완료 성공")
    void verify_success() {
        // Arrange
        String email = "test@example.com";
        String code = "123456";
        when(valueOperations.get("verify:email:" + email)).thenReturn(code);
        when(listOperations.rightPush("verify:email:success:", email)).thenReturn(1L);

        // Act
        emailVerifyService.verify(email, code);

        // Assert
        verify(listOperations, times(1)).rightPush("verify:email:success:", email);
        verify(redisTemplate, times(1)).delete("verify:email:" + email);
    }


    @Test
    @DisplayName("이메일 인증시 잘못된 코드가 발생했을 때 401오류 발생")
    void verify_fail_invalidCode() {
        // Arrange
        String email = "test@example.com";
        String code = "123456";
        when(valueOperations.get("verify:email:" + email)).thenReturn("654321");

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> emailVerifyService.verify(email, code));
        assertEquals(ErrorCode.INVALID_VERIFICATION_CODE, exception.getErrorCode());
        assertEquals(ErrorCode.INVALID_VERIFICATION_CODE.getHttpStatus(), HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("이메일 인증 여부 확인 - 인증됨")
    void validateEmailIsVerified_success() {
        // Arrange
        String email = "test@example.com";
        when(listOperations.range("verify:email:success:", 0, -1)).thenReturn(List.of(email));

        // Act & Assert
        assertDoesNotThrow(() -> emailVerifyService.validateEmailIsVerified(email));
    }

    @Test
    @DisplayName("이메일 인증 여부 확인 - 인증되지 않음")
    void validateEmailIsVerified_fail() {
        // Arrange
        String email = "test@example.com";
        when(listOperations.range("verify:email:success:", 0, -1)).thenReturn(List.of());

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> emailVerifyService.validateEmailIsVerified(email));
        assertEquals(ErrorCode.INVALIDATE_EMAIL, exception.getErrorCode());
    }

    @Test
    @DisplayName("이메일 인증 코드 전송")
    void sendVerifyCode_success() {
        // Arrange
        String email = "test@example.com";
        String verifyCode = "123456";

        doNothing().when(valueOperations).set("verify:email:" + email, verifyCode, VERIFY_CODE_EXPIRATION, TimeUnit.MINUTES);
        doNothing().when(emailSendService).sendEmail(eq(email), anyString(), contains(verifyCode));

        // Act
        emailVerifyService.sendVerifyCode(email);

        // Assert
        verify(valueOperations, times(1)).set(eq("verify:email:" + email), anyString(), eq(VERIFY_CODE_EXPIRATION), eq(TimeUnit.MINUTES));
        verify(emailSendService, times(1)).sendEmail(eq(email), eq("Verification Code"), contains("Verification Code"));
    }

    @Test
    @DisplayName("비밀번호 재설정 이메일 전송")
    void sendPasswordResetEmail_success() {
        // Arrange
        String email = "test@example.com";

        doNothing().when(valueOperations).set(contains("verify:password:email:" + email), anyString(), eq(VERIFY_CODE_EXPIRATION), eq(TimeUnit.MINUTES));
        doNothing().when(emailSendService).sendEmail(eq(email), anyString(), contains("reset-password"));

        // Act
        emailVerifyService.sendPasswordResetEmail(email);

        // Assert
        verify(valueOperations, times(1)).set(contains("verify:password:email:" + email), anyString(), eq(VERIFY_CODE_EXPIRATION), eq(TimeUnit.MINUTES));
        verify(emailSendService, times(1)).sendEmail(eq(email), eq("Nhn24.store 비밀번호 변경"), contains("reset-password"));
    }

    @Test
    @DisplayName("비밀번호 재설정 코드 검증 성공")
    void validateResetPasswordCode_success() {
        // Arrange
        String email = "test@example.com";
        String code = "123456";
        when(valueOperations.get("verify:password:email:" + email)).thenReturn(code);

        // Act & Assert
        assertDoesNotThrow(() -> emailVerifyService.validateResetPasswordCode(email, code));
        verify(redisTemplate, times(1)).delete("verify:password:email:" + email);
    }

    @Test
    @DisplayName("계정 활성화 코드 검증 성공")
    void verifyAccountActiveCode_success() {
        // Arrange
        String email = "test@example.com";
        String code = "123456";
        Member member = mock(Member.class);
        when(valueOperations.get("verify:account:active:" + email)).thenReturn(code);
        when(memberRepository.findByEmail(email)).thenReturn(java.util.Optional.of(member));

        doNothing().when(member).removeRole(MemberRole.INACTIVE);
        doNothing().when(member).addRole(MemberRole.USER);

        // Act
        emailVerifyService.verifyAccountActiveCode(email, code);

        // Assert
        verify(valueOperations, times(1)).get("verify:account:active:" + email);
        verify(member, times(1)).removeRole(MemberRole.INACTIVE);
        verify(member, times(1)).addRole(MemberRole.USER);
    }

}
