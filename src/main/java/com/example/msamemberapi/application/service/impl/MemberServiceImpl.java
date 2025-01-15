package com.example.msamemberapi.application.service.impl;

import com.example.msamemberapi.application.dto.request.JoinRequestDto;
import com.example.msamemberapi.application.dto.request.UpdatePasswordRequestDto;
import com.example.msamemberapi.application.dto.response.MemberAccountInfo;
import com.example.msamemberapi.application.dto.response.MemberAuthInfo;
import com.example.msamemberapi.application.dto.response.MemberDto;
import com.example.msamemberapi.application.dto.response.MemberGradeDto;
import com.example.msamemberapi.application.dto.response.MemberGradeHistoryDto;
import com.example.msamemberapi.application.entity.Member;
import com.example.msamemberapi.application.entity.MemberAccount;
import com.example.msamemberapi.application.entity.MemberGradeHistory;
import com.example.msamemberapi.application.entity.GradePolicy;
import com.example.msamemberapi.application.entity.User;
import com.example.msamemberapi.application.enums.AccountType;
import com.example.msamemberapi.application.enums.MemberGrade;
import com.example.msamemberapi.application.enums.MemberRole;
import com.example.msamemberapi.application.error.CustomException;
import com.example.msamemberapi.application.error.ErrorCode;
import com.example.msamemberapi.application.repository.GradePolicyRepository;
import com.example.msamemberapi.application.repository.MemberGradeHistoryRepository;
import com.example.msamemberapi.application.repository.MemberRepository;
import com.example.msamemberapi.application.service.MemberService;

import java.time.LocalDate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final GradePolicyRepository gradePolicyRepository;
    private final MemberGradeHistoryRepository memberGradeHistoryRepository;


    @Override
    @Transactional
    public MemberDto join(JoinRequestDto joinRequestDto) {

        validateUniqueMember(joinRequestDto);
        MemberAccount memberAccount = createMemberAccount(joinRequestDto);
        User user = createUser(joinRequestDto);
        Member member = createMember(joinRequestDto, memberAccount, user);
        return new MemberDto(memberRepository.save(member));
    }

    @Override
    @Transactional(readOnly = true)
    public MemberAuthInfo findByMemberId(String loginId) {
        Member member = memberRepository.findByMemberAccount_Id(loginId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_ID_NOT_FOUND));
        return new MemberAuthInfo(member);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberAccountInfo getMemberAccountByEmail(String email) {
        Member member =
                memberRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.EMAIL_NOT_FOUND));

        if (!member.getMemberAccount().getAccountType().equals(AccountType.REGISTERED)) {
            throw new CustomException(ErrorCode.EMAIL_NOT_FOUND);
        }

        return new MemberAccountInfo(member.getMemberAccount());
    }

    @Override
    @Transactional(readOnly = true)
    public MemberAccountInfo getMemberAccountByPhoneNumber(String phoneNumber) {
        Member member = memberRepository.findByUserPhoneNumber(phoneNumber)
                .orElseThrow(() -> new CustomException(ErrorCode.PHONE_NOT_FOUND));

        if (!member.getMemberAccount().getAccountType().equals(AccountType.REGISTERED)) {
            throw new CustomException(ErrorCode.PHONE_NOT_FOUND);
        }

        return new MemberAccountInfo(member.getMemberAccount());
    }

    @Override
    @Transactional(readOnly = true)
    public void validateMatchingLoginIdAndEmail(String email, String loginId) {
        if (!getMemberAccountByEmail(email).getLoginId().equals(loginId)) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
    }

    @Override
    @Transactional
    public void updateMemberPassword(UpdatePasswordRequestDto updatePasswordRequestDto) {
        Member member = memberRepository.findByEmail(updatePasswordRequestDto.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.EMAIL_NOT_FOUND));

        if (!member.getMemberAccount().getAccountType().equals(AccountType.REGISTERED)) {
            throw new CustomException(ErrorCode.EMAIL_NOT_FOUND);
        }

        MemberAccount memberAccount = member.getMemberAccount();
        memberAccount.changePassword(passwordEncoder.encode(updatePasswordRequestDto.getNewPassword()));
    }

    @Override
    public Page<MemberDto> getMembers(String name, MemberGrade memberGrade, Pageable pageable) {
        return memberRepository.findMembers(name, memberGrade, pageable);
    }

    @Override
    @Transactional
    public void updateLastLoggedInAt(Long userId) {
        Member member = memberRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));
        member.getMemberAccount().updateLastLoggedInAt();
    }

    @Override
    @Transactional
    public MemberAuthInfo findByMemberId(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_ID_NOT_FOUND));
        return new MemberAuthInfo(member);
    }

    private Member createMember(JoinRequestDto joinRequestDto, MemberAccount memberAccount, User user) {
        Member member = Member.builder()
                .memberAccount(memberAccount)
                .user(user)
                .birth(joinRequestDto.getBirth())
                .memberGrade(MemberGrade.REGULAR)
                .gender(joinRequestDto.getGender())
                .email(joinRequestDto.getEmail())
                .gradeHistories(new ArrayList<>())
                .build();

        MemberGradeHistory gradeHistory = createMemberGradeHistory(member);
        member.addGradeHistory(gradeHistory);
        member.addRole(MemberRole.USER);

        return member;
    }

    private User createUser(JoinRequestDto joinRequestDto) {
        return User.builder()
                .name(joinRequestDto.getName())
                .phoneNumber(joinRequestDto.getPhoneNumber())
                .build();
    }

    private MemberGradeHistory createMemberGradeHistory(Member member) {
        return MemberGradeHistory.builder()
                .createdAt(LocalDate.now())
                .gradePolicy(gradePolicyRepository.findByGrade(MemberGrade.ROYAL).orElseThrow(() ->
                        new CustomException(ErrorCode.GRADE_POLICY_NOT_FOUND)))
                .member(member)
                .build();
    }

    private MemberAccount createMemberAccount(JoinRequestDto joinRequestDto) {
        return MemberAccount.builder()
                .id(joinRequestDto.getLoginId())
                .password(passwordEncoder.encode(joinRequestDto.getPassword()))
                .accountType(AccountType.REGISTERED)
                .build();
    }

    private void validateUniqueMember(JoinRequestDto joinRequestDto) {

        if (memberRepository.existsByMemberAccount_Id(joinRequestDto.getLoginId())) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_LOGIN_ID);
        }

        if (memberRepository.existsByEmail(joinRequestDto.getEmail())) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_EMAIL);
        }

        if (memberRepository.existsByUserPhoneNumber(joinRequestDto.getPhoneNumber())) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_PHONE);
        }
    }


    @Override
    public MemberDto getMember(Long id) {
        return memberRepository.findById(id)
                .map(this::mapToResponseDto)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + id));
    }

    @Override
    public void updateMember(Long id, MemberDto memberDto) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + id));
        member.update(memberDto.getName(), memberDto.getEmail(), memberDto.getPhoneNumber());
        memberRepository.save(member);
    }

    @Override
    public void deleteMember(Long id) {
        if (!memberRepository.existsById(id)) {
            throw new RuntimeException("Member not found with id: " + id);
        }
        memberRepository.deleteById(id);
    }

    @Transactional
    public void updateMemberInfo(String memberId, MemberDto memberDto) {
        Member member = memberRepository.findByMemberAccount_Id(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_ID_NOT_FOUND));

        if (memberDto.getEmail() != null && !memberDto.getEmail().isEmpty()) {
            member.updateEmail(memberDto.getEmail());
        }
        if (memberDto.getName() != null && !memberDto.getName().isEmpty()) {
            member.updateName(memberDto.getName());
        }
        if (memberDto.getPhoneNumber() != null && !memberDto.getPhoneNumber().isEmpty()) {
            member.updatePhoneNumber(memberDto.getPhoneNumber());
        }
        if (memberDto.getPassword() != null && !memberDto.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(memberDto.getPassword());
            member.updatePassword(encodedPassword);
        }

        memberRepository.save(member);
    }

    private MemberDto mapToResponseDto(Member member) {
        return MemberDto.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .phoneNumber(member.getPhone())
                .build();
    }

    @Override
    @Transactional
    public void quitMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));
        if (!member.getRoles().contains(MemberRole.USER.name())) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        member.removeRole(MemberRole.USER);
        member.addRole(MemberRole.QUIT);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberDto findMemberInfoByUserId(String userId) {
        Member member = memberRepository.findByMemberAccount_Id(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_ID_NOT_FOUND));
        return new MemberDto(member);
    }

    @Override
    public MemberGradeDto getMemberGrade(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_ID_NOT_FOUND));

        // 최근 3개월 순수 소비 금액 계산
        int spending = calculateSpending(memberId);

        // 현재 등급과 다음 등급 계산
        GradePolicy currentGrade = gradePolicyRepository.findCurrentGrade(spending);
        GradePolicy nextGrade = gradePolicyRepository.findNextGrade(spending);

        return MemberGradeDto.builder()
                .currentGrade(currentGrade.getGrade().name()) // 현재 등급
                .nextGrade(nextGrade != null ? nextGrade.getGrade().name() : "최고 등급") // 다음 등급
                .currentSpending(spending) // 현재 소비 금액
                .requiredForNextGrade(nextGrade != null ? nextGrade.getMin() - spending : 0) // 다음 등급까지 필요한 금액
                .build();
    }

    @Override
    public List<MemberGradeHistoryDto> getGradeHistory(Long memberId) {
        List<MemberGradeHistory> history = memberGradeHistoryRepository.findByMemberId(memberId);

        return history.stream()
                .map(record -> MemberGradeHistoryDto.builder()
                        .gradeName(record.getGradePolicy().getName())
                        .changedDate(record.getCreatedAt()) // 필드 이름 수정
                        .build())
                .collect(Collectors.toList());
    }

    private int calculateSpending(Long memberId) {
        // TODO: 최근 3개월간의 주문 데이터를 조회하여 순수 소비 금액을 합산합니다.
        return 300000; //  임시 값
    }
}