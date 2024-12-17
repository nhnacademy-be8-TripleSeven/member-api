package com.example.msamemberapi.application.dto.response;

import com.example.msamemberapi.application.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MemberAccountInfo {

    private String loginId;
    private String password;

    public MemberAccountInfo(Member member) {
        this.loginId = member.getMemberAccount().getId();
        this.password = member.getMemberAccount().getPassword();
    }
}
