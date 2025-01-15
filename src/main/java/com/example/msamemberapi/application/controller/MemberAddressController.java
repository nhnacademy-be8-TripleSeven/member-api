package com.example.msamemberapi.application.controller;

import com.example.msamemberapi.application.dto.request.MemberAddressRequestDto;
import com.example.msamemberapi.application.dto.response.MemberAddressResponseDto;
import com.example.msamemberapi.application.service.MemberAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member-Address", description = "회원 주소 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/members/{memberId}/addresses")
public class MemberAddressController {

    private final MemberAddressService memberAddressService;

    @Operation(summary = "회원 주소 조회", description = "특정 회원의 모든 주소 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주소 조회 성공"),
            @ApiResponse(responseCode = "404", description = "회원 또는 주소를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping
    public ResponseEntity<List<MemberAddressResponseDto>> getMemberAddresses(@PathVariable Long memberId) {
        List<MemberAddressResponseDto> addresses = memberAddressService.getAddressesByMemberId(memberId);
        return ResponseEntity.ok(addresses);
    }

    @Operation(summary = "회원 주소 추가", description = "회원에게 도로명주소를 추가 (최대 10개)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "주소 추가 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 요청"),
            @ApiResponse(responseCode = "404", description = "회원 또는 주소를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @PostMapping
    public ResponseEntity<MemberAddressResponseDto> addAddressToMember(
            @PathVariable Long memberId,
            @Valid @RequestBody MemberAddressRequestDto requestDto) {
        MemberAddressResponseDto addedAddress = memberAddressService.addAddressToMember(memberId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedAddress);
    }

    
    @Operation(summary = "회원 주소 삭제", description = "회원의 특정 주소 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "주소 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "주소를 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> removeAddressFromMember(@PathVariable Long memberId, @PathVariable Long addressId) {
        memberAddressService.removeAddressFromMember(memberId, addressId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{addressId}")
    @Operation(summary = "주소 별칭 및 기본 주소 수정", description = "특정 주소의 별칭과 기본 주소 여부를 수정합니다.")
    public ResponseEntity<Void> updateAliasAndDefault(
            @PathVariable Long memberId,
            @PathVariable Long addressId,
            @RequestBody Map<String, Object> request) {

        String alias = (String) request.get("alias");
        Boolean isDefault = (Boolean) request.get("isDefault");

        memberAddressService.updateAlias(addressId, alias, isDefault);

        return ResponseEntity.ok().build();
    }
}