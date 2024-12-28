package com.example.msamemberapi.application.error;

import com.example.msamemberapi.application.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(value = { CustomException.class })
    protected ErrorResponse handleHttpCustomException(CustomException e, HttpServletRequest request) {
        log.error("handleHttpCustomException throw CustomException : {}", e.getErrorCode());
        return new ErrorResponse(e, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        e.printStackTrace();
        return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
