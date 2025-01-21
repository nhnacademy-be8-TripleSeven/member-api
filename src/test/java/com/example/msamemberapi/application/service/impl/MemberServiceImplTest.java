package com.example.msamemberapi.application.service.impl;

import com.example.msamemberapi.application.dto.request.AddressRequestDto;
import com.example.msamemberapi.application.dto.request.JoinRequestDto;
import com.example.msamemberapi.application.dto.request.UpdatePasswordRequestDto;
import com.example.msamemberapi.application.dto.response.MemberAccountInfo;
import com.example.msamemberapi.application.dto.response.MemberAuthInfo;
import com.example.msamemberapi.application.dto.response.MemberDto;
import com.example.msamemberapi.application.dto.response.MemberGradeDto;
import com.example.msamemberapi.application.entity.Address;
import com.example.msamemberapi.application.entity.GradePolicy;
import com.example.msamemberapi.application.entity.Member;
import com.example.msamemberapi.application.entity.MemberAccount;
import com.example.msamemberapi.application.entity.User;
import com.example.msamemberapi.application.enums.AccountType;
import com.example.msamemberapi.application.enums.Gender;
import com.example.msamemberapi.application.enums.MemberGrade;
import com.example.msamemberapi.application.enums.MemberRole;
import com.example.msamemberapi.application.error.CustomException;
import com.example.msamemberapi.application.error.ErrorCode;
import com.example.msamemberapi.application.feign.OrderFeignClient;
import com.example.msamemberapi.application.repository.AddressRepository;
import com.example.msamemberapi.application.repository.GradePolicyRepository;
import com.example.msamemberapi.application.repository.MemberRepository;
import com.example.msamemberapi.application.service.AddressService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
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

    @Mock
    private GradePolicyRepository gradePolicyRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private AddressService addressService;

    @Mock
    private OrderFeignClient orderFeignClient;

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

        GradePolicy gradePolicy = GradePolicy.builder()
                .grade(MemberGrade.GOLD) // 예시로 GOLD 등급 설정
                .build();

        when(gradePolicyRepository.findByGrade(any(MemberGrade.class))).thenReturn(Optional.ofNullable(gradePolicy));

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
    @DisplayName("중복된 이메일로 회원 가입시 에러 발생")
    void join_duplicateEmail_throwsException() {
        // Arrange
        JoinRequestDto joinRequestDto = new JoinRequestDto("test", "1234", "chan", "test@example.com", "01012345678",
                new Date(), Gender.MALE);

        when(memberRepository.existsByEmail(any())).thenReturn(true);

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> memberService.join(joinRequestDto));
        assertEquals(ErrorCode.ALREADY_EXIST_EMAIL, exception.getErrorCode());
    }

    @Test
    @DisplayName("중복된 핸드폰번호로 회원 가입시 에러 발생")
    void join_duplicatePhone_throwsException() {
        // Arrange
        JoinRequestDto joinRequestDto = new JoinRequestDto("test", "1234", "chan", "test@example.com", "01012345678",
                new Date(), Gender.MALE);

        when(memberRepository.existsByUserPhoneNumber(any())).thenReturn(true);

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> memberService.join(joinRequestDto));
        assertEquals(ErrorCode.ALREADY_EXIST_PHONE, exception.getErrorCode());
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

//    @Test
//    @DisplayName("회원 ID로 삭제 성공")
//    void deleteByMemberId_success() {
//        // Arrange
//        Long memberId = 1L;
//
//        Member member = Member.builder().id(1L).build();
//        member.addRole(MemberRole.USER);
//
//
//        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
//
//        // Act
//        memberService.quitMember(memberId);
//
//        Assertions.assertTrue(member.getRoles().contains(MemberRole.QUIT.toString()));
//    }

    @Test
    @DisplayName("이메일로 회원 계정 정보 조회 성공")
    void getMemberAccountByEmail_success() {
        // Arrange
        String email = "test@example.com";
        MemberAccount memberAccount =
                MemberAccount.builder().id("testUser").accountType(AccountType.REGISTERED).build();
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
        CustomException exception =
                assertThrows(CustomException.class, () -> memberService.getMemberAccountByEmail(email));
        assertEquals(ErrorCode.EMAIL_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("회원 비밀번호 업데이트 성공")
    void updateMemberPassword_success() {
        // Arrange
        UpdatePasswordRequestDto updatePasswordRequestDto =
                new UpdatePasswordRequestDto("nonexistent@example.com", "123456", "newPassword");
        String encodedPassword = "encodedPassword";
        Member member = Member.builder()
                .email("test@example.com")
                .memberAccount(MemberAccount.builder()
                        .accountType(AccountType.REGISTERED)
                        .password("oldPassword")
                        .build())
                .build();

        when(memberRepository.findByEmail(updatePasswordRequestDto.getEmail())).thenReturn(Optional.of(member));
        when(passwordEncoder.encode(updatePasswordRequestDto.getNewPassword())).thenReturn(encodedPassword);

        // Act
        memberService.updateMemberPassword(updatePasswordRequestDto);

        // Assert
        assertEquals(encodedPassword, member.getMemberAccount().getPassword());
        verify(memberRepository, times(1)).findByEmail(updatePasswordRequestDto.getEmail());
    }

    @Test
    @DisplayName("회원 비밀번호 업데이트 시 이메일이 존재하지 않는 경우 예외 발생")
    void updateMemberPassword_emailNotFound_throwsException() {
        // Arrange
        UpdatePasswordRequestDto updatePasswordRequestDto =
                new UpdatePasswordRequestDto("nonexistent@example.com", "123456", "newPassword");

        when(memberRepository.findByEmail(updatePasswordRequestDto.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        CustomException exception =
                assertThrows(CustomException.class, () -> memberService.updateMemberPassword(updatePasswordRequestDto));
        assertEquals(ErrorCode.EMAIL_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("이메일과 로그인 ID가 일치하는지 확인 성공")
    void validateMatchingLoginIdAndEmail_success() {
        // Arrange
        String email = "test@example.com";
        String loginId = "testUser";

        // spy 사용하지 않고 직접 모킹하는 방식
        MemberAccount memberAccount = MemberAccount.builder()
                .accountType(AccountType.REGISTERED)
                .id(loginId)
                .build();

        Member member = Member.builder().memberAccount(memberAccount)
                .email(email).build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));

        // Act & Assert
        assertDoesNotThrow(() -> memberService.validateMatchingLoginIdAndEmail(email, loginId));
    }

    @Test
    @DisplayName("이메일과 로그인 ID가 일치하지 않는 경우 예외 발생")
    void validateMatchingLoginIdAndEmail_mismatch_throwsException() {
        // Arrange
        String email = "test@example.com";
        String loginId = "testUser";

        // spy 사용하지 않고 직접 모킹하는 방식
        MemberAccount memberAccount = MemberAccount.builder()
                .accountType(AccountType.REGISTERED)
                .id("anotherId")
                .build();

        Member member = Member.builder().memberAccount(memberAccount)
                .email(email).build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class,
                () -> memberService.validateMatchingLoginIdAndEmail(email, loginId));
        assertEquals(ErrorCode.BAD_REQUEST, exception.getErrorCode());
    }

    @Test
    @DisplayName("회원 마지막 로그인 시간 업데이트 성공")
    void updateLastLoggedInAt_success() {
        // Arrange
        Long userId = 1L;
        Member member = Member.builder().id(userId).memberAccount(new MemberAccount()).build();
        when(memberRepository.findById(userId)).thenReturn(Optional.of(member));

        // Act
        memberService.updateLastLoggedInAt(userId);

        // Assert
        verify(memberRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("존재하지 않는 회원 ID로 마지막 로그인 시간 업데이트 시 예외 발생")
    void updateLastLoggedInAt_userNotFound_throwsException() {
        // Arrange
        Long userId = 1L;

        when(memberRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        CustomException exception =
                assertThrows(CustomException.class, () -> memberService.updateLastLoggedInAt(userId));
        assertEquals(ErrorCode.BAD_REQUEST, exception.getErrorCode());
    }

    @Test
    @DisplayName("회원 가입 성공 - GradePolicy 조회")
    void join_success_gradePolicy() {
        // Arrange
        JoinRequestDto joinRequestDto = new JoinRequestDto("test", "1234", "chan", "test@example.com", "01012345678",
                new Date(), Gender.MALE);

        GradePolicy gradePolicy = GradePolicy.builder()
                .grade(MemberGrade.GOLD)
                .min(5000)
                .rate(BigDecimal.valueOf(30))
                .build();

        when(gradePolicyRepository.findByGrade(any(MemberGrade.class))).thenReturn(Optional.of(gradePolicy));
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
    @DisplayName("회원 비밀번호 검증 성공")
    void verifyPassword_success() {
        // Arrange
        Long memberId = 1L;
        String password = "password123";
        String encodedPassword = "encodedPassword";

        MemberAccount account = MemberAccount.builder().password(encodedPassword).build();
        Member member = Member.builder().memberAccount(account).build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);

        // Act
        boolean result = memberService.verifyPassword(memberId, password);

        // Assert
        assertTrue(result);
        verify(memberRepository, times(1)).findById(memberId);
    }

    @Test
    @DisplayName("회원 정보 업데이트 성공")
    void updateMember_success() {
        // Arrange
        Long userId = 1L;
        MemberDto memberDto = MemberDto.builder()
                .email("updated@example.com")
                .phoneNumber("01098765432")
                .build();

        Member member = Member.builder()
                .email("original@example.com")
                .phone("01012345678")
                .build();

        when(memberRepository.findById(userId)).thenReturn(Optional.of(member));
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        // Act
        MemberDto result = memberService.updateMember(userId, memberDto);

        // Assert
        assertNotNull(result);
        assertEquals("updated@example.com", result.getEmail());
        verify(memberRepository, times(1)).save(member);
    }

    @Test
    @DisplayName("회원 등급 조회 테스트")
    void getMemberGrade_success() {
        // Arrange
        Long memberId = 1L;
        Member member = Member.builder().id(memberId).memberGrade(MemberGrade.REGULAR).build();
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        GradePolicy currentGrade = GradePolicy.builder()
                .grade(MemberGrade.REGULAR).min(0).max(10000)
                .build();
        GradePolicy nextGrade = GradePolicy.builder()
                .grade(MemberGrade.GOLD).min(10000).max(20000)
                .build();

        when(gradePolicyRepository.findCurrentGrade(anyInt())).thenReturn(currentGrade);
        when(gradePolicyRepository.findNextGrade(anyInt())).thenReturn(nextGrade);

        // Act
        MemberGradeDto result = memberService.getMemberGrade(memberId);

        // Assert
        assertNotNull(result);
        assertEquals("REGULAR", result.getCurrentGrade());
        assertEquals("GOLD", result.getNextGrade());
        verify(gradePolicyRepository, times(1)).findCurrentGrade(anyInt());
    }


    @Test
    @DisplayName("회원 조회 실패 - 존재하지 않는 회원")
    void getMember_notFound() {
        // Arrange
        Long userId = 1L;
        when(memberRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomException.class, () -> memberService.getMember(userId));
    }

    @Test
    @DisplayName("회원 삭제 성공")
    void deleteMember_success() {
        // Arrange
        Long memberId = 1L;
        when(memberRepository.existsById(memberId)).thenReturn(true);

        // Act
        memberService.deleteMember(memberId);

        // Assert
        verify(memberRepository, times(1)).deleteById(memberId);
    }

    @Test
    @DisplayName("회원 삭제 실패 - 회원 없음")
    void deleteMember_notFound() {
        // Arrange
        Long memberId = 1L;
        when(memberRepository.existsById(memberId)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> memberService.deleteMember(memberId));
        assertEquals("Member not found with id: " + memberId, exception.getMessage());
    }
    @Test
    @DisplayName("회원 등급 업데이트 성공")
    void updateMemberGrade_success() {
        // Arrange
        Member member = Member.builder()
                .id(1L)
                .memberGrade(MemberGrade.REGULAR)
                .build();

        GradePolicy gradePolicy = GradePolicy.builder()
                .min(0)
                .max(50000)
                .grade(MemberGrade.GOLD)
                .build();

        List<Member> members = List.of(member);

        // Mock 설정
        when(memberRepository.findAll()).thenReturn(members);
        when(orderFeignClient.getNetAmount(1L)).thenReturn(30000L); // 소비 금액 Mock 설정
        when(gradePolicyRepository.findAll()).thenReturn(List.of(gradePolicy)); // 등급 정책 Mock 설정

        // Act
        memberService.updateMemberGrade();

        // Assert
        assertEquals(MemberGrade.GOLD, member.getMemberGrade()); // 회원 등급 업데이트 확인
        verify(memberRepository, times(1)).findAll(); // 회원 조회 호출 검증
        verify(orderFeignClient, times(1)).getNetAmount(1L); // 소비 금액 호출 검증
    }

    @Test
    @DisplayName("회원 ID로 회원 조회 성공")
    void getMember_success() {
        // Arrange
        Long memberId = 1L;

        User user = User.builder()
                .name("Test User")
                .phoneNumber("01012345678")
                .points(100)
                .membership(MemberGrade.REGULAR)
                .build();

        // Member 객체 생성 및 설정
        Member member = Member.builder()
                .id(memberId)
                .email("test@example.com")
                .user(user) // User 설정
                .build();

        // Mock 설정
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // Act
        MemberDto result = memberService.getMember(memberId);

        // Assert
        assertNotNull(result); // 결과가 null이 아님을 확인
        assertEquals("test@example.com", result.getEmail()); // 이메일 확인
        assertEquals(100, result.getPoints()); // 포인트 확인
        verify(memberRepository, times(1)).findById(memberId); // ID로 조회 호출 검증
    }

    @Test
    @DisplayName("전화번호로 회원 계정 조회 성공")
    void getMemberAccountByPhoneNumber_success() {
        // Arrange
        String phoneNumber = "01012345678";
        MemberAccount memberAccount = MemberAccount.builder()
                .accountType(AccountType.REGISTERED)
                .build();
        Member member = Member.builder()
                .phone(phoneNumber)
                .memberAccount(memberAccount)
                .build();

        when(memberRepository.findByUserPhoneNumber(phoneNumber)).thenReturn(Optional.of(member)); // Mock 설정

        // Act
        MemberAccountInfo result = memberService.getMemberAccountByPhoneNumber(phoneNumber);

        // Assert
        assertNotNull(result); // 결과가 null이 아님을 확인
        verify(memberRepository, times(1)).findByUserPhoneNumber(phoneNumber); // 전화번호로 조회 호출 검증
    }


    @Test
    @DisplayName("회원 ID로 사용자 정보 조회 성공")
    void findMemberInfoByUserId_success() {
        // Arrange
        Long userId = 1L;

        // User 객체 생성 및 설정
        User user = User.builder()
                .name("Test User")
                .phoneNumber("01012345678")
                .build();

        // Member 객체 생성 및 설정
        Member member = Member.builder()
                .id(userId)
                .email("test@example.com")
                .user(user) // User 설정
                .build();

        // Mock 설정
        when(memberRepository.findByMemberAccount_Id(String.valueOf(userId)))
                .thenReturn(Optional.of(member));

        // Act
        MemberDto result = memberService.findMemberInfoByUserId(userId);

        // Assert
        assertNotNull(result); // 결과가 null이 아님을 확인
        assertEquals("test@example.com", result.getEmail()); // 이메일 확인
        assertEquals("01012345678", result.getPhoneNumber()); // 전화번호 확인
        verify(memberRepository, times(1)).findByMemberAccount_Id(String.valueOf(userId)); // 호출 검증
    }

    @Test
    @DisplayName("회원 정보 조회 성공")
    void getMemberInfo_success() {
        // Arrange
        Long memberId = 1L;
        Member member = Member.builder()
                .id(memberId)
                .email("test@example.com")
                .user(User.builder().points(100).build())
                .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // Act
        MemberDto result = memberService.getMemberInfo(memberId);

        // Assert
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals(100, result.getPoints());
        verify(memberRepository, times(1)).findById(memberId);
    }



    
}