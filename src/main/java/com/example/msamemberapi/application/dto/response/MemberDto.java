package com.example.msamemberapi.application.dto.response;

import com.example.msamemberapi.application.entity.Member;
import com.example.msamemberapi.application.entity.MemberAccount;
import com.example.msamemberapi.application.entity.MemberGradeHistory;
import com.example.msamemberapi.application.enums.Gender;
import com.example.msamemberapi.application.enums.MemberGrade;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.info.Contact;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class MemberDto {

    private Long id;
    private String email;
    private String phoneNumber;
    private String name;
    private Date birth;
    private String gender;
    private String memberGrade;

    private String password;
    private String postcode;
    private String address;
    private String detailAddress;
    private Integer points;

//
//    public MemberDto(Member member) {
//        this.id = member.getId();
//        this.email = member.getEmail();
//        this.phoneNumber = member.getUser().getPhoneNumber();
//        this.name = member.getUser().getName();
//        this.birth = member.getBirth();
//        this.gender = member.getGender().name();
//        this.memberGrade = member.getMemberGrade().name();
//    }

    @QueryProjection
    public MemberDto(Long id, String email, String phoneNumber, String name, Date birth,
                     Gender gender, MemberGrade memberGrade, Integer points) {
        this.id = id;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.birth = birth;
        this.gender = gender != null ? gender.name() : "UNKNOWN";
        this.memberGrade = memberGrade != null ? memberGrade.name() : "REGULAR";
        this.points = points != null ? points : 0;
    }

    public MemberDto(Member member) {
        this.id = member.getId();
        this.email = member.getEmail();
        this.phoneNumber = member.getUser().getPhoneNumber();
        this.name = member.getUser().getName();
        this.birth = member.getBirth();
        this.gender = member.getGender() != null ? member.getGender().name() : "UNKNOWN";
        this.memberGrade = member.getMemberGrade() != null ? member.getMemberGrade().name() : "REGULAR";
        this.points = member.getUser() != null ? member.getUser().getPoints() : 0;
    }



    public static MemberDto fromEntity(Member member) {
        return MemberDto.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .memberGrade(member.getMemberGrade() != null ? member.getMemberGrade().name() : "REGULAR")
                .points(member.getUser() != null ? member.getUser().getPoints() : 0)
                .build();
    }
}
