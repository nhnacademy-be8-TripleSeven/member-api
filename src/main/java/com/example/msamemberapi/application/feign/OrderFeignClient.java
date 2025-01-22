package com.example.msamemberapi.application.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "order-api")
public interface OrderFeignClient {

    @GetMapping("/orders/amount/net")
    Long getNetAmount(@RequestParam Long userId);

}
