package com.example.msamemberapi.application.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberGradeHistory is a Querydsl query type for MemberGradeHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberGradeHistory extends EntityPathBase<MemberGradeHistory> {

    private static final long serialVersionUID = 245088748L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberGradeHistory memberGradeHistory = new QMemberGradeHistory("memberGradeHistory");

    public final DateTimePath<java.util.Date> createdAt = createDateTime("createdAt", java.util.Date.class);

    public final EnumPath<com.example.msamemberapi.application.enums.MemberGrade> grade = createEnum("grade", com.example.msamemberapi.application.enums.MemberGrade.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMember member;

    public QMemberGradeHistory(String variable) {
        this(MemberGradeHistory.class, forVariable(variable), INITS);
    }

    public QMemberGradeHistory(Path<? extends MemberGradeHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberGradeHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberGradeHistory(PathMetadata metadata, PathInits inits) {
        this(MemberGradeHistory.class, metadata, inits);
    }

    public QMemberGradeHistory(Class<? extends MemberGradeHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member"), inits.get("member")) : null;
    }

}

