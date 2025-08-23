package com.example.msamemberapi.application.controller;

import com.example.msamemberapi.application.dto.request.GradeUpdateRequestDto;
import com.example.msamemberapi.application.dto.request.MemberUpdateRequestDto;
import com.example.msamemberapi.application.dto.response.MemberDto;
import com.example.msamemberapi.application.dto.response.MemberGradeDto;
import com.example.msamemberapi.application.enums.MemberGrade;
import com.example.msamemberapi.application.service.AddressService;
import com.example.msamemberapi.application.service.GradePolicyService;
import com.example.msamemberapi.application.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Member", description = "회원 Api")
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private GradePolicyService gradePolicyService;
    private final PasswordEncoder passwordEncoder;
    private final AddressService addressService;


    @Operation(summary = "멤버 정보 조회", description = "마이페이지에서 멤버 상세 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "멤버 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "해당 멤버를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/info")
    public ResponseEntity<MemberDto> getMemberInfo(@RequestHeader("X-USER") Long userId) {
        MemberDto memberInfo = memberService.getMemberInfo(userId);
        return ResponseEntity.ok(memberInfo);
    }


    @Operation(summary = "멤버 삭제", description = "멤버 ID를 통해 멤버를 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "멤버 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "해당 멤버를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestHeader("X-USER") Long userId) {
        memberService.quitMember(userId);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "멤버 조회", description = "멤버 ID를 통해 멤버 정보를 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "멤버 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 멤버를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MemberDto> getMemberById(@PathVariable Long id) {
        MemberDto member = memberService.findMemberInfoByUserId(id);
        return ResponseEntity.ok(member);
    }


    @Operation(summary = "멤버 수정", description = "멤버 ID를 통해 멤버 정보를 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "멤버 수정 성공"),
            @ApiResponse(responseCode = "404", description = "해당 멤버를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @PutMapping("/{userId}/edit")
    public ResponseEntity<MemberDto> updateMember(
            @PathVariable Long userId,
            @RequestBody MemberUpdateRequestDto requestDto) {
        MemberDto memberDto = requestDto.toMemberDto();
        MemberDto updatedMember = memberService.updateMember(userId, requestDto.toMemberDto());
        return ResponseEntity.ok(updatedMember);
    }

    @GetMapping("/verify-password")
    public String verifyPasswordPage(Model model, @RequestHeader("X-USER") String userId) {
        MemberDto memberDto = memberService.findMemberInfoByUserId(Long.valueOf(userId));
        model.addAttribute("user", memberDto);
        return "verify-password";
    }



    @Operation(summary = "비밀번호 검증", description = "사용자가 입력한 비밀번호를 검증")
    @PostMapping("/{userId}/verify-password")
    public ResponseEntity<?> checkPassword(
            @PathVariable Long userId,
            @RequestBody Map<String, String> payload) {
        String password = payload.get("password");
        boolean isValid = memberService.verifyPassword(userId, password);
        if (!isValid) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("비밀번호가 틀렸습니다.");
        }

        return ResponseEntity.ok("비밀번호 검증 성공");
    }


    @Operation(summary = "멤버 정보 수정", description = "사용자가 자신의 정보를 수정")
    @PostMapping("/edit")
    public ResponseEntity<Void> updateMemberInfo(
            @RequestHeader("X-USER") String userId,
            @RequestBody MemberUpdateRequestDto memberDto) {
        memberService.updateMemberInfo(Long.valueOf(userId), memberDto.toMemberDto());
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "회원 등급 변경", description = "회원 등급을 변경합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "등급 변경 성공"),
            @ApiResponse(responseCode = "404", description = "회원 정보 없음")
    })
    @PutMapping("/updateGrade/{id}")
    public ResponseEntity<MemberGradeDto> updateGrade(
            @PathVariable Long id,
            @RequestBody @Valid GradeUpdateRequestDto gradeUpdateRequestDto) {

        MemberGrade memberGrade = gradeUpdateRequestDto.getGrade();
        MemberGradeDto memberGradeDto = gradePolicyService.updateGrade(id, memberGrade);
        return ResponseEntity.ok().body(memberGradeDto);
    }


   }