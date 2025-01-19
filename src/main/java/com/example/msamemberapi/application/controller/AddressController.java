package com.example.msamemberapi.application.controller;

import com.example.msamemberapi.application.dto.response.AddressResponseDto;
import com.example.msamemberapi.application.dto.request.AddressRequestDto;
import com.example.msamemberapi.application.dto.response.MemberDto;
import com.example.msamemberapi.application.entity.Address;
import com.example.msamemberapi.application.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.Map;

import java.util.List;

@Tag(name = "Address", description = "주소 관리 API")
@RestController
@RequestMapping("/api/members/{userId}/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @Operation(summary = "주소 목록 조회", description = "회원의 모든 주소 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주소 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })

    @GetMapping("/addresses/manage")
    public ResponseEntity<List<AddressResponseDto>> getAllAddresses(@PathVariable Long userId) {
        List<AddressResponseDto> addresses = addressService.getAllAddressesByUserId(userId);
        return ResponseEntity.ok(addresses);
    }

    @Operation(summary = "주소 추가", description = "새로운 주소 추가")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "주소 추가 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })

    @PostMapping("/manage")
    public ResponseEntity<Void> addOrUpdateAddress(@PathVariable Long userId,
                                                   @RequestBody AddressRequestDto requestDto) {
        addressService.addOrUpdateAddress(userId, requestDto.toEntity(userId));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @Operation(summary = "주소 수정", description = "주소 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주소 수정 성공"),
            @ApiResponse(responseCode = "404", description = "주소를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @PutMapping("/manage/{addressId}")
    public ResponseEntity<Void> updateAddress(@PathVariable Long userId, @PathVariable Long addressId,
                                              @RequestBody AddressRequestDto requestDto) {
        addressService.addOrUpdateAddress(userId, requestDto.toEntity(userId));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "주소 삭제", description = "주소 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주소 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "주소를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @DeleteMapping("/manage/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long userId,
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
            @PathVariable Long userId,
            @RequestBody Map<String, String> payload) {
        String keyword = payload.get("keyword");
        String alias = payload.getOrDefault("alias", "Default Alias");
        String detailAddress = payload.getOrDefault("detailAddress", "Default Detail Address");
        addressService.saveAddressFromKakao(userId, keyword, alias, detailAddress);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}