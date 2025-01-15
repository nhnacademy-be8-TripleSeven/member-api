package com.example.msamemberapi.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberGradeDto {
    private String currentGrade; // 현재 등급
    private String nextGrade; // 다음 등급
    private Integer currentSpending; // 3개월 순수 금액
    private Integer requiredForNextGrade; // 다음 등급까지 필요한 금액
}