package com.example.msamemberapi.application.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    /* 400 BAD_REQUEST : 잘못된 요청 */
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다"),

    /* 401 UNAUTHORIZED : 인증되지 않은 사용자 */
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "일치하는 계정이 없습니다"),
    INVALID_VERIFICATION_CODE(HttpStatus.UNAUTHORIZED, "인증 코드가 일치하지 않습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다"),
    /* 403 FORBIDDEN : 권한이 없는 사용자 */
    INVALIDATE_EMAIL(HttpStatus.FORBIDDEN, "인증되지 않은 이메일 입니다."),

    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
    ACCOUNT_ID_NOT_FOUND(HttpStatus.NOT_FOUND, "일치하는 계정이 없습니다."),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "일치하는 이메일이 없습니다."),
    PHONE_NOT_FOUND(HttpStatus.NOT_FOUND, "일치하는 핸드폰번호가 없습니다."),
    GRADE_POLICY_NOT_FOUND(HttpStatus.NOT_FOUND,"등급 정책을 찾을 수 없습니다."),

    /* 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재 */
    ALREADY_EXIST_LOGIN_ID(HttpStatus.CONFLICT, "이미 존재하는 로그인 아이디입니다."),
    ALREADY_EXIST_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일 입니다."),
    ALREADY_EXIST_PHONE(HttpStatus.CONFLICT, "이미 존재하는 핸드폰번호 입니다."),
    INSUFFICIENT_BOOK_QUANTITY(HttpStatus.CONFLICT, "책의 재고 수량이 부족합니다."),

    /* 500 INTERNAL_SERVER_ERROR : 서버오류 */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러"),
    ;

    private final HttpStatus httpStatus;
    private final String detail;
}
