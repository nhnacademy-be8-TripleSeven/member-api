package com.example.msamemberapi.application.service;

import com.example.msamemberapi.application.dto.request.GradeUpdateRequestDto;
import com.example.msamemberapi.application.dto.response.MemberGradeDto;
import com.example.msamemberapi.application.enums.MemberGrade;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface GradePolicyService {
    List<MemberGradeDto> getAllGrades();
    MemberGradeDto getGradeById(Long id);
    MemberGradeDto createGrade(String name, MemberGrade memberGrade, String description, BigDecimal rate, int min, int max);
    void deleteGrade(Long id);
    Long calculateMemberGrade(Long userId, Long amount);
    MemberGradeDto updateGrade(Long id, MemberGrade memberGrade);
}