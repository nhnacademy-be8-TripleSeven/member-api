package com.example.msamemberapi.application.enums;


public enum MemberRole {

    USER,
    PAYCO,
    ADMIN,

    ;

    @Override
    public String toString() {
        return "ROLE_" + this.name();
    }
}
