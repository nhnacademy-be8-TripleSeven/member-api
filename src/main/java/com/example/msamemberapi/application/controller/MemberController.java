package com.example.msamemberapi.application.controller;

import com.example.msamemberapi.application.dto.request.JoinRequestDto;
import com.example.msamemberapi.application.dto.response.MemberAuthInfo;
import com.example.msamemberapi.application.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/members")
    public ResponseEntity join(@RequestBody JoinRequestDto joinRequestDto) {
        memberService.join(joinRequestDto);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/members")
    public MemberAuthInfo getMemberAuthInfo(@RequestParam String id) {
        return memberService.findByMemberId(id);
    }

    @DeleteMapping("/api/members")
    public ResponseEntity delete(@RequestParam String id) {
        memberService.deleteByMemberId(id);
        return ResponseEntity.ok(null);
    }


}