package com.example.msamemberapi.application.entity;

import com.example.msamemberapi.application.enums.MemberGrade;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GradePolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MemberGrade grade;

    @Column(nullable = false)
    private Integer min; // 최소 금액

    @Column
    private Integer max; // 최대 금액

    @Column(nullable = false)
    private Double rate;

    public String getName() {
        return grade.name();
    }

    public boolean isWithinRange(int spending) {
        return spending >= min && (max == null || spending < max);
    }

    @Builder
    public GradePolicy(MemberGrade grade, Integer min, Integer max, Double rate) {
        this.grade = grade;
        this.min = min;
        this.max = max;
        this.rate = rate;
    }
}