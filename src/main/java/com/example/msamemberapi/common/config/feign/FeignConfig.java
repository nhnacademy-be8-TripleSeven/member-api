package com.example.msamemberapi.common.config.feign;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.example.msamemberapi.application")
public class FeignConfig {
}
