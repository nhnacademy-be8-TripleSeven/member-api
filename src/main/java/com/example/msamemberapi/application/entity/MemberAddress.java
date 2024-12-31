package com.example.msamemberapi.application.entity;

import jakarta.persistence.*;
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

    @Column(name = "address_id", nullable = false)
    private Long addressId;

    @Column(name = "alias", length = 50, nullable = false)
    private String alias;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault; //기본 주소

    public void updateAlias(String alias) {
        this.alias = alias;
    }

    public void setDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }
}