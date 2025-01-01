package com.example.msamemberapi.application.controller;

import com.example.msamemberapi.application.dto.request.MemberUpdateRequestDto;
import com.example.msamemberapi.application.dto.response.MemberDto;
import com.example.msamemberapi.application.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Member", description = "회원 Api")
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "멤버 정보 조회", description = "마이페이지에서 멤버 상세 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "멤버 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "해당 멤버를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/info")
    public MemberDto getMemberInfo(@RequestHeader("X-USER") Long userId) {
        return null;
    }


    @Operation(summary = "멤버 삭제", description = "멤버 ID를 통해 멤버를 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "멤버 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "해당 멤버를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestParam Long memberId) {
        memberService.deleteByMemberId(memberId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-password")
    public ResponseEntity<Void> verifyPassword(@RequestParam String userId, @RequestParam String password) {
        boolean isVerified = memberService.verifyPassword(userId, password);
        if (!isVerified) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원 정보 수정", description = "회원 정보를 업데이트합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 정보 수정 성공"),
            @ApiResponse(responseCode = "404", description = "해당 멤버를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })

    @PostMapping("/update")
    public ResponseEntity<Void> updateMember(@RequestBody MemberUpdateRequestDto requestDto) {
        memberService.updateMember(requestDto);
        return ResponseEntity.ok().build();
    }
}



