package com.example.msamemberapi.application.controller;

import com.example.msamemberapi.application.dto.request.MemberUpdateRequestDto;
import com.example.msamemberapi.application.dto.response.AddressResponseDto;
import com.example.msamemberapi.application.dto.request.AddressRequestDto;
import com.example.msamemberapi.application.dto.response.MemberDto;
import com.example.msamemberapi.application.service.AddressService;
import com.example.msamemberapi.application.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import com.example.msamemberapi.application.dto.request.MemberAddressRequestDto;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.Map;

import java.util.List;

@RequiredArgsConstructor
@Tag(name = "Address", description = "주소 관리 API")
@RestController
@RequestMapping("/api/members/{memberId}/addresses")
public class AddressController {
    private final AddressService addressService;


    @Operation(summary = "주소 목록 조회", description = "회원의 모든 주소 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주소 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })

    @GetMapping("/manage")
    public ResponseEntity<List<AddressResponseDto>> getAllAddresses(@PathVariable Long memberId) {
        List<AddressResponseDto> addresses = addressService.getAllAddressesById(memberId);
        return ResponseEntity.ok(addresses);
    }

    @Operation(summary = "주소 수정", description = "주소 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주소 수정 성공"),
            @ApiResponse(responseCode = "404", description = "주소를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @PutMapping
    public ResponseEntity<AddressResponseDto> updateAddress(
            @PathVariable Long memberId,
            @RequestBody @Valid AddressRequestDto requestDto) {
        AddressResponseDto response = addressService.updateAddress(memberId, requestDto);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "주소 삭제", description = "주소 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주소 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "주소를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @DeleteMapping("/manage/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long memberId,
                                              @PathVariable Long addressId) {
        addressService.deleteAddress(addressId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "주소 검색 및 저장", description = "카카오 API를 통해 주소 검색 및 저")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주소 검색 및 저장 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @PostMapping("/search/save")
    public ResponseEntity<Void> searchAndSaveAddress(
            @PathVariable Long memberId,
            @RequestBody @Valid MemberAddressRequestDto requestDto) {
        addressService.saveAddressFromKakao(memberId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

//    @PostMapping("/search/save")
//    public ResponseEntity<Void> searchAndSaveAddress(
//            @PathVariable Long memberId,
//            @RequestBody Map<String, String> payload) {
//        String keyword = payload.get("keyword");
//        String alias = payload.getOrDefault("alias", "Default Alias");
//        String detailAddress = payload.getOrDefault("detailAddress", "Default Detail Address");
//        addressService.saveAddressFromKakao(memberId, keyword, alias, detailAddress);
//        return ResponseEntity.status(HttpStatus.CREATED).build();
//    }
    
}