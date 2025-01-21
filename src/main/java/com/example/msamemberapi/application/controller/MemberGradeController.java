package com.example.msamemberapi.application.controller;

import com.example.msamemberapi.application.dto.request.GradeRequestDto;
import com.example.msamemberapi.application.dto.request.GradeUpdateRequestDto;
import com.example.msamemberapi.application.dto.response.MemberGradeDto;
import com.example.msamemberapi.application.entity.GradePolicy;
import com.example.msamemberapi.application.enums.MemberGrade;
import com.example.msamemberapi.application.repository.GradePolicyRepository;
import com.example.msamemberapi.application.service.GradePolicyService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequiredArgsConstructor
@RestController
@RequestMapping
@Tag(name = "Grade", description = "회원 등급 관리 API")
public class MemberGradeController {

    private final GradePolicyService gradePolicyService;
    private final GradePolicyRepository gradePolicyRepository;  // 리포지토리 주입

    @Operation(summary = "등급 목록 조회", description = "모든 등급 정책을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "등급 목록 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/api/members/grades")
    public ResponseEntity<List<MemberGradeDto>> getGrades() {
        List<MemberGradeDto> memberGradeDtoList = gradePolicyService.getAllGrades();
        return ResponseEntity.ok(memberGradeDtoList);
    }

    @Operation(summary = "회원 등급 조회", description = "회원 ID를 통해 회원의 등급 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 등급 조회 성공"),
            @ApiResponse(responseCode = "404", description = "회원 정보 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/api/members/grade")
    public ResponseEntity<MemberGradeDto> getGradeByMemberId(
            @RequestHeader("X-USER") Long userId
    ) {
        MemberGradeDto memberGradeDto = gradePolicyService.getGradeById(userId);
        return ResponseEntity.ok().body(memberGradeDto);
    }

    @Operation(summary = "새 등급 생성", description = "새로운 등급 정책을 추가합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "등급 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @PostMapping("/admin/members/grade")
    public ResponseEntity<MemberGradeDto> createGrade(@RequestBody @Valid GradeRequestDto gradeRequestDto) {
        MemberGradeDto memberGradeDto = gradePolicyService.createGrade(
                gradeRequestDto.getName(),
                MemberGrade.valueOf(gradeRequestDto.getName().toUpperCase()),
                gradeRequestDto.getDescription(),
                gradeRequestDto.getRate(),
                gradeRequestDto.getMin(),
                gradeRequestDto.getMax()
        );
        return ResponseEntity.status(201).body(memberGradeDto);
    }

    @Operation(summary = "등급 업데이트", description = "기존 등급 정책을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "등급 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "등급을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @PutMapping("/admin/members/grade/{id}")
    public ResponseEntity<MemberGradeDto> updateGrade(
            @PathVariable Long id,
            @RequestBody @Valid GradeUpdateRequestDto gradeUpdateRequestDto) {

        MemberGrade memberGrade = gradeUpdateRequestDto.getGrade();
        MemberGradeDto memberGradeDto = gradePolicyService.updateGrade(id, memberGrade);

        return ResponseEntity.ok().body(memberGradeDto);
    }

    @Operation(summary = "등급 삭제", description = "기존 등급 정책을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "등급 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "등급을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @DeleteMapping("/admin/members/grade/{id}")
    public ResponseEntity<String> deleteGrade(@PathVariable Long id) {
        gradePolicyService.deleteGrade(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "회원 등급 계산", description = "회원의 소비 금액에 따라 회원 등급을 계산합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 등급 계산 성공"),
            @ApiResponse(responseCode = "404", description = "회원 정보 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/members/grade/point")
    public ResponseEntity<Long> getMemberGrade(@RequestParam Long userId,
                                               @RequestParam Long amount) {
        Long point = gradePolicyService.calculateMemberGrade(userId, amount);
        return ResponseEntity.ok(point);
    }
}