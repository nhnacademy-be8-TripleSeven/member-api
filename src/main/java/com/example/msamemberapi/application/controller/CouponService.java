package com.example.msamemberapi.application.controller;

import com.example.msamemberapi.application.feign.BookFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService {

    private final BookFeignClient bookFeignClient;

    @Async
    public void issueWelcomeCoupon(Long memberId) {
        try {
            bookFeignClient.createWelcomeCoupon(memberId);
            log.debug("웰컴 쿠폰 발급 성공 - memberId={}", memberId);
        } catch (Exception e) {
            log.warn("웰컴 쿠폰 발급 실패 - memberId={}", memberId, e);
        }
    }

}