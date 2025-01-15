package com.example.msamemberapi.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class KakaoAddressResponseDto {

    @JsonProperty("documents")
    private List<Document> documents;

    @JsonProperty("meta")
    private Meta meta;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Document {
        @JsonProperty("address")
        private Address address;

        @JsonProperty("address_name")
        private String addressName;

        @JsonProperty("address_type")
        private String addressType;

        @JsonProperty("road_address")
        private RoadAddress roadAddress;

        @JsonProperty("x")
        private String x;

        @JsonProperty("y")
        private String y;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Address {
        @JsonProperty("address_name")
        private String addressName;

        @JsonProperty("b_code")
        private String bCode;

        @JsonProperty("h_code")
        private String hCode;

        @JsonProperty("main_address_no")
        private String mainAddressNo;

        @JsonProperty("mountain_yn")
        private String mountainYn;

        @JsonProperty("region_1depth_name")
        private String region1DepthName;

        @JsonProperty("region_2depth_name")
        private String region2DepthName;

        @JsonProperty("region_3depth_h_name")
        private String region3DepthHName;

        @JsonProperty("region_3depth_name")
        private String region3DepthName;

        @JsonProperty("sub_address_no")
        private String subAddressNo;

        @JsonProperty("x")
        private String x;

        @JsonProperty("y")
        private String y;

        @JsonProperty("zone_no")
        private String zoneNo;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RoadAddress {
        @JsonProperty("address_name")
        private String addressName;

        @JsonProperty("region_1depth_name")
        private String region1DepthName;

        @JsonProperty("region_2depth_name")
        private String region2DepthName;

        @JsonProperty("region_3depth_name")
        private String region3DepthName;

        @JsonProperty("road_name")
        private String roadName;

        @JsonProperty("main_building_no")
        private String mainBuildingNo;

        @JsonProperty("sub_building_no")
        private String subBuildingNo;

        @JsonProperty("building_name")
        private String buildingName;

        @JsonProperty("zone_no")
        private String zoneNo;
    }


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Meta {
        @JsonProperty("is_end")
        private boolean isEnd;

        @JsonProperty("pageable_count")
        private int pageableCount;

        @JsonProperty("total_count")
        private int totalCount;
    }


}