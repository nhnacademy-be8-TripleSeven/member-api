package com.example.msamemberapi.application.dto.response;

import com.example.msamemberapi.application.error.CustomException;
import com.example.msamemberapi.application.error.ErrorCode;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class ErrorResponse {

    private int statusCode;
    private LocalDateTime localDateTime;
    private String message;
    private String requestPath;

    public ErrorResponse(CustomException e, HttpServletRequest request) {
        this.statusCode = e.getErrorCode().getHttpStatus().value();
        this.localDateTime = LocalDateTime.now();
        this.message = e.getMessage();
        this.requestPath = request.getRequestURI();
    }
}