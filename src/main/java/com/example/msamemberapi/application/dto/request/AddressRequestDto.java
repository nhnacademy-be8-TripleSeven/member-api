package com.example.msamemberapi.application.dto.request;

import com.example.msamemberapi.application.entity.Address;
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

    public Address toEntity(Long userId) {
        return Address.builder()
                .id(userId)
                .postcode(this.postcode)
                .roadAddress(this.roadAddress)
                .detailAddress(this.detailAddress)
                .alias(this.alias)
                .isDefault(this.isDefault)
                .build();
    }

}