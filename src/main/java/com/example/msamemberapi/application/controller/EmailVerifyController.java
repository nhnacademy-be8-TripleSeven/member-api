package com.example.msamemberapi.application.controller;

import com.example.msamemberapi.application.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Email Verify", description = "이메일 인증 Api")
@RestController
@RequestMapping("/members/verify/emails")
@RequiredArgsConstructor
public class EmailVerifyController {

    private final EmailService emailService;

    @Operation(summary = "이메일 인증번호 전송", description = "회원가입 시 작성한 이메일에 인증요청 보내기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일에 인증번호를 성공적으로 보냄"),
            @ApiResponse(responseCode = "400", description = "잘못된 이메일 형식"),
            @ApiResponse(responseCode = "409", description = "이미 가입된 이메일"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @PostMapping("/{email}")
    public ResponseEntity<Void> sendVerificationCode(@PathVariable String email) {
        emailService.sendVerifyCode(email);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "이메일 인증", description = "회원가입 시 작성한 이메일의 6자리 코드 인증")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증번호 일치"),
            @ApiResponse(responseCode = "400", description = "잘못된 이메일, 인증 코드 형식"),
            @ApiResponse(responseCode = "401", description = "코드 불일치")
    })
    @PostMapping("/{email}/{verificationCode}")
    public ResponseEntity<Void> verify(@PathVariable String email, @PathVariable String verificationCode) {
        emailService.verify(email, verificationCode);
        return ResponseEntity.ok().build();
    }
}
