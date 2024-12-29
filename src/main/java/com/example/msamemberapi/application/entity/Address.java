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

        @Column(nullable = false, length = 255)
        private String roadAddress;

        @Column(nullable = false, length = 255)
        private String detail;

        @Column(nullable = false, length = 50)
        private String alias;

        public void updateDetails(String roadAddress, String detail, String alias) {
            this.roadAddress = roadAddress;
            this.detail = detail;
            this.alias = alias;
        }
    }