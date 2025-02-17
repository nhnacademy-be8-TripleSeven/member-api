package com.example.msamemberapi.application.controller;

import com.example.msamemberapi.application.dto.request.Oauth2MemberRequestDto;
import com.example.msamemberapi.application.dto.response.MemberAccountInfo;
import com.example.msamemberapi.application.service.impl.PaycoMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/members/oauth2")
@RequiredArgsConstructor
public class Oauth2AccountController {

    private final PaycoMemberService paycoMemberService;

    @PostMapping("/payco")
    public MemberAccountInfo savePaycoMember(@RequestBody Oauth2MemberRequestDto oauth2MemberRequestDto) {
        return paycoMemberService.saveMemberDetail(oauth2MemberRequestDto);
    }
}
