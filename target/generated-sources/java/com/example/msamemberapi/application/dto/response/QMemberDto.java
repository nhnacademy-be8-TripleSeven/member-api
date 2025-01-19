package com.example.msamemberapi.application.dto.response;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.example.msamemberapi.application.dto.response.QMemberDto is a Querydsl Projection type for MemberDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QMemberDto extends ConstructorExpression<MemberDto> {

    private static final long serialVersionUID = -1492577635L;

    public QMemberDto(com.querydsl.core.types.Expression<Long> id, com.querydsl.core.types.Expression<String> email, com.querydsl.core.types.Expression<String> phoneNumber, com.querydsl.core.types.Expression<String> name, com.querydsl.core.types.Expression<? extends java.util.Date> birth, com.querydsl.core.types.Expression<com.example.msamemberapi.application.enums.Gender> gender, com.querydsl.core.types.Expression<com.example.msamemberapi.application.enums.MemberGrade> memberGrade) {
        super(MemberDto.class, new Class<?>[]{long.class, String.class, String.class, String.class, java.util.Date.class, com.example.msamemberapi.application.enums.Gender.class, com.example.msamemberapi.application.enums.MemberGrade.class}, id, email, phoneNumber, name, birth, gender, memberGrade);
    }

}

