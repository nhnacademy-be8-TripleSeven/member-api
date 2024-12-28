package com.example.msamemberapi.application.controller;

import com.example.msamemberapi.application.dto.request.JoinRequestDto;
import com.example.msamemberapi.application.dto.response.MemberAccountInfo;
import com.example.msamemberapi.application.dto.response.MemberAuthInfo;
import com.example.msamemberapi.application.service.EmailVerifyService;
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
    private final EmailVerifyService emailVerifyService;

    @Operation(summary = "멤버 생성", description = "회원가입 시 받은 정보로 멤버 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "멤버 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 로그인아이디, 핸드폰번호, 이메일")
    })
    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody JoinRequestDto joinRequestDto) {
        emailVerifyService.validateEmailIsVerified(joinRequestDto.getEmail());
        memberService.join(joinRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "멤버 인증 정보 조회", description = "로그인 ID를 통해 멤버 인증 정보를 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 멤버를 찾을 수 없음")
    })

    @GetMapping("/auth")
    public MemberAuthInfo getMemberAuthInfo(@RequestParam String loginId) {
        return memberService.findByMemberId(loginId);
    }

    @Operation(summary = "멤버 계정 정보 조회", description = "휴대폰 번호를 통해 멤버 계정 정보를 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "계정 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 정보를 찾을 수 없음")
    })

    @GetMapping("/account/phone")
    public MemberAccountInfo getMemberAccountFromPhoneNumber(@RequestParam String phoneNumber) {
        return memberService.getMemberAccountByPhoneNumber(phoneNumber);
    }

    @Operation(summary = "멤버 계정 정보 조회", description = "이메일을 통해 멤버 계정 정보를 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "계정 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 정보를 찾을 수 없음")
    })
    @GetMapping("/account/email")
    public MemberAccountInfo getMemberAccountFromEmail(@RequestParam String email) {
        return null;
    }
}
