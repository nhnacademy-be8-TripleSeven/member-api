package com.example.msamemberapi.application.controller;

import com.example.msamemberapi.application.dto.response.MemberDto;
import com.example.msamemberapi.application.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Member Manage", description = "관리자가 멤버를 관리하는 api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/members")
public class MemberManageController {

    private final MemberService memberService;

    @Operation(summary = "멤버 리스트 조회", description = "관리자페이지에서 페이징을 지원하는 멤버 리스트 조회. 이름 검색, 정렬 기능 제공")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "멤버 리스트 반환"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @GetMapping
    public List<MemberDto> getMembers(@RequestParam(required = false) String name, Pageable pageable) {
        return memberService.getMembers(name, pageable).stream().toList();
    }

    @Operation(summary = "멤버 이름으로 검색한 리스트 조회", description = "검색한 문자열이 포함된 이름을 가진 멤버의 리스트 반환. 페이징 지원")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "멤버 리스트 반환"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @GetMapping("/name/{userName}")
    public List<MemberDto> searchMembersByName(@PathVariable String userName, Pageable pageable) {
        return null;
    }

    @Operation(summary = "멤버 이메일로 검색한 리스트 조회", description = "검색한 문자열이 포함된 이메일을 가진 멤버의 리스트 반환. 페이징 지원")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "멤버 리스트 반환"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @GetMapping("/email/{userEmail}")
    public List<MemberDto> searchMembersByEmail(@PathVariable String userEmail, Pageable pageable) {
        return null;
    }
}
