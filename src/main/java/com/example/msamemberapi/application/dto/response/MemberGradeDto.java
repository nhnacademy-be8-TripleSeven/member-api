package com.example.msamemberapi.application.dto.response;

import com.example.msamemberapi.application.entity.GradePolicy;
import com.example.msamemberapi.application.enums.MemberGrade;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class MemberGradeDto {
    private Long id;
    private String name;
    private int rate;     // 적립률
    private String description;
    private MemberGrade grade;
    private double points;
    private String currentGrade;
    private String nextGrade;
    private int currentSpending;
    private int requiredForNextGrade;

    public MemberGradeDto(Long id, String name, int rate, String description, MemberGrade memberGrade, double points) {
        this.id = id;
        this.name = name;
        this.rate = rate;
        this.description = description;
        this.grade = memberGrade;
        this.points = points;

    }

    public static MemberGradeDto from(MemberGrade grade, double points) {
        return new MemberGradeDto(
                null,
                grade.name(),
                0,
                grade.toString(),
                grade,
                points
        );
    }

    public static MemberGradeDto from(GradePolicy grade) {
        return new MemberGradeDto(
                grade.getId(),
                grade.getName(),
                grade.getRate(),
                grade.getDescription(),
                grade.getGrade(),
                0
        );
    }

    public static MemberGradeDto from(String currentGrade, String nextGrade, int currentSpending, int requiredForNextGrade) {
        return MemberGradeDto.builder()
                .currentGrade(currentGrade)
                .nextGrade(nextGrade)
                .currentSpending(currentSpending)
                .requiredForNextGrade(requiredForNextGrade)
                .build();
    }
}