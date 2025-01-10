package com.example.msamemberapi.application.feign;

import com.example.msamemberapi.application.dto.response.BookDetailResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value="book-coupon-api")
public interface BookFeignClient {

    @GetMapping("/books/{bookId}")
    ResponseEntity<BookDetailResponseDto> getBookDetails(@PathVariable long bookId);

    @PostMapping("/coupons/create/welcome")
    ResponseEntity createWelcomeCoupon(@RequestParam Long memberId);


}

