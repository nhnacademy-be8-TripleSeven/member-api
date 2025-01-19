package com.example.msamemberapi.application.repository.querydsl.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.msamemberapi.application.entity.GradePolicy;
import com.example.msamemberapi.application.enums.MemberGrade;
import com.example.msamemberapi.application.repository.GradePolicyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@SpringBootTest
@Transactional
class GradePolicyRepositoryTest {

    @Autowired
    private GradePolicyRepository gradePolicyRepository;

    @Test
    @DisplayName("MemberGrade로 GradePolicy 조회 - 성공")
    void findByGrade_Success() {
        GradePolicy gradePolicy = GradePolicy.builder()
                .grade(MemberGrade.GOLD)
                .min(5000)
                .rate(30)
                .build();
        gradePolicyRepository.save(gradePolicy);

        Optional<GradePolicy> result = gradePolicyRepository.findByGrade(MemberGrade.GOLD);

        assertTrue(result.isPresent());
        assertEquals(MemberGrade.GOLD, result.get().getGrade());
    }
}