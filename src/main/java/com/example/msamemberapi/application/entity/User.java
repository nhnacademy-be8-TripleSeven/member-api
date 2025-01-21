package com.example.msamemberapi.application.entity;

import com.example.msamemberapi.application.enums.MemberGrade;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String phoneNumber;
    private String name;

    @Builder.Default
    private int points = 0;
    private MemberGrade membership;

    public User withAddedPoints(int additionalPoints) {
        int updatedPoints = this.points + additionalPoints;
        return this.withUpdatedPoints(updatedPoints);
    }

    public User withUpdatedPoints(int newPoints) {
        MemberGrade updatedMembership = calculateMembership(newPoints);
        return User.builder()
                .id(this.id)
                .name(this.name)
                .phoneNumber(this.phoneNumber)
                .points(newPoints)
                .membership(updatedMembership)
                .build();
    }


    public MemberGrade calculateMembership(int points) {
        if (points >= 10000) {
            return MemberGrade.PLATINUM;
        } else if (points >= 5000) {
            return MemberGrade.GOLD;
        } else if (points >= 1000) {
            return MemberGrade.ROYAL;
        } else {
            return MemberGrade.REGULAR;
        }
    }

    public void updatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            throw new IllegalArgumentException("전화번호는 비어 있을 수 없습니다.");
        }
        this.phoneNumber = phoneNumber;
    }

    public void updateName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("이름은 비어 있을 수 없습니다.");
        }
        this.name = name;
    }

    public User withUpdatedPhoneNumber(String phoneNumber) {
        if (phoneNumber != null && !phoneNumber.isBlank()) {
            return User.builder()
                    .id(this.id)
                    .name(this.name)
                    .phoneNumber(phoneNumber)
                    .build();
        }
        return this;
    }
    @PrePersist
    @PreUpdate
    private void updateMembershipOnChange() {
        this.membership = calculateMembership(this.points);
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", phoneNumber='" + phoneNumber + "', name='" + name + "', points=" + points + ", membership=" + membership + "}";
    }

}
