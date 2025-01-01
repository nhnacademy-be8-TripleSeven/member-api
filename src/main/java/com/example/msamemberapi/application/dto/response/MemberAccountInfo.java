package com.example.msamemberapi.application.dto.response;

import com.example.msamemberapi.application.entity.MemberAccount;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MemberAccountInfo {

    private String loginId;
    private String password;

    public MemberAccountInfo(MemberAccount memberAccount) {
        this.loginId = memberAccount.getId();
        this.password = memberAccount.getPassword();
    }
}
