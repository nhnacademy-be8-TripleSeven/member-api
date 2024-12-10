package com.example.msamemberapi.application.service;

import com.example.msamemberapi.application.entity.Member;

public interface MemberService {

    Member join(Member member);
    Member findByMemberId(String memberId);
    void deleteByMemberId(String memberId);

}
