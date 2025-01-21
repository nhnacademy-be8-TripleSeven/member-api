package com.example.msamemberapi.application.service.impl;

import com.example.msamemberapi.application.dto.response.MemberGradeDto;
import com.example.msamemberapi.application.dto.response.MemberGradeHistoryDto;
import com.example.msamemberapi.application.entity.GradePolicy;
import com.example.msamemberapi.application.entity.Member;
import com.example.msamemberapi.application.entity.MemberGradeHistory;
import com.example.msamemberapi.application.enums.MemberGrade;
import com.example.msamemberapi.application.feign.OrderFeignClient;
import com.example.msamemberapi.application.repository.GradePolicyRepository;
import com.example.msamemberapi.application.repository.MemberRepository;
import com.example.msamemberapi.application.service.MemberService;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GradePolicyServiceImplTest {

    @Mock
    private GradePolicyRepository gradePolicyRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private OrderFeignClient orderFeignClient;

    @Mock
    private MemberService memberService;


    @InjectMocks
    private GradePolicyServiceImpl gradePolicyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("모든 등급 조회 성공")
    void getAllGrades_success() {
        // Arrange
        GradePolicy gradePolicy = GradePolicy.builder()
                .id(1L)
                .name("Gold")
                .grade(MemberGrade.GOLD)
                .description("Gold grade")
                .rate(BigDecimal.valueOf(0.1))
                .min(1000)
                .max(5000)
                .build();
        when(gradePolicyRepository.findAll()).thenReturn(List.of(gradePolicy));

        // Act
        List<MemberGradeDto> grades = gradePolicyService.getAllGrades();

        // Assert
        assertEquals(1, grades.size());
        assertEquals("Gold", grades.get(0).getName());
        verify(gradePolicyRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("ID로 등급 조회 성공")
    void getGradeById_success() {
        // Arrange
        GradePolicy gradePolicy = GradePolicy.builder()
                .id(1L)
                .name("Gold")
                .grade(MemberGrade.GOLD)
                .description("Gold grade")
                .rate(BigDecimal.valueOf(0.1))
                .min(1000)
                .max(5000)
                .build();
        when(gradePolicyRepository.findById(1L)).thenReturn(Optional.of(gradePolicy));

        // Act
        MemberGradeDto grade = gradePolicyService.getGradeById(1L);

        // Assert
        assertNotNull(grade);
        assertEquals("Gold", grade.getName());
        verify(gradePolicyRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("등급 생성 성공")
    void createGrade_success() {
        // Arrange
        GradePolicy gradePolicy = GradePolicy.builder()
                .id(1L)
                .name("Gold")
                .grade(MemberGrade.GOLD)
                .description("Gold grade")
                .rate(BigDecimal.valueOf(0.1))
                .min(1000)
                .max(5000)
                .build();
        when(gradePolicyRepository.existsByName("Gold")).thenReturn(false);
        when(gradePolicyRepository.save(any(GradePolicy.class))).thenReturn(gradePolicy);

        // Act
        MemberGradeDto createdGrade = gradePolicyService.createGrade(
                "Gold", MemberGrade.GOLD, "Gold grade", BigDecimal.valueOf(0.1), 1000, 5000);

        // Assert
        assertNotNull(createdGrade);
        assertEquals("Gold", createdGrade.getName());
        verify(gradePolicyRepository, times(1)).existsByName("Gold");
        verify(gradePolicyRepository, times(1)).save(any(GradePolicy.class));
    }

    @Test
    @DisplayName("등급 업데이트 성공")
    void updateGrade_success() {
        // Arrange
        GradePolicy gradePolicy = GradePolicy.builder()
                .id(1L)
                .name("Gold")
                .grade(MemberGrade.GOLD)
                .description("Gold grade")
                .rate(BigDecimal.valueOf(0.1))
                .min(1000)
                .max(5000)
                .build();
        when(gradePolicyRepository.findById(1L)).thenReturn(Optional.of(gradePolicy));
        when(gradePolicyRepository.save(any(GradePolicy.class))).thenReturn(gradePolicy);

        // Act
        MemberGradeDto updatedGrade = gradePolicyService.updateGrade(1L, MemberGrade.PLATINUM);

        // Assert
        assertNotNull(updatedGrade);
        assertEquals(MemberGrade.PLATINUM, updatedGrade.getGrade());
        verify(gradePolicyRepository, times(1)).findById(1L);
        verify(gradePolicyRepository, times(1)).save(any(GradePolicy.class));
    }

    @Test
    @DisplayName("등급 삭제 성공")
    void deleteGrade_success() {
        // Arrange
        GradePolicy gradePolicy = GradePolicy.builder()
                .id(1L)
                .build();
        when(gradePolicyRepository.findById(1L)).thenReturn(Optional.of(gradePolicy));

        // Act
        gradePolicyService.deleteGrade(1L);

        // Assert
        verify(gradePolicyRepository, times(1)).findById(1L);
        verify(gradePolicyRepository, times(1)).delete(gradePolicy);
    }

    @Test
    @DisplayName("멤버 등급 계산 성공")
    void calculateMemberGrade_success() {
        // Arrange
        Member member = Member.builder()
                .id(1L)
                .memberGrade(MemberGrade.GOLD)
                .build();
        GradePolicy gradePolicy = GradePolicy.builder()
                .id(1L)
                .grade(MemberGrade.GOLD)
                .rate(BigDecimal.valueOf(0.1))
                .build();
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(gradePolicyRepository.findByGrade(MemberGrade.GOLD)).thenReturn(Optional.of(gradePolicy));

        // Act
        Long result = gradePolicyService.calculateMemberGrade(1L, 10000L);

        // Assert
        assertEquals(1000L, result);
        verify(memberRepository, times(1)).findById(1L);
        verify(gradePolicyRepository, times(1)).findByGrade(MemberGrade.GOLD);
    }

//    @Test
//    @DisplayName("회원 등급 변경 이력 조회 성공")
//    void getGradeHistory_success() {
//        // Arrange
//        Long memberId = 1L;
//        MemberGradeHistory history1 = MemberGradeHistory.builder()
//                .createdAt(LocalDate.of(2023, 1, 1))
//                .gradePolicy(GradePolicy.builder().name("GOLD").build())
//                .build();
//
//        MemberGradeHistory history2 = MemberGradeHistory.builder()
//                .createdAt(LocalDate.of(2023, 2, 1))
//                .gradePolicy(GradePolicy.builder().name("PLATINUM").build())
//                .build();
//
//        when(gradePolicyRepository.findByMemberId(memberId))
//                .thenReturn(List.of(history1, history2));
//
//        // Act
//        List<MemberGradeHistoryDto> result = memberService.getGradeHistory(memberId);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(2, result.size());
//        assertEquals("GOLD", result.get(0).getGradeName());
//        assertEquals(LocalDate.of(2023, 1, 1), result.get(0).getChangedDate());
//        assertEquals("PLATINUM", result.get(1).getGradeName());
//        assertEquals(LocalDate.of(2023, 2, 1), result.get(1).getChangedDate());
//        verify(gradePolicyRepository, times(1)).findByMemberId(memberId);
//    }
}
