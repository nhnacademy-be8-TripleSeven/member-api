package com.example.msamemberapi.application.service;

import com.example.msamemberapi.application.dto.request.Oauth2MemberRequestDto;

public interface Oauth2MemberService {

    void saveMemberDetail(Oauth2MemberRequestDto memberRequestDto);
}
