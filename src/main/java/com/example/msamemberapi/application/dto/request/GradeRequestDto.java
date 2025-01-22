package com.example.msamemberapi.application.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeRequestDto {

    @NotNull
    private String name;

    @NotNull
    private String description;

    @NotNull
    private BigDecimal rate;

    @NotNull
    private int min;

    @NotNull
    private int max;

}
