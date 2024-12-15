package com.example.msamemberapi.application.dto.response;

import com.example.msamemberapi.application.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public class MemberAuthInfo {

    private String id;
    private String password;
    private List<String> roles;

    public MemberAuthInfo(Member member) {
        this.id = member.getId();
        this.password = member.getPassword();
        this.roles = new ArrayList<>(member.getRoles());
    }
}
