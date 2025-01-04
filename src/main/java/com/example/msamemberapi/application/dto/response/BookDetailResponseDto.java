package com.example.msamemberapi.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class BookDetailResponseDto {

    private String title;
    private String description;
    private LocalDate publishedDate;
    private int regularPrice;
    private int salePrice;
    private String isbn13;
    private int stock;
    private int page;
    private String coverUrl;
    private String publisher;

}
