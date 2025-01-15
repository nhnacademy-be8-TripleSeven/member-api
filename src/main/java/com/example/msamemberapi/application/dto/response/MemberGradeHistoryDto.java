package com.example.msamemberapi.application.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class MemberGradeHistoryDto {
    private String gradeName; // 변경된 등급명
    private LocalDate changedDate; // 변경 날짜
}