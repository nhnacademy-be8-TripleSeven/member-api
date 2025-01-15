package com.example.msamemberapi.application.controller;

import com.example.msamemberapi.application.dto.request.MemberUpdateRequestDto;
import com.example.msamemberapi.application.dto.response.MemberDto;
import com.example.msamemberapi.application.dto.response.MemberGradeDto;
import com.example.msamemberapi.application.dto.response.MemberGradeHistoryDto;
import com.example.msamemberapi.application.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
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
    public ResponseEntity<MemberDto> getMemberInfo(@RequestHeader("X-USER") Long userId) {
        MemberDto memberInfo = memberService.getMember(userId);
        return ResponseEntity.ok(memberInfo);
    }
    @Operation(summary = "멤버 삭제", description = "멤버 ID를 통해 멤버를 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "멤버 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "해당 멤버를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestParam Long memberId) {
        memberService.quitMember(memberId);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "멤버 조회", description = "멤버 ID를 통해 멤버 정보를 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "멤버 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 멤버를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MemberDto> getMemberById(@PathVariable Long id) {
        MemberDto member = memberService.findMemberInfoByUserId(String.valueOf(id));
        return ResponseEntity.ok(member);
    }


    @Operation(summary = "멤버 수정", description = "멤버 ID를 통해 멤버 정보를 수정합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "멤버 수정 성공"),
            @ApiResponse(responseCode = "404", description = "해당 멤버를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateMember(
            @PathVariable Long id,
            @RequestBody MemberUpdateRequestDto requestDto) {
        memberService.updateMemberInfo(String.valueOf(id), requestDto.toMemberDto());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원 등급 조회", description = "회원의 현재 등급과 다음 등급 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 등급 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 멤버를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/{id}/grade")
    public ResponseEntity<MemberGradeDto> getMemberGrade(@PathVariable Long id) {
        MemberGradeDto gradeDto = memberService.getMemberGrade(id);
        return ResponseEntity.ok(gradeDto);
    }

    @Operation(summary = "회원 등급 변경 히스토리 조회", description = "회원의 등급 변경 히스토리를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 등급 변경 히스토리 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 멤버를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/{id}/grade-history")
    public ResponseEntity<List<MemberGradeHistoryDto>> getGradeHistory(@PathVariable Long id) {
        List<MemberGradeHistoryDto> history = memberService.getGradeHistory(id);
        return ResponseEntity.ok(history);
    }
}
