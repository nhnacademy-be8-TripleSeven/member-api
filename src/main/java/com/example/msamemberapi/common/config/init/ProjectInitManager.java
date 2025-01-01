package com.example.msamemberapi.common.config.init;

import com.example.msamemberapi.application.entity.Member;
import com.example.msamemberapi.application.entity.MemberAccount;
import com.example.msamemberapi.application.entity.User;
import com.example.msamemberapi.application.enums.AccountType;
import com.example.msamemberapi.application.enums.Gender;
import com.example.msamemberapi.application.enums.MemberGrade;
import com.example.msamemberapi.application.enums.MemberRole;
import com.example.msamemberapi.application.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProjectInitManager {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initAdminMember() {

        if (!memberRepository.existsByMemberAccount_Id("admin")) {
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

            admin.addRole(MemberRole.ADMIN);

            memberRepository.save(admin);
        }

    }
}
