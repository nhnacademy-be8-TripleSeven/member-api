package com.example.msamemberapi.application.service;

import com.example.msamemberapi.application.dto.request.Oauth2MemberRequestDto;
import com.example.msamemberapi.application.dto.response.MemberAccountInfo;

public interface Oauth2MemberService {

    MemberAccountInfo saveMemberDetail(Oauth2MemberRequestDto memberRequestDto);
}
