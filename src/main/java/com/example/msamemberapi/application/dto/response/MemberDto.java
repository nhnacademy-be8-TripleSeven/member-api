package com.example.msamemberapi.application.dto.response;

import com.example.msamemberapi.application.entity.Member;
import com.example.msamemberapi.application.entity.MemberAccount;
import com.example.msamemberapi.application.entity.MemberGradeHistory;
import com.example.msamemberapi.application.enums.Gender;
import com.example.msamemberapi.application.enums.MemberGrade;
import com.querydsl.core.annotations.QueryProjection;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
public class MemberDto {

    private Long id;
    private String email;
    private String phoneNumber;
    private String name;
    private Date birth;
    private Gender gender;
    private MemberGrade memberGrade;

    public MemberDto(Member member) {
        this.id = member.getId();
        this.email = member.getEmail();
        this.phoneNumber = member.getUser().getPhoneNumber();
        this.name = member.getUser().getName();
        this.birth = member.getBirth();
        this.gender = member.getGender();
        this.memberGrade = member.getMemberGrade();
    }

    @QueryProjection
    public MemberDto(Long id, String email, String phoneNumber, String name, Date birth,
                     Gender gender, MemberGrade memberGrade) {
        this.id = id;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.birth = birth;
        this.gender = gender;
        this.memberGrade = memberGrade;
    }
}
