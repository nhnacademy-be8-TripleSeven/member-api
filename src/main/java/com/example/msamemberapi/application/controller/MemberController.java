package com.example.msamemberapi.application.controller;

import com.example.msamemberapi.application.dto.request.AddressRequestDto;
import com.example.msamemberapi.application.dto.request.GradeUpdateRequestDto;
import com.example.msamemberapi.application.dto.request.MemberUpdateRequestDto;
import com.example.msamemberapi.application.dto.response.AddressResponseDto;
import com.example.msamemberapi.application.dto.response.MemberDto;
import com.example.msamemberapi.application.dto.response.MemberGradeDto;
import com.example.msamemberapi.application.error.CustomException;
import com.example.msamemberapi.application.entity.Address;
import com.example.msamemberapi.application.entity.Member;
import com.example.msamemberapi.application.entity.MemberDetails;
import com.example.msamemberapi.application.enums.MemberGrade;
import com.example.msamemberapi.application.error.ErrorCode;
import com.example.msamemberapi.application.service.AddressService;
import com.example.msamemberapi.application.service.GradePolicyService;
import com.example.msamemberapi.application.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Member", description = "회원 Api")
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private GradePolicyService gradePolicyService;
    private final PasswordEncoder passwordEncoder;
    private final AddressService addressService;


    @Operation(summary = "멤버 정보 조회", description = "마이페이지에서 멤버 상세 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "멤버 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "해당 멤버를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })

    @GetMapping("/info")
    public ResponseEntity<MemberDto> getMemberInfo(@RequestHeader("X-USER") Long userId) {
        MemberDto memberInfo = memberService.getMemberInfo(userId);
        return ResponseEntity.ok(memberInfo);
    }


    @Operation(summary = "멤버 삭제", description = "멤버 ID를 통해 멤버를 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "멤버 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "해당 멤버를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestHeader("X-USER") Long userId) {
        memberService.quitMember(userId);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "멤버 조회", description = "멤버 ID를 통해 멤버 정보를 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "멤버 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 멤버를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MemberDto> getMemberById(@PathVariable Long id) {
        MemberDto member = memberService.findMemberInfoByUserId(id);
        return ResponseEntity.ok(member);
    }


    @Operation(summary = "멤버 수정", description = "멤버 ID를 통해 멤버 정보를 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "멤버 수정 성공"),
            @ApiResponse(responseCode = "404", description = "해당 멤버를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @PutMapping("/{userId}/edit")
    public ResponseEntity<MemberDto> updateMember(@PathVariable Long userId,
                                                  @RequestBody MemberUpdateRequestDto memberUpdateRequestDto) {
        Member member = memberService.getMemberById(userId);

        if (memberUpdateRequestDto.getNewPassword() != null && !memberUpdateRequestDto.getNewPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(memberUpdateRequestDto.getNewPassword());
            member.updatePassword(encodedPassword);
        }

        member.update(memberUpdateRequestDto.getEmail(), memberUpdateRequestDto.getPhone(),
                memberUpdateRequestDto.getAddress(), memberUpdateRequestDto.getDetailAddress());
        memberService.saveMember(member);

        return ResponseEntity.ok(new MemberDto(member));
    }

    @GetMapping("/verify-password")
    public String verifyPasswordPage(Model model, @RequestHeader("X-USER") String userId) {
        MemberDto memberDto = memberService.findMemberInfoByUserId(Long.valueOf(userId));
        model.addAttribute("user", memberDto);
        return "verify-password";
    }

    @Operation(summary = "비밀번호 검증", description = "사용자가 입력한 비밀번호를 검증")
    @PostMapping("/api/members/{userId}/verify-password")
    public ResponseEntity<?> checkPassword(
            @PathVariable Long userId,
            @RequestBody Map<String, String> payload,
            HttpSession session) {
        String password = payload.get("password");

        boolean isValid = memberService.verifyPassword(userId, password);
        if (!isValid) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("비밀번호가 틀렸습니다.");
        }

        session.setAttribute("passwordVerified", true);

        String redirectUrl = (String) session.getAttribute("redirectAfterPasswordCheck");
        session.removeAttribute("redirectAfterPasswordCheck");
        return ResponseEntity.ok(redirectUrl != null ? redirectUrl : "/frontend/member/edit");
    }

    @Operation(summary = "멤버 정보 수정", description = "사용자가 자신의 정보를 수정")
    @PostMapping("/edit")
    public ResponseEntity<Void> updateMemberInfo(
            @RequestHeader("X-USER") String userId,
            @RequestBody MemberUpdateRequestDto memberDto) {
        memberService.updateMemberInfo(Long.valueOf(userId), memberDto.toMemberDto());
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "회원 등급 변경", description = "회원 등급을 변경합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "등급 변경 성공"),
            @ApiResponse(responseCode = "404", description = "회원 정보 없음")
    })
    @PutMapping("/updateGrade/{id}")
    public ResponseEntity<MemberGradeDto> updateGrade(
            @PathVariable Long id,
            @RequestBody @Valid GradeUpdateRequestDto gradeUpdateRequestDto) {

        MemberGrade memberGrade = gradeUpdateRequestDto.getGrade();
        MemberGradeDto memberGradeDto = gradePolicyService.updateGrade(id, memberGrade);
        return ResponseEntity.ok().body(memberGradeDto);
    }


    @Operation(summary = "비밀번호 확인 페이지 반환", description = "비밀번호 확인 페이지")
    @GetMapping("/frontend/pwd-check")
    public String getPasswordCheckPage(HttpSession session) {
        Boolean passwordVerified = (Boolean) session.getAttribute("passwordVerified");
        if (passwordVerified != null && passwordVerified) {
            String redirectUrl = (String) session.getAttribute("redirectAfterPasswordCheck");
            session.removeAttribute("redirectAfterPasswordCheck");
            return "redirect:" + (redirectUrl != null ? redirectUrl : "/frontend/member/edit");
        }
        return "member/pwd-check";
    }

    @GetMapping("/frontend/member/edit")
    public String getMemberEditPage(@RequestHeader(value = "X-USER", required = false) Long userId,
                                    HttpSession session, Model model) {
        if (userId == null) {
            return "auth/login";
        }

        Boolean passwordVerified = (Boolean) session.getAttribute("passwordVerified");
        if (passwordVerified == null || !passwordVerified) {
            session.setAttribute("redirectAfterPasswordCheck", "/frontend/member/edit");
            return "redirect:/frontend/member/pwd-check";
        }

        MemberDto member = memberService.getMemberInfo(userId);
        model.addAttribute("member", member);
        return "member/member-edit";
    }


    @PostMapping("/frontend/member/edit")
    public String updateMemberInfo(@RequestHeader("X-USER") Long userId,
                                   @ModelAttribute MemberDto memberDTO,
                                   Model model) {
        if (memberDTO.getName() == null || memberDTO.getEmail() == null) {
            model.addAttribute("error", "이름과 이메일을 입력해주세요.");
            return "member/member-edit";
        }

        memberService.updateMemberInfo(userId, memberDTO);
        model.addAttribute("member", memberDTO);
        return "member/member-edit";
    }

    @Operation(summary = "주소 관리 페이지", description = "주소 관리 페이지 조회")
    @GetMapping("/{userId}/addresses/manage")
    public String getAddressManagePage(@PathVariable Long userId,
                                       HttpSession session, Model model) {
        Boolean passwordVerified = (Boolean) session.getAttribute("passwordVerified");
        if (passwordVerified == null || !passwordVerified) {
            session.setAttribute("redirectAfterPasswordCheck", "/frontend/member/address-manage");
            return "redirect:/frontend/member/pwd-check";  // 비밀번호 확인 후 주소 관리 페이지로 리다이렉트
        }

        List<AddressResponseDto> addresses = addressService.getAllAddressesByUserId(userId);
        model.addAttribute("addresses", addresses);
        return "member/address-manage";
    }
}