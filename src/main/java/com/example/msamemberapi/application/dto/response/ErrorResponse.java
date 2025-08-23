package com.example.msamemberapi.application.dto.response;

import com.example.msamemberapi.application.error.application.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Builder
@Getter
public class ErrorResponse {

    private int statusCode;
    private LocalDateTime localDateTime;
    private String message;
    private String requestPath;

    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode, HttpServletRequest request) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResponse.builder()
                        .localDateTime(LocalDateTime.now())
                        .statusCode(errorCode.getHttpStatus().value())
                        .message(errorCode.getDetail())
                        .requestPath(request.getRequestURI())
                        .build()
                );
    }
}