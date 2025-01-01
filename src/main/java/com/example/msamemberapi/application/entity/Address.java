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
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String postcode;

    @Column(nullable = false)
    private String roadAddress;

    @Column(nullable = false)
    private String detailAddress;

    @Column(nullable = false)
    private String alias;

    public void updateDetails(String roadAddress, String detailAddress, String alias) {
        this.roadAddress = roadAddress;
        this.detailAddress = detailAddress;
        this.alias = alias;
    }
}