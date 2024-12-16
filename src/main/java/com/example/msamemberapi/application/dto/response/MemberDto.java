package com.example.msamemberapi.application.dto.response;

import com.example.msamemberapi.application.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public class MemberDto {

    private Long id;
    private List<String> roles;

    public MemberDto(Member member) {
        this.id = member.getId();
        this.roles = new ArrayList<>(member.getRoles());
    }
}
