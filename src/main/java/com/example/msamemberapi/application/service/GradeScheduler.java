package com.example.msamemberapi.application.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
@Component
@RequiredArgsConstructor
public class GradeScheduler {
    private final MemberService memberService;

    @Scheduled(cron = "0 0 0 1 * *")
    public void processUpdateGrades() {
        memberService.updateMemberGrade();
    }
}
