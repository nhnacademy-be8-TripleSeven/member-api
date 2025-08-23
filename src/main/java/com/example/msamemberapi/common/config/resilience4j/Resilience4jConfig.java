package com.example.msamemberapi.common.config.resilience4j;

import com.example.msamemberapi.application.error.api.ApiException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Resilience4jConfig {

    @Bean
    public CircuitBreaker bookApiCircuitBreaker(CircuitBreakerRegistry registry) {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(50) // 실패율 임계치
                .slidingWindowSize(10)    // 윈도우 크기
                .waitDurationInOpenState(Duration.ofSeconds(30)) // Open 상태 유지 시간
                .recordException(e -> !(e instanceof ApiException)) // BusinessException 무시
                .build();

        return registry.circuitBreaker("bookApiCircuitBreaker", config);
    }
}