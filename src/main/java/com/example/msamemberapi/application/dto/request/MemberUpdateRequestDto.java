package com.example.msamemberapi.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberUpdateRequestDto {

    @NotBlank
    private String userId;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "^[0-9]{10,11}$", message = "전화번호는 10~11자리 숫자여야 합니다.")
    private String phone;

    @Size(max = 10)
    private String postcode;

    @Size(max = 255)
    private String address;

    @Size(max = 255)
    private String detailAddress;

    @Size(min = 10, max = 20, message = "새 비밀번호는 10~20자여야 합니다.")
    private String newPassword;
}