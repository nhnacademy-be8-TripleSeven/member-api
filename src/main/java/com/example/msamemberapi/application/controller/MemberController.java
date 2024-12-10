package com.example.msamemberapi.application.controller;

import com.example.msamemberapi.application.entity.Member;
import com.example.msamemberapi.application.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/members")
    public ResponseEntity join(@RequestBody Member member) {
        Member join = memberService.join(member);
        return ResponseEntity.ok(join);
    }

    @GetMapping("/members")
    public Member getMemberById(@RequestParam String id) {
        return memberService.findByMemberId(id);
    }

    @DeleteMapping("/api/members")
    public ResponseEntity delete(@RequestParam String id) {
        memberService.deleteByMemberId(id);
        return ResponseEntity.ok(null);
    }


}