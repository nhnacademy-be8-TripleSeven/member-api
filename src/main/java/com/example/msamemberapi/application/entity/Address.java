package com.example.msamemberapi.application.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    
    @Column(nullable = true)
    private String postcode;

    @Column(nullable = false)
    private String roadAddress;

    @Column(nullable = false)
    private String detailAddress;

    @Column(nullable = false)
    private String alias;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Boolean isDefault;

    public Address(Member member, String postcode, String roadAddress, String detailAddress, String alias, Boolean isDefault) {
        this.member = member;
        this.postcode = postcode;
        this.roadAddress = roadAddress;
        this.detailAddress = detailAddress;
        this.alias = alias;
        this.isDefault = isDefault;
    }

    public void updateDetails(String roadAddress, String detailAddress, String alias, String postcode, Boolean isDefault) {
        this.roadAddress = roadAddress;
        this.detailAddress = detailAddress;
        this.alias = alias;
        this.postcode = postcode;
        this.isDefault = isDefault;
    }
}