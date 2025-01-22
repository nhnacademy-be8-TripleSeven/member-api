package com.example.msamemberapi.application.dto.request;

import com.example.msamemberapi.application.dto.response.MemberGradeDto;
import com.example.msamemberapi.application.enums.MemberGrade;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class GradeUpdateRequestDto {


    @NotBlank(message = "등급 이름은 필수 입력입니다.")
    @Size(max = 10)
    private String name;

    @NotNull(message = "적립률은 필수 입력입니다.")
    @Min(value = 0)
    private BigDecimal rate;

    @NotBlank(message = "등급 설명은 필수 입력입니다.")
    @Size(max = 200)
    private String description;

    @NotNull(message = "등급은 필수 입력입니다.")
    private MemberGrade grade;

    private int min;

    private int max;


}