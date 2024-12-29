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

    @Column(name = "alias", length = 50, nullable = false) // 별칭 추가
    private String alias;


    public static class MemberAddressBuilder {
        public MemberAddressBuilder addressId(Long addressId) {
            this.addressId = addressId;
            return this;
        }
    }
}