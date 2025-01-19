package com.example.msamemberapi.application.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMemberAccount is a Querydsl query type for MemberAccount
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberAccount extends EntityPathBase<MemberAccount> {

    private static final long serialVersionUID = 234394526L;

    public static final QMemberAccount memberAccount = new QMemberAccount("memberAccount");

    public final EnumPath<com.example.msamemberapi.application.enums.AccountType> accountType = createEnum("accountType", com.example.msamemberapi.application.enums.AccountType.class);

    public final StringPath id = createString("id");

    public final DateTimePath<java.time.LocalDateTime> lastLoggedInAt = createDateTime("lastLoggedInAt", java.time.LocalDateTime.class);

    public final StringPath password = createString("password");

    public QMemberAccount(String variable) {
        super(MemberAccount.class, forVariable(variable));
    }

    public QMemberAccount(Path<? extends MemberAccount> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMemberAccount(PathMetadata metadata) {
        super(MemberAccount.class, metadata);
    }

}

