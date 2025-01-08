package com.example.msamemberapi.application.controller;

import com.example.msamemberapi.application.dto.request.JoinRequestDto;
import com.example.msamemberapi.application.dto.request.UpdatePasswordRequestDto;
import com.example.msamemberapi.application.dto.response.MemberAccountInfo;
import com.example.msamemberapi.application.dto.response.MemberAuthInfo;
import com.example.msamemberapi.application.dto.response.MemberDto;
import com.example.msamemberapi.application.feign.BookFeignClient;
import com.example.msamemberapi.application.service.EmailService;
import com.example.msamemberapi.application.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Member-Account", description = "계정 Api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class AccountController {

    private final MemberService memberService;
    private final BookFeignClient bookFeignClient;
    private final EmailService emailService;

    @Operation(summary = "멤버 생성", description = "회원가입 시 받은 정보로 멤버 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "멤버 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 로그인아이디, 핸드폰번호, 이메일")
    })
    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody JoinRequestDto joinRequestDto) {
        emailService.validateEmailIsVerified(joinRequestDto.getEmail());
        MemberDto memberDto = memberService.join(joinRequestDto);
        bookFeignClient.createWelcomeCoupon(memberDto.getId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "멤버 인증 정보 조회", description = "로그인 ID를 통해 멤버 인증 정보를 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 멤버를 찾을 수 없음")
    })
    @GetMapping("/auth/login-id")
    public MemberAuthInfo getMemberAuthInfo(@RequestParam String loginId) {
        return memberService.findByMemberId(loginId);
    }

    @Operation(summary = "멤버 인증 정보 조회", description = "멤버 id를 통해 멤버 인증 정보를 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 멤버를 찾을 수 없음")
    })
    @GetMapping("/auth/id")
    public MemberAuthInfo getMemberAuthInfo(@RequestParam Long memberId) {
        return memberService.findByMemberId(memberId);
    }

    @Operation(summary = "멤버 계정 아이디 조회", description = "휴대폰 번호를 통해 멤버 계정 아이디 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "계정 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 정보를 찾을 수 없음")
    })
    @GetMapping("/account-id/phone")
    public MemberAccountInfo getMemberAccountFromPhoneNumber(@RequestParam String phoneNumber) {
        return memberService.getMemberAccountByPhoneNumber(phoneNumber);
    }

    @Operation(summary = "멤버 계정 아이디 조회", description = "이메일을 통해 멤버 계정 아이디 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "계정 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 정보를 찾을 수 없음")
    })
    @GetMapping("/account-id/email")
    public MemberAccountInfo getMemberAccountFromEmail(@RequestParam String email) {
        return memberService.getMemberAccountByEmail(email);
    }

    @Operation(summary = "비밀번호 변경 요청", description = "이메일에 비밀번호 변경 URL 전송")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일과 로그인 아이디 일치, 성공적으로 이메일을 보냄"),
            @ApiResponse(responseCode = "400", description = "이메일과 로그인 아이디가 일치하지 않음"),
            @ApiResponse(responseCode = "404", description = "이메일을 찾을 수 없음")
    })
    @PostMapping("/reset/password")
    public ResponseEntity<Void> sendResetPasswordEmail(@RequestParam String email, @RequestParam String loginId) {
        memberService.validateMatchingLoginIdAndEmail(email, loginId);
        emailService.sendPasswordResetEmail(email);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "비밀번호 변경", description = "인증 코드를 확인 후 일치할 시 비밀번호 변경")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증번호 일치, 비밀번호 변경 성공"),
            @ApiResponse(responseCode = "401", description = "인증 코드 불일치"),
            @ApiResponse(responseCode = "404", description = "회원가입 한 이메일을 찾을 수 없음")
    })
    @PutMapping("/reset/password")
    public ResponseEntity<Void> updatePassword(@Valid @RequestBody UpdatePasswordRequestDto updatePasswordRequestDto) {
        emailService.validateResetPasswordCode(updatePasswordRequestDto.getEmail(), updatePasswordRequestDto.getCode());
        memberService.updateMemberPassword(updatePasswordRequestDto);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "회원 로그인", description = "회원 로그인 시 마지막 로그인 시간 변경")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "회원을 찾을 수 없음")
    })
    @PostMapping("/login")
    public ResponseEntity<Void> updateMemberLoggedInAt(@RequestHeader("X-USER") Long userId) {
        memberService.updateLastLoggedInAt(userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "휴면 계정 해제 코드 발송", description = "휴면 계정을 해제하는 이메일 인증코드 발송")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @PostMapping("/unlock/account")
    public ResponseEntity<Void> sendAccountActiveEmail(@RequestParam String loginId) {
        emailService.sendAccountActiveEmail(loginId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "휴면 계정 해제", description = "코드를 검증하고 계정의 휴면 상태 해제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "인증 코드 불일치")
    })
    @PostMapping("/unlock/account/verify")
    public ResponseEntity<Void> verifyAccountActiveCode(@RequestParam String email, @RequestParam String code) {
        emailService.verifyAccountActiveCode(email, code);
        return ResponseEntity.ok().build();
    }

}
