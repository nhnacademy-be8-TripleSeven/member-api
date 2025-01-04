package com.example.msamemberapi.application.feign;

import com.example.msamemberapi.application.dto.response.BookDetailResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value="book-coupon-api", path="/books")
public interface BookFeignClient {

    @GetMapping("/{bookId}")
    ResponseEntity<BookDetailResponseDto> getBookDetails(@PathVariable long bookId);

}

