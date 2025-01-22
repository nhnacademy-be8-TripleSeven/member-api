package com.example.msamemberapi.application.dto.response;


import com.example.msamemberapi.application.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class AddressResponseDto {
    private Long id;
    private String postcode;
    private String roadAddress;
    private String detailAddress;
    private String alias;
    private Boolean isDefault;




    public static AddressResponseDto fromEntity(Address address) {
        return AddressResponseDto.builder()
                .id(address.getId())
                .postcode(address.getPostcode())
                .roadAddress(address.getRoadAddress())
                .detailAddress(address.getDetailAddress())
                .alias(address.getAlias())
                .build();
    }

}