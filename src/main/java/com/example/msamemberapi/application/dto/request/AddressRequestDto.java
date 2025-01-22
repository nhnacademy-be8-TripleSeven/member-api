package com.example.msamemberapi.application.dto.request;

import com.example.msamemberapi.application.entity.Address;
import com.example.msamemberapi.application.entity.Member;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressRequestDto {
    /**
     * DTO를 Address 엔티티로 변환합니다.
     *
     * @param memberId 회원 ID
     * @return Address 엔티티
     */

    private Long id;

    @NotNull
    @NotBlank
    private String postcode;

    @NotNull
    @NotBlank
    private String roadAddress;

    @NotNull
    @NotBlank
    private String detailAddress;

    @NotNull
    @NotBlank
    private String alias;

    @NotNull
    private Boolean isDefault; // 기본 주소 여부

//    public Address toEntity(Long userId) {
//        return Address.builder()
//                .id(userId)
//                .postcode(this.postcode)
//                .roadAddress(this.roadAddress)
//                .detailAddress(this.detailAddress)
//                .alias(this.alias)
//                .isDefault(this.isDefault)
//                .build();
//    }

    public AddressRequestDto(String postcode, String roadAddress, String detailAddress, String alias,
                             Boolean isDefault) {
        this.postcode = postcode;
        this.roadAddress = roadAddress;
        this.detailAddress = detailAddress;
        this.alias = alias;
        this.isDefault = isDefault;
    }

    public Address toEntity(Member member) {
        return Address.builder()
                .member(member) // Member 객체 설정
                .postcode(this.postcode)
                .roadAddress(this.roadAddress)
                .detailAddress(this.detailAddress)
                .alias(this.alias)
                .isDefault(this.isDefault)
                .build();
    }
}