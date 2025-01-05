package com.example.msamemberapi.application.enums;


public enum MemberRole {

    USER,
    PAYCO_USER,
    ADMIN_USER,
    INACTIVE // 휴면 계정
    ;

    @Override
    public String toString() {
        return "ROLE_" + this.name();
    }
}
