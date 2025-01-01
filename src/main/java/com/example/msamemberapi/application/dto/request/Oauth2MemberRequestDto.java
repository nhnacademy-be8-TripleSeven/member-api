package com.example.msamemberapi.application.dto.request;

import com.example.msamemberapi.application.entity.Member;
import com.example.msamemberapi.application.entity.MemberAccount;
import com.example.msamemberapi.application.entity.User;
import com.example.msamemberapi.application.enums.AccountType;
import com.example.msamemberapi.application.enums.Gender;
import com.example.msamemberapi.application.enums.MemberGrade;
import com.example.msamemberapi.application.enums.MemberRole;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Oauth2MemberRequestDto {

    @NotEmpty
    private String idNo;
    @NotEmpty
    private String email;
    @NotEmpty
    private String mobile;
    @NotEmpty
    private String name;
    @NotEmpty
    private String genderCode;
    private String birthdayMMdd;

    public Member toPaycoMemberEntity() {

        User user = User.builder()
                .phoneNumber(mobile)
                .name(name)
                .build();

        MemberAccount memberAccount = MemberAccount.builder()
                .id(idNo)
                .accountType(AccountType.PAYCO)
                .build();

        Member member = Member.builder()
                .user(user)
                .memberAccount(memberAccount)
                .email(email)
                .gender(Gender.valueOf(genderCode))
                .memberGrade(MemberGrade.REGULAR)
                .build();

        member.addRole(MemberRole.PAYCO);
        return member;
    }

}
