package com.example.msamemberapi.common.config.feign.book;


import com.example.msamemberapi.application.feign.BookFeignClient;
import feign.codec.ErrorDecoder;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.feign.FeignDecorator;
import io.github.resilience4j.feign.FeignDecorators;
import io.github.resilience4j.feign.Resilience4jFeign;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BookApiFeignConfig {

    private final CircuitBreaker bookApiCircuitBreaker;
    private final ErrorDecoder bookApiErrorDecoder;

    public BookApiFeignConfig(CircuitBreaker bookApiCircuitBreaker,
                              ErrorDecoder bookApiErrorDecoder) {
        this.bookApiCircuitBreaker = bookApiCircuitBreaker;
        this.bookApiErrorDecoder = bookApiErrorDecoder;
    }


    @Bean
    public BookFeignClient bookApiClient() {
        FeignDecorators decorators = FeignDecorators.builder()
                .withCircuitBreaker(bookApiCircuitBreaker)
                .build();

        return Resilience4jFeign.builder(decorators)
                .contract(new SpringMvcContract())
                .errorDecoder(bookApiErrorDecoder)
                .target(BookFeignClient.class, "http://book-coupon-api");
    }
}