package com.example.msamemberapi.application.service.impl;

import com.example.msamemberapi.application.entity.Member;
import com.example.msamemberapi.application.enums.MemberRole;
import com.example.msamemberapi.application.error.CustomException;
import com.example.msamemberapi.application.error.ErrorCode;
import com.example.msamemberapi.application.repository.MemberRepository;
import com.example.msamemberapi.application.service.skm.SecureKeyManagerService;
import com.example.msamemberapi.common.annotations.secure.SecureKey;
import com.netflix.discovery.converters.Auto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailServiceImplTest {

    private static final String VERIFY_EMAIL_TITLE = "Verification Code";

    @InjectMocks
    private EmailServiceImpl emailVerifyService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private ListOperations<String, String> listOperations;

    @SecureKey("asdf")
    private String USERNAME;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(emailVerifyService, "USERNAME", "wjdcks282828@gmail.com");
        ReflectionTestUtils.setField(emailVerifyService, "PASSWORD", "epqa ghyt fulf brrj");
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
    @DisplayName("sendVerifyCode - 성공적으로 인증 코드 저장 및 이메일 전송")
    void sendVerifyCode_success() {
        // Arrange
        String email = "wjdcks282828@gmail.com";
        String verifyCode = "123456"; // 모킹할 인증 코드 값

        EmailServiceImpl emailVerifyServiceSpy = spy(emailVerifyService);
        when(emailVerifyServiceSpy.generateVerifyCode()).thenReturn(verifyCode);
        doNothing().when(valueOperations).set(eq("verify:email:" + email), eq(verifyCode), eq(5L), eq(TimeUnit.MINUTES));
        doNothing().when(emailVerifyServiceSpy).sendEmail(eq(email), eq(VERIFY_EMAIL_TITLE), anyString());
        emailVerifyServiceSpy.sendVerifyCode(email); // 테스트할 메서드 호출

        verify(valueOperations, times(1)).set(eq("verify:email:" + email), eq(verifyCode), eq(5L), eq(TimeUnit.MINUTES)); // Redis 호출 검증
        verify(emailVerifyServiceSpy, times(1)).sendEmail(eq(email), eq(VERIFY_EMAIL_TITLE), anyString());  // sendEmail 호출 검증
    }




    @Test
    @DisplayName("sendPasswordResetEmail - 비밀번호 재설정 코드 저장 및 이메일 전송")
    void sendPasswordResetEmail_success() {
        // Arrange
        String email = "test@example.com";
        doNothing().when(valueOperations).set(startsWith("verify:password:email:"), anyString(), eq(5L), eq(TimeUnit.MINUTES));

        // Act
        emailVerifyService.sendPasswordResetEmail(email);

        // Assert
        verify(valueOperations, times(1)).set(startsWith("verify:password:email:"), anyString(), eq(5L), eq(TimeUnit.MINUTES));
    }

    @Test
    @DisplayName("validateResetPasswordCode - 성공적으로 검증 코드 확인 및 삭제")
    void validateResetPasswordCode_success() {
        // Arrange
        String email = "test@example.com";
        String code = "123456";
        when(valueOperations.get("verify:password:email:" + email)).thenReturn(code);

        // Act
        emailVerifyService.validateResetPasswordCode(email, code);

        // Assert
        verify(redisTemplate, times(1)).delete("verify:password:email:" + email);
    }

    @Test
    @DisplayName("validateResetPasswordCode - 코드가 없거나 잘못된 경우")
    void validateResetPasswordCode_fail_invalidCode() {
        // Arrange
        String email = "test@example.com";
        String code = "123456";
        when(valueOperations.get("verify:password:email:" + email)).thenReturn(null);

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class,
                () -> emailVerifyService.validateResetPasswordCode(email, code));
        assertEquals(ErrorCode.INVALID_VERIFICATION_CODE, exception.getErrorCode());
    }

    @Test
    @DisplayName("sendAccountActiveEmail - 성공적으로 계정 활성화 이메일 전송")
    void sendAccountActiveEmail_success() {
        // Arrange
        String loginId = "testUser";
        String email = "test@example.com";
        Member mockMember = mock(Member.class);
        when(mockMember.getEmail()).thenReturn(email);
        when(memberRepository.findByMemberAccount_Id(loginId)).thenReturn(Optional.of(mockMember));

        // Act
        emailVerifyService.sendAccountActiveEmail(loginId);

        // Assert
        verify(valueOperations, times(1)).set(startsWith("verify:account:active:"), anyString(), eq(5L), eq(TimeUnit.MINUTES));
    }

    @Test
    @DisplayName("verifyAccountActiveCode - 코드 검증 성공 및 역할 업데이트")
    void verifyAccountActiveCode_success() {
        // Arrange
        String email = "test@example.com";
        String code = "123456";
        Member mockMember = mock(Member.class);
        when(valueOperations.get("verify:account:active:" + email)).thenReturn(code);
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(mockMember));

        // Act
        emailVerifyService.verifyAccountActiveCode(email, code);

        // Assert
        verify(mockMember, times(1)).removeRole(MemberRole.INACTIVE);
        verify(mockMember, times(1)).addRole(MemberRole.USER);
        verify(redisTemplate, times(1)).delete("verify:password:email:" + email);
    }

    @Test
    @DisplayName("generateVerifyCode - 6자리 코드 생성 확인")
    void generateVerifyCode_success() {
        // Act
        String code = emailVerifyService.generateVerifyCode();

        // Assert
        assertEquals(6, code.length());
        assertDoesNotThrow(() -> Integer.parseInt(code));
    }

    @Test
    @DisplayName("sendEmail - AddressException 처리")
    void sendEmail_fail_addressException() {
        // Arrange
        String recipientEmail = "invalidEmail";
        String subject = "Test Subject";
        String content = "Test Content";

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class,
                () -> emailVerifyService.sendEmail(recipientEmail, subject, content));
        assertEquals(ErrorCode.EMAIL_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("sendEmail - MessagingException 처리")
    void sendEmail_fail_messagingException() throws Exception {
        // Arrange
        String recipientEmail = "test@example.com";
        String subject = "Test Subject";
        String content = "Test Content";

        // Mock static method of Transport.send
        try (MockedStatic<Transport> mockedStatic = Mockito.mockStatic(Transport.class)) {
            mockedStatic.when(() -> Transport.send(any(MimeMessage.class)))
                    .thenThrow(MessagingException.class);

            // Act & Assert
            CustomException exception = assertThrows(CustomException.class,
                    () -> emailVerifyService.sendEmail(recipientEmail, subject, content));
            assertEquals(ErrorCode.EMAIL_NOT_FOUND, exception.getErrorCode());
        }
    }

}
