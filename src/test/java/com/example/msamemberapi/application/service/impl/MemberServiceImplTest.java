package com.example.msamemberapi.application.service.impl;

import com.example.msamemberapi.application.dto.request.JoinRequestDto;
import com.example.msamemberapi.application.dto.response.MemberAuthInfo;
import com.example.msamemberapi.application.entity.Member;
import com.example.msamemberapi.application.entity.MemberAccount;
import com.example.msamemberapi.application.enums.AccountType;
import com.example.msamemberapi.application.enums.Gender;
import com.example.msamemberapi.application.enums.MemberRole;
import com.example.msamemberapi.application.error.CustomException;
import com.example.msamemberapi.application.error.ErrorCode;
import com.example.msamemberapi.application.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MemberServiceImplTest {

    @InjectMocks
    private MemberServiceImpl memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("회원 가입 성공")
    void join_success() {
        // Arrange
        JoinRequestDto joinRequestDto = new JoinRequestDto("test", "1234", "chan", "test@example.com", "01012345678",
                new Date(), Gender.MALE);

        when(memberRepository.existsByMemberAccount_Id(joinRequestDto.getLoginId())).thenReturn(false);
        when(memberRepository.existsByEmail(joinRequestDto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(joinRequestDto.getPassword())).thenReturn("encodedPassword");
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        var result = memberService.join(joinRequestDto);

        // Assert
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("chan", result.getName());
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("중복된 로그인 ID로 회원 가입시 에러 발생")
    void join_duplicateLoginId_throwsException() {
        // Arrange
        JoinRequestDto joinRequestDto = new JoinRequestDto("test", "1234", "chan", "test@example.com", "01012345678",
                new Date(), Gender.MALE);

        when(memberRepository.existsByMemberAccount_Id(joinRequestDto.getLoginId())).thenReturn(true);

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> memberService.join(joinRequestDto));
        assertEquals(ErrorCode.ALREADY_EXIST_LOGIN_ID, exception.getErrorCode());
    }

    @Test
    @DisplayName("회원 ID로 조회 성공")
    void findByMemberId_success() {
        // Arrange
        String loginId = "testUser";
        MemberAccount memberAccount = MemberAccount.builder().id(loginId).build();
        Member member = Member.builder().memberAccount(memberAccount).build();

        when(memberRepository.findByMemberAccount_Id(loginId)).thenReturn(Optional.of(member));

        // Act
        MemberAuthInfo result = memberService.findByMemberId(loginId);

        // Assert
        assertNotNull(result);
        assertEquals(loginId, result.getMemberAccount().getId());
        verify(memberRepository, times(1)).findByMemberAccount_Id(loginId);
    }

    @Test
    @DisplayName("존재하지 않는 회원 ID로 조회시 에러 발생")
    void findByMemberId_notFound_throwsException() {
        // Arrange
        String loginId = "nonexistentUser";

        when(memberRepository.findByMemberAccount_Id(loginId)).thenReturn(Optional.empty());

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> memberService.findByMemberId(loginId));
        assertEquals(ErrorCode.ACCOUNT_ID_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("회원 ID로 삭제 성공")
    void deleteByMemberId_success() {
        // Arrange
        Long memberId = 1L;

        Member member = Member.builder().id(1L).build();
        member.addRole(MemberRole.USER);


        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // Act
        memberService.quitMember(memberId);

        Assertions.assertTrue(member.getRoles().contains(MemberRole.QUIT.toString()));
    }

    @Test
    @DisplayName("이메일로 회원 계정 정보 조회 성공")
    void getMemberAccountByEmail_success() {
        // Arrange
        String email = "test@example.com";
        MemberAccount memberAccount = MemberAccount.builder().id("testUser").accountType(AccountType.REGISTERED).build();
        Member member = Member.builder().email(email).memberAccount(memberAccount).build();
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));

        // Act
        var result = memberService.getMemberAccountByEmail(email);

        // Assert
        assertNotNull(result);
        assertEquals("testUser", result.getLoginId());
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 조회시 에러 발생")
    void getMemberAccountByEmail_notFound_throwsException() {
        // Arrange
        String email = "nonexistent@example.com";

        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> memberService.getMemberAccountByEmail(email));
        assertEquals(ErrorCode.EMAIL_NOT_FOUND, exception.getErrorCode());
    }
}
