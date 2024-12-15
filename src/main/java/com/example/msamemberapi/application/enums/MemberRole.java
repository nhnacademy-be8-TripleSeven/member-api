package com.example.msamemberapi.application.enums;


public enum MemberRole {

    USER,

    ;

    @Override
    public String toString() {
        return "ROLE_" + this.name();
    }
}
