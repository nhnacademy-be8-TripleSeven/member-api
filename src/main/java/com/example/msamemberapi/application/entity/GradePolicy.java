// GradePolicy.java
package com.example.msamemberapi.application.entity;

import com.example.msamemberapi.application.enums.MemberGrade;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="grade")
public class GradePolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MemberGrade grade;

    @Column(name="description", nullable = false)
    private String description;

    private BigDecimal rate;

    private int min;

    private int max;

    public static GradePolicy addGradePolicy(String name, MemberGrade grade, String description, BigDecimal rate, int min, int max) {
        return new GradePolicy(null, name, grade, description, rate, min, max);
    }

}