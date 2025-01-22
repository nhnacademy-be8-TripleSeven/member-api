package com.example.msamemberapi.application.controller;

import com.example.msamemberapi.application.dto.request.GradeRequestDto;
import com.example.msamemberapi.application.dto.request.GradeUpdateRequestDto;
import com.example.msamemberapi.application.dto.response.MemberGradeDto;
import com.example.msamemberapi.application.enums.MemberGrade;
import com.example.msamemberapi.application.repository.GradePolicyRepository;
import com.example.msamemberapi.application.service.GradePolicyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberGradeController.class)
class MemberGradeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GradePolicyService gradePolicyService;

    @MockBean
    private GradePolicyRepository gradePolicyRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("등급 목록 조회")
    void getGrades_success() throws Exception {
        List<MemberGradeDto> mockGrades = List.of(
                MemberGradeDto.builder()
                        .id(1L)
                        .name("Gold")
                        .rate(BigDecimal.valueOf(0.1))
                        .description("Gold grade")
                        .grade(MemberGrade.GOLD)
                        .build(),
                MemberGradeDto.builder()
                        .id(2L)
                        .name("Platinum")
                        .rate(BigDecimal.valueOf(0.2))
                        .description("Platinum grade")
                        .grade(MemberGrade.PLATINUM)
                        .build()
        );

        when(gradePolicyService.getAllGrades()).thenReturn(mockGrades);

        mockMvc.perform(get("/api/members/grades"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("Gold"))
                .andExpect(jsonPath("$[1].name").value("Platinum"));
    }

    @Test
    @DisplayName("회원 등급 조회")
    void getGradeByMemberId_success() throws Exception {
        Long userId = 1L;

        MemberGradeDto mockGrade = MemberGradeDto.builder()
                .id(1L)
                .name("Gold")
                .rate(BigDecimal.valueOf(0.1))
                .description("Gold grade")
                .grade(MemberGrade.GOLD)
                .points(500.0)
                .build();

        when(gradePolicyService.getGradeById(userId)).thenReturn(mockGrade);

        mockMvc.perform(get("/api/members/grade")
                        .header("X-USER", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Gold"))
                .andExpect(jsonPath("$.grade").value("GOLD"))
                .andExpect(jsonPath("$.points").value(500.0));
    }

    @Test
    @DisplayName("새 등급 생성")
    void createGrade_success() throws Exception {
        GradeRequestDto requestDto = GradeRequestDto.builder()
                .name("Platinum")
                .description("Platinum grade")
                .rate(BigDecimal.valueOf(0.2))
                .min(5000)
                .max(10000)
                .build();

        MemberGradeDto responseDto = MemberGradeDto.builder()
                .id(1L)
                .name("Platinum")
                .rate(BigDecimal.valueOf(0.2))
                .description("Platinum grade")
                .grade(MemberGrade.PLATINUM)
                .build();

        when(gradePolicyService.createGrade(
                Mockito.eq(requestDto.getName()),
                Mockito.eq(MemberGrade.PLATINUM),
                Mockito.eq(requestDto.getDescription()),
                Mockito.eq(requestDto.getRate()),
                Mockito.eq(requestDto.getMin()),
                Mockito.eq(requestDto.getMax())
        )).thenReturn(responseDto);

        mockMvc.perform(post("/admin/members/grade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Platinum"))
                .andExpect(jsonPath("$.rate").value(0.2))
                .andExpect(jsonPath("$.grade").value("PLATINUM"));
    }

    @Test
    @DisplayName("등급 업데이트 성공")
    void updateGrade_success() throws Exception {
        Long gradeId = 1L;

        // 요청 데이터: 유효한 데이터를 제공
        GradeUpdateRequestDto updateRequestDto = GradeUpdateRequestDto.builder()
                .name("Platinum") // 필수 필드: @NotBlank
                .description("Updated Platinum grade") // 필수 필드: @NotBlank
                .rate(BigDecimal.valueOf(0.25)) // 필수 필드: @NotNull
                .grade(MemberGrade.PLATINUM)
                .min(6000)
                .max(12000)
                .build();

        // Mock 응답 데이터
        MemberGradeDto updatedGrade = MemberGradeDto.builder()
                .id(gradeId)
                .name("Platinum")
                .rate(BigDecimal.valueOf(0.25))
                .description("Updated Platinum grade")
                .grade(MemberGrade.PLATINUM)
                .build();

        // Mock 설정
        when(gradePolicyService.updateGrade(eq(gradeId), any(MemberGrade.class)))
                .thenReturn(updatedGrade);

        // 테스트 실행
        mockMvc.perform(put("/admin/members/grade/{id}", gradeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isOk()) // 상태 코드 200 확인
                .andExpect(jsonPath("$.name").value("Platinum")) // 응답 검증
                .andExpect(jsonPath("$.grade").value("PLATINUM"))
                .andExpect(jsonPath("$.description").value("Updated Platinum grade"))
                .andExpect(jsonPath("$.rate").value(0.25)); // BigDecimal의 값 확인
    }

    @Test
    @DisplayName("등급 삭제")
    void deleteGrade_success() throws Exception {
        Long gradeId = 1L;

        doNothing().when(gradePolicyService).deleteGrade(gradeId);

        mockMvc.perform(delete("/admin/members/grade/{id}", gradeId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("회원 등급 계산")
    void getMemberGrade_success() throws Exception {
        Long userId = 1L;
        Long amount = 5000L;
        Long expectedPoints = 500L;

        when(gradePolicyService.calculateMemberGrade(userId, amount)).thenReturn(expectedPoints);

        mockMvc.perform(get("/members/grade/point")
                        .param("userId", userId.toString())
                        .param("amount", amount.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedPoints.toString()));
    }
}