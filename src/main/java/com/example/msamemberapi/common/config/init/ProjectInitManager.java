package com.example.msamemberapi.common.config.init;

import com.example.msamemberapi.application.entity.Member;
import com.example.msamemberapi.application.entity.MemberAccount;
import com.example.msamemberapi.application.entity.User;
import com.example.msamemberapi.application.enums.AccountType;
import com.example.msamemberapi.application.enums.Gender;
import com.example.msamemberapi.application.enums.MemberGrade;
import com.example.msamemberapi.application.enums.MemberRole;
import com.example.msamemberapi.application.repository.MemberRepository;
import jakarta.ws.rs.core.Application;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProjectInitManager {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initMembers() {
        List<Member> members = new ArrayList<>();

        if (!memberRepository.existsByMemberAccount_Id("1")) {
            for (int i = 0; i < 21; i++) {
                members.add(createMember(i));
            }
        }

        if (!memberRepository.existsByMemberAccount_Id("inactive")) {
            Member inActiveMember = createInActiveMember();
            members.add(inActiveMember);
        }
        memberRepository.saveAll(members);
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initAdminMember() {

        if (!memberRepository.existsByMemberAccount_Id("admin")) {
            Member admin = createAdmin();
            memberRepository.save(admin);
        }
    }

    private Member createAdmin() {
        MemberAccount account = MemberAccount.builder()
                .id("admin")
                .password(passwordEncoder.encode("admin"))
                .accountType(AccountType.ADMIN)
                .build();

        User user = User.builder()
                .name("admin")
                .phoneNumber("admin")
                .build();

        Member admin = Member.builder()
                .memberAccount(account)
                .memberGrade(MemberGrade.REGULAR)
                .gender(Gender.MALE)
                .email("admin")
                .user(user)
                .birth(new Date())
                .build();

        admin.addRole(MemberRole.ADMIN_USER);
        return admin;
    }

    private Member createMember(int i) {
        MemberAccount account = MemberAccount.builder()
                .id(String.valueOf(i))
                .password(passwordEncoder.encode(String.valueOf(i)))
                .accountType(AccountType.REGISTERED)
                .build();

        User user = User.builder()
                .name("MEMBER".concat(String.valueOf(i)))
                .phoneNumber(String.valueOf(i))
                .build();

        Member member = Member.builder()
                .memberAccount(account)
                .memberGrade(MemberGrade.REGULAR)
                .gender(Gender.MALE)
                .email(String.format("%s@example.com", i))
                .user(user)
                .birth(new Date())
                .build();

        member.addRole(MemberRole.USER);
        return member;
    }

    private Member createInActiveMember() {
        MemberAccount account = MemberAccount.builder()
                .id("inactive")
                .password(passwordEncoder.encode("1234"))
                .accountType(AccountType.REGISTERED)
                .build();

        User user = User.builder()
                .name("INACTIVE_MEMBER")
                .phoneNumber("12311231212")
                .build();

        Member member = Member.builder()
                .memberAccount(account)
                .memberGrade(MemberGrade.REGULAR)
                .gender(Gender.MALE)
                .email("wjdcks25@naver.com")
                .user(user)
                .birth(new Date())
                .build();

        member.addRole(MemberRole.INACTIVE);
        return member;
    }
}
