package com.example.msamemberapi.application.dto.response;

import com.example.msamemberapi.application.entity.Member;
import com.example.msamemberapi.application.entity.MemberAccount;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public class MemberAuthInfo {

    private Long id;
    private MemberAccount memberAccount;
    private List<String> roles;

    public MemberAuthInfo(Member member) {
        this.id = member.getId();
        this.memberAccount = member.getMemberAccount();
        this.roles = new ArrayList<>(member.getRoles());
    }
}
