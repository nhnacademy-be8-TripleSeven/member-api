package com.example.msamemberapi.application.entity;

import com.example.msamemberapi.application.enums.Gender;
import com.example.msamemberapi.application.enums.MemberGrade;
import com.example.msamemberapi.application.enums.MemberRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    private MemberAccount memberAccount;

    @Column(unique = true, nullable = false)
    private String email;

    private Date birth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private MemberGrade memberGrade;

    @OneToOne(fetch = FetchType.LAZY)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private User user;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<MemberGradeHistory> gradeHistories = new ArrayList<>();


    @ElementCollection(fetch = FetchType.LAZY) @Builder.Default
    private List<String> roles = new ArrayList<>();

    public void addRole(MemberRole memberRole) {
        roles.add(memberRole.toString());
    }

    public void addGradeHistory(MemberGradeHistory gradeHistory) {
        this.gradeHistories.add(gradeHistory);
    }
}