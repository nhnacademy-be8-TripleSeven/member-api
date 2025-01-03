package com.example.msamemberapi.application.repository.querydsl.impl;

import com.example.msamemberapi.application.dto.response.MemberDto;
import com.example.msamemberapi.application.dto.response.QMemberDto;
import com.example.msamemberapi.application.entity.QMember;
import com.example.msamemberapi.application.enums.MemberGrade;
import com.example.msamemberapi.application.repository.querydsl.MemberRepositoryCustom;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<MemberDto> findMembers(String name, MemberGrade memberGrade, Pageable pageable) {
        QMember member = QMember.member;

        List<MemberDto> content = queryFactory
                .select(new QMemberDto(
                        member.id,
                        member.email,
                        member.user.phoneNumber,
                        member.user.name,
                        member.birth,
                        member.gender,
                        member.memberGrade
                ))
                .from(member)
                .where(
                        name != null ? member.user.name.containsIgnoreCase(name) : null,
                        memberGrade != null ? member.memberGrade.eq(memberGrade) : null
                )
                .orderBy(getOrderSpecifiers(pageable.getSort(), member))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .selectFrom(member)
                .where(
                        name != null ? member.user.name.containsIgnoreCase(name) : null,
                        memberGrade != null ? member.memberGrade.eq(memberGrade) : null
                )
                .fetchCount();

        return new PageImpl<>(content, pageable, total);
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort, QMember member) {
        return sort.stream()
                .map(order -> {
                    String property = order.getProperty();
                    PathBuilder<?> entityPath = new PathBuilder<>(member.getType(), member.getMetadata());
                    if (order.getProperty().equals("user.name")) {

                        return order.isAscending()
                                ? entityPath.getString(property).asc()
                                : entityPath.getString(property).desc();
                    } else if (order.getProperty().equals("birth")) {

                        return order.isAscending()
                                ? entityPath.getDate(property, Date.class).asc()
                                : entityPath.getDate(property, Date.class).desc();
                    }

                    return order.isAscending()
                            ? entityPath.getNumber("id", Long.class).asc()
                            : entityPath.getNumber("id", Long.class).desc();
                })
                .toArray(OrderSpecifier[]::new);
    }
}
