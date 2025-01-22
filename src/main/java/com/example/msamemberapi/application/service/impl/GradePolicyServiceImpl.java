package com.example.msamemberapi.application.service.impl;

import com.example.msamemberapi.application.dto.request.GradeUpdateRequestDto;
import com.example.msamemberapi.application.dto.response.MemberGradeDto;
import com.example.msamemberapi.application.entity.GradePolicy;
import com.example.msamemberapi.application.entity.Member;
import com.example.msamemberapi.application.enums.MemberGrade;
import com.example.msamemberapi.application.feign.OrderFeignClient;
import com.example.msamemberapi.application.repository.GradePolicyRepository;
import com.example.msamemberapi.application.repository.MemberRepository;
import com.example.msamemberapi.application.service.GradePolicyService;
import java.math.BigDecimal;
import javax.swing.text.html.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GradePolicyServiceImpl implements GradePolicyService {

    private final GradePolicyRepository gradePolicyRepository;
    private final MemberRepository memberRepository;
    private final OrderFeignClient orderFeignClient;;

    @Override
    @Transactional(readOnly = true)
    public List<MemberGradeDto> getAllGrades() {
        return gradePolicyRepository.findAll().stream()
                .map(gradePolicy -> new MemberGradeDto(
                        gradePolicy.getId(),
                        gradePolicy.getName(),
                        gradePolicy.getRate(),
                        gradePolicy.getDescription(),
                        gradePolicy.getGrade(),
                        0.0
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MemberGradeDto getGradeById(Long id) {
        GradePolicy gradePolicy = gradePolicyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("등급을 찾을 수 없습니다."));
        return new MemberGradeDto(
                gradePolicy.getId(),
                gradePolicy.getName(),
                gradePolicy.getRate(),
                gradePolicy.getDescription(),
                gradePolicy.getGrade(),
                0.0
        );
    }

    @Override
    public MemberGradeDto createGrade(String name, MemberGrade memberGrade, String description, BigDecimal rate, int min, int max) {
        if (gradePolicyRepository.existsByName(name)) {
            throw new IllegalArgumentException("등급 이름이 이미 존재합니다.");
        }

        GradePolicy gradePolicy = GradePolicy.addGradePolicy(name, memberGrade, description, rate, min,max);
        GradePolicy savedGradePolicy = gradePolicyRepository.save(gradePolicy);

        return new MemberGradeDto(
                savedGradePolicy.getId(),
                savedGradePolicy.getName(),
                savedGradePolicy.getRate(),
                savedGradePolicy.getDescription(),
                savedGradePolicy.getGrade(),
                0.0
        );
    }

    @Override
    @Transactional
    public MemberGradeDto updateGrade(Long id, MemberGrade memberGrade) {

        GradePolicy gradePolicy = gradePolicyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("등급을 찾을 수 없습니다."));


        GradePolicy updatedGradePolicy = GradePolicy.builder()
                .id(gradePolicy.getId())
                .name(gradePolicy.getName())
                .grade(memberGrade)
                .description(gradePolicy.getDescription())
                .rate(gradePolicy.getRate())
                .min(gradePolicy.getMin())
                .max(gradePolicy.getMax())
                .build();


        gradePolicyRepository.save(updatedGradePolicy);

        return new MemberGradeDto(
                updatedGradePolicy.getId(),
                updatedGradePolicy.getName(),
                updatedGradePolicy.getRate(),
                updatedGradePolicy.getDescription(),
                updatedGradePolicy.getGrade(),
                0.0
        );
    }

    @Override
    @Transactional
    public void deleteGrade(Long id) {
        GradePolicy gradePolicy = gradePolicyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("등급을 찾을 수 없습니다."));
        gradePolicyRepository.delete(gradePolicy);
    }

    @Override
    @Transactional
    public Long calculateMemberGrade(Long userId, Long amount) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("멤버를 찾을 수 없습니다."));


        MemberGrade grade = member.getMemberGrade();
        GradePolicy gradePolicy = gradePolicyRepository.findByGrade(grade)
                .orElseThrow(() -> new IllegalArgumentException("등급을 찾을 수 없습니다."));

        BigDecimal rate = gradePolicy.getRate();

        return rate.multiply(BigDecimal.valueOf(amount)).longValue();
    }



}