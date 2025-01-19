package com.example.msamemberapi.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberGradeHistoryDto {
    private String gradeName;
    private LocalDate changedDate;
}