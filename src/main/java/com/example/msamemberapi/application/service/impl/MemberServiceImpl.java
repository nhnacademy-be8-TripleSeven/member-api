package com.example.msamemberapi.application.service.impl;

import com.example.msamemberapi.application.dto.request.JoinRequestDto;
import com.example.msamemberapi.application.dto.request.UpdatePasswordRequestDto;
import com.example.msamemberapi.application.dto.response.MemberAccountInfo;
import com.example.msamemberapi.application.dto.response.MemberAddressResponseDto;
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
import com.example.msamemberapi.application.feign.OrderFeignClient;
import com.example.msamemberapi.application.repository.GradePolicyRepository;
import com.example.msamemberapi.application.repository.MemberGradeHistoryRepository;
import com.example.msamemberapi.application.repository.MemberRepository;
import com.example.msamemberapi.application.service.MemberService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final GradePolicyRepository gradePolicyRepository;
    private final MemberGradeHistoryRepository memberGradeHistoryRepository;
    private final OrderFeignClient orderFeignClient;


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
    public MemberDto getMemberDTOById(Long memberId) {
        Optional<Member> member = memberRepository.findById(memberId);
        if (member.isEmpty()) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        return new MemberDto(member.get());
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

        //MemberGradeHistory gradeHistory = createMemberGradeHistory(member);
        //member.addGradeHistory(gradeHistory);
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
                .gradePolicy(GradePolicy.addGradePolicy(
                        "일반",
                        MemberGrade.REGULAR,
                        "회원 가입",
                        BigDecimal.valueOf(0.01),
                        0,
                        100000
                ))
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
    @Transactional(readOnly = true)
    public MemberDto getMember(Long userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_ID_NOT_FOUND));

        User user = member.getUser();
//        return MemberDto.builder()
//                .id(member.getId())
//                .name(member.getName())
//                .email(member.getEmail())
//                .phoneNumber(member.getPhone())
//                .points(user.getPoints())
//                .memberGrade(user.getMembership().name())
//                .build();
        return null;
    }


    @Override
    @Transactional
    public MemberDto updateMember(Long userId, MemberDto memberDto) {
        return updateMemberInfo(userId, memberDto);
    }

    @Override
    @Transactional
    public MemberDto updateMemberInfo(Long userId, MemberDto memberDto) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_ID_NOT_FOUND));

//        member.update(
//                memberDto.getEmail() != null ? memberDto.getEmail() : member.getEmail(),
//                memberDto.getPhoneNumber() != null ? memberDto.getPhoneNumber() : member.getPhone(),
//                memberDto.getAddress() != null ? memberDto.getAddress() : member.getAddress(),
//                memberDto.getDetailAddress() != null ? memberDto.getDetailAddress() : member.getDetailAddress(),
//                memberDto.getPassword() != null ? passwordEncoder.encode(memberDto.getPassword()) : member.getPassword()
//        );

        Member updatedMember = memberRepository.save(member);
        return MemberDto.fromEntity(updatedMember);
    }


    @Override
    public void deleteMember(Long id) {
        if (!memberRepository.existsById(id)) {
            throw new RuntimeException("Member not found with id: " + id);
        }
        memberRepository.deleteById(id);
    }


    @Override
    public boolean verifyPassword(Long memberId, String password) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_ID_NOT_FOUND));

        return passwordEncoder.matches(password, member.getMemberAccount().getPassword());
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
    public MemberDto findMemberInfoByUserId(Long userId) {
        Member member = memberRepository.findByMemberAccount_Id(String.valueOf(userId))
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_ID_NOT_FOUND));

        List<MemberAddressResponseDto> addressDTOs = member.getAddresses().stream()
                .map(address -> MemberAddressResponseDto.builder()
                        .id(address.getId())
                        .alias(address.getAlias())

                        .roadAddress(address.getRoadAddress())
                        .detail(address.getDetailAddress())
                        .postalCode(address.getPostcode())
                        .isDefault(address.getIsDefault())
                        .build())
                .collect(Collectors.toList());

//        return MemberDto.builder()
//                .id(member.getId())
//                .name(member.getName())
//                .email(member.getEmail())
//                .phoneNumber(member.getPhone())
//                .addresses(addressDTOs)
//                .build();
        return null;
    }


    @Override
    @Transactional
    public MemberGradeDto getMemberGrade(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_ID_NOT_FOUND));

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
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<MemberGradeHistoryDto> getGradeHistory(Long memberId) {
//        List<MemberGradeHistory> history = memberGradeHistoryRepository.findByMemberId(memberId);
//
//        return history.stream()
//                .map(record -> MemberGradeHistoryDto.builder()
//                        .gradeName(record.getGradePolicy().getName())
//                        .changedDate(record.getCreatedAt())
//                        .build())
//                .collect(Collectors.toList());
//    }

    private int calculateSpending(Long memberId) {

        return 300000; // 임시 값
    }


    @Override
    public MemberDto getMemberInfo(Long userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_ID_NOT_FOUND));

        return new MemberDto(member);
    }

    @Override
    public void saveMember(Member member) {
        memberRepository.save(member);
    }

    @Override
    public Member getMemberById(Long userId) {
        return memberRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_ID_NOT_FOUND));
    }


    @Override
    @Transactional
    public void updateMemberGrade() {
        List<Member> members = memberRepository.findAll();
        for (Member member : members) {

            Long amount = orderFeignClient.getNetAmount(member.getId());
            List<GradePolicy> gradePolicies = gradePolicyRepository.findAll();
            MemberGrade grade = MemberGrade.REGULAR;

            for (GradePolicy gradePolicy : gradePolicies) {
                int min = gradePolicy.getMin();
                int max = gradePolicy.getMax();
                if (min <= amount && amount < max) {
                    grade = gradePolicy.getGrade();
                }
            }

            member.updateGrade(grade);
        }
    }

    private boolean hasRole(List<String> roles, String roleValue) {

        for (String role : roles) {
            if (role.contains(roleValue)) {
                return true;
            }
        }

        return false;
    }

}

