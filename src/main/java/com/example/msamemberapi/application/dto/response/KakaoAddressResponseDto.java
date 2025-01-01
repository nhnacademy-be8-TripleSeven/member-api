package com.example.msamemberapi.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KakaoAddressResponseDto {
    private List<Document> documents;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Document {
        private String addressName;
        private String roadAddressName;
        private String buildingName;
    }
}