package com.example.msamemberapi.application.service.impl;

import com.example.msamemberapi.application.error.CustomException;
import com.example.msamemberapi.application.error.ErrorCode;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailVerifyServiceImplTest {

    @InjectMocks
    private EmailVerifyServiceImpl emailVerifyService;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private ListOperations<String, String> listOperations;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(emailVerifyService, "USERNAME", "TEST_USERNAME@example.com");
        ReflectionTestUtils.setField(emailVerifyService, "PASSWORD", "TEST_PASSWORD");
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
}
