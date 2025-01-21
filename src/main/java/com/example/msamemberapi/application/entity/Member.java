package com.example.msamemberapi.application.entity;

import com.example.msamemberapi.application.enums.Gender;
import com.example.msamemberapi.application.enums.MemberGrade;
import com.example.msamemberapi.application.enums.MemberRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @OneToOne(cascade = CascadeType.ALL) // Cascade 옵션 확인
    private User user;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<MemberGradeHistory> gradeHistories = new ArrayList<>();

    private String name;

    private String phone;
    private String postcode;
    private String address;
    private String detailAddress;


    @ElementCollection(fetch = FetchType.LAZY) @Builder.Default
    private List<String> roles = new ArrayList<>();


    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberAddress> memberAddresses = new ArrayList<>();

    private String password;

    public void addRole(MemberRole memberRole) {
        roles.add(memberRole.toString());
    }

    public void removeRole(MemberRole memberRole) {
        for (int i = 0; i < this.roles.size(); i++) {
            String role = this.roles.get(i);
            if (role.contains(memberRole.toString())) {
                this.roles.remove(i);
                return;
            }
        }

    }

    public void addGradeHistory(MemberGradeHistory gradeHistory) {
        this.gradeHistories.add(gradeHistory);
    }
    public Member(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }


    public void update(String email, String phoneNumber, String address, String detailAddress, String password) {
        if (email != null && !email.isBlank()) {
            validateEmail(email);
            this.email = email;
        }
        if (phoneNumber != null && !phoneNumber.isBlank()) {
            this.phone = phoneNumber;
        }
        if (address != null) {
            this.address = address;
        }
        if (detailAddress != null) {
            this.detailAddress = detailAddress;
        }
        if (password != null && !password.isBlank()) {
            this.password = password;
        }
    }

    // 이메일 유효성 검사
    private void validateEmail(String email) {
        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            throw new IllegalArgumentException("유효하지 않은 이메일 형식입니다.");
        }
    }

    // 기본 주소 생성
    public MemberAddress createMemberAddress(Address address, String alias, Boolean isDefault) {
        if (address == null || alias == null || alias.isBlank() || isDefault == null) {
            throw new IllegalArgumentException("주소와 별칭, 기본 주소 여부는 필수 입력 항목입니다.");
        }
        return MemberAddress.builder()
                .member(this)
                .address(address)
                .alias(alias.trim())
                .isDefault(isDefault)
                .build();
    }


    public void updateGrade(MemberGrade grade) {
        this.memberGrade = grade;
    }

}