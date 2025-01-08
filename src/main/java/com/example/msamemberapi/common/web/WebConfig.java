package com.example.msamemberapi.common.web;

import com.example.msamemberapi.common.interceptor.AuthHeaderInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthHeaderInterceptor authHeaderInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authHeaderInterceptor)
                .addPathPatterns("/members/auth/id", "/members/auth/login-id");
    }
}
