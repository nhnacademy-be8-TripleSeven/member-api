package com.example.msamemberapi.common.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebClientConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebClientConfig.class);

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    @PostConstruct
    public void logKakaoApiKey() {
        if (kakaoApiKey != null && !kakaoApiKey.isEmpty()) {
            logger.info("Kakao API Key is successfully loaded.");
        } else {
            logger.warn("Kakao API Key is not set.");
        }
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("https://dapi.kakao.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoApiKey)
                .build();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8080")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/addresses").setViewName("address-manage");
        registry.addViewController("/members/edit").setViewName("member-edit");
    }
}