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
    private List<MemberAddress> memberAddresses = new ArrayList<>();
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<Address> addresses = new ArrayList<>();

    
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

    public void updateEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("이메일은 비어 있을 수 없습니다.");
        }
        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            throw new IllegalArgumentException("유효하지 않은 이메일 형식입니다.");
        }

        this.email = email;
    }

    public Member withUpdatedPhoneNumber(String phoneNumber) {
        if (phoneNumber != null && !phoneNumber.isBlank()) {
            return Member.builder()
                    .id(this.id)
                    .email(this.email)
                    .memberAccount(this.memberAccount)
                    .user(this.user.withUpdatedPhoneNumber(phoneNumber))
                    .build();
        }
        return this;
    }

    public void updatePassword(String password) {
        if (this.memberAccount == null) {
            throw new IllegalStateException("연결된 계정이 없습니다.");
        }
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("비밀번호는 최소 8자 이상이어야 합니다.");
        }
        this.memberAccount.updatePassword(password);
    }

    public Member withUpdatedName(String name) {
        if (name != null && !name.isBlank()) {
            return Member.builder()
                    .id(this.id)
                    .email(this.email)
                    .memberAccount(this.memberAccount)
                    .user(this.user)
                    .birth(this.birth)
                    .gender(this.gender)
                    .memberGrade(this.memberGrade)
                    .postcode(this.postcode)
                    .address(this.address)
                    .detailAddress(this.detailAddress)
                    .gradeHistories(this.gradeHistories)
                    .roles(this.roles)
                    .build();
        }
        return this;
    }

    public Member withUpdatedEmail(String email) {
        if (email != null && !email.isBlank()) {
            return Member.builder()
                    .id(this.id)
                    .email(email)
                    .memberAccount(this.memberAccount)
                    .user(this.user)
                    .birth(this.birth)
                    .gender(this.gender)
                    .memberGrade(this.memberGrade)
                    .postcode(this.postcode)
                    .address(this.address)
                    .detailAddress(this.detailAddress)
                    .gradeHistories(this.gradeHistories)
                    .roles(this.roles)
                    .build();
        }
        return this;
    }

    public void updateUserDetails(String phoneNumber, String name) {
        if (this.user == null) {
            throw new IllegalStateException("연결된 사용자가 없습니다.");
        }

        if (phoneNumber != null && !phoneNumber.isBlank()) {
            this.user.updatePhoneNumber(phoneNumber);
        }
        if (name != null && !name.isBlank()) {
            this.user.updateName(name);
        }
    }


    public MemberAddress createMemberAddress(Address address, String alias, Boolean isDefault) {
        if (address == null) {
            throw new IllegalArgumentException("주소는 필수 입력 항목입니다.");
        }
        if (alias == null || alias.isBlank()) {
            throw new IllegalArgumentException("별칭은 필수 입력 항목입니다.");
        }
        if (isDefault == null) {
            throw new IllegalArgumentException("isDefault 값은 필수 입력 항목입니다.");
        }
        return MemberAddress.builder()
                .member(this)
                .address(address)
                .alias(alias.trim())
                .isDefault(isDefault)
                .build();
    }



    public void update(String name, String email, String phoneNumber) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (email != null && !email.isBlank()) {
            updateEmail(email);
        }
        if (phoneNumber != null && !phoneNumber.isBlank()) {
            this.phone = phoneNumber;
        }
    }

    public void updatePhoneNumber(String phoneNumber) {
        if (phoneNumber != null && !phoneNumber.isBlank()) {
            this.phone = phoneNumber;
        } else {
            throw new IllegalArgumentException("전화번호는 필수 입력 항목입니다.");
        }
    }

    public void updateName(String name) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        } else {
            throw new IllegalArgumentException("이름은 필수 입력 항목입니다.");
        }
    }

    public Member(Long id, String name, String email, String phoneNumber) {

    }

    public void update(String email, String phoneNumber, String address, String detailAddress) {
    }

    public void updateGrade(MemberGrade grade) {
        this.memberGrade = grade;

    }
}