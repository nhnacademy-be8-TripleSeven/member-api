package com.example.msamemberapi.application.service;

import com.example.msamemberapi.application.dto.request.GradeUpdateRequestDto;
import com.example.msamemberapi.application.dto.response.MemberGradeDto;
import com.example.msamemberapi.application.enums.MemberGrade;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface GradePolicyService {
    List<MemberGradeDto> getAllGrades();
    MemberGradeDto getGradeById(Long id);
    MemberGradeDto createGrade(String name, MemberGrade memberGrade, String description, int rate, int min); // min 추가
    void deleteGrade(Long id);
    Long calculateMemberGrade(Long userId, Long amount);
    MemberGradeDto updateGrade(Long id, MemberGrade memberGrade);
}