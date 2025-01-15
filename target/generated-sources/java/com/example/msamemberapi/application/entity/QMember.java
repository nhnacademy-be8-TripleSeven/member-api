package com.example.msamemberapi.application.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = 1100398479L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMember member = new QMember("member1");

    public final StringPath address = createString("address");

    public final DateTimePath<java.util.Date> birth = createDateTime("birth", java.util.Date.class);

    public final StringPath detailAddress = createString("detailAddress");

    public final StringPath email = createString("email");

    public final EnumPath<com.example.msamemberapi.application.enums.Gender> gender = createEnum("gender", com.example.msamemberapi.application.enums.Gender.class);

    public final ListPath<MemberGradeHistory, QMemberGradeHistory> gradeHistories = this.<MemberGradeHistory, QMemberGradeHistory>createList("gradeHistories", MemberGradeHistory.class, QMemberGradeHistory.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMemberAccount memberAccount;

    public final EnumPath<com.example.msamemberapi.application.enums.MemberGrade> memberGrade = createEnum("memberGrade", com.example.msamemberapi.application.enums.MemberGrade.class);

    public final StringPath name = createString("name");

    public final StringPath phone = createString("phone");

    public final StringPath postcode = createString("postcode");

    public final ListPath<String, StringPath> roles = this.<String, StringPath>createList("roles", String.class, StringPath.class, PathInits.DIRECT2);

    public final QUser user;

    public QMember(String variable) {
        this(Member.class, forVariable(variable), INITS);
    }

    public QMember(Path<? extends Member> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMember(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMember(PathMetadata metadata, PathInits inits) {
        this(Member.class, metadata, inits);
    }

    public QMember(Class<? extends Member> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.memberAccount = inits.isInitialized("memberAccount") ? new QMemberAccount(forProperty("memberAccount")) : null;
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

