package com.example.msamemberapi.application.dto.request;

import com.example.msamemberapi.application.dto.response.MemberDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class MemberUpdateRequestDto {

    private String email;
    private String phoneNumber;
    private String postcode;
    private String address;
    private String detailAddress;
    private String newPassword;


    public MemberDto toMemberDto() {
        return MemberDto.builder()
                .email(this.email)
                .phoneNumber(this.phoneNumber)
                .postcode(this.postcode)
                .address(this.address)
                .detailAddress(this.detailAddress)
                .password(this.newPassword)
                .build();
    }
}