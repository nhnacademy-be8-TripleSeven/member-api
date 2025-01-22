package com.example.msamemberapi.application.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @Column(name = "alias", length = 50, nullable = false)
    private String alias;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault;

    public MemberAddress createUpdatedCopy(String alias, Boolean isDefault) {
        return MemberAddress.builder()
                .id(this.id)
                .member(this.member)
                .address(this.address)
                .alias(alias != null ? alias : this.alias)
                .isDefault(isDefault != null ? isDefault : this.isDefault)
                .build();
    }
}