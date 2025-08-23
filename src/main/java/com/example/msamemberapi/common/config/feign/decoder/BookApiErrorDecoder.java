package com.example.msamemberapi.common.config.feign.decoder;

import com.example.msamemberapi.application.error.api.ApiException;
import com.example.msamemberapi.application.error.application.CustomException;
import com.example.msamemberapi.application.error.application.ErrorCode;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BookApiErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new ErrorDecoder.Default();

    @Override
    public Exception decode(String methodKey, Response response) {

        int status = response.status();

        try {
            // 400번대 오류 처리
            if (status >= 400 && status < 500) {
                String body = response.body() != null ? Util.toString(response.body().asReader(StandardCharsets.UTF_8)) : "Client error";
                return new ApiException(status, body);
            }

            // 500번대 오류 등 기존 처리
            switch (status) {
                case 500:
                    return new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
                default:
                    return defaultErrorDecoder.decode(methodKey, response);
            }
        } catch (IOException e) {
            return new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
