package com.example.msamemberapi.application.controller;

import com.example.msamemberapi.application.dto.response.MemberDto;
import com.example.msamemberapi.application.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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


    @Operation(summary = "멤버 탈퇴", description = "맴버 계정 탈퇴 (QUIT) 처리")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "멤버 탈퇴 성공"),
            @ApiResponse(responseCode = "404", description = "해당 멤버를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @DeleteMapping
    public ResponseEntity<Void> quit(@RequestHeader("X-USER") Long userId) {
        memberService.deleteByMemberId(userId);
        return ResponseEntity.ok().build();
    }



}

