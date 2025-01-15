package com.example.msamemberapi.application.controller;

import com.example.msamemberapi.application.dto.response.AddressResponseDto;
import com.example.msamemberapi.application.dto.request.AddressRequestDto;
import com.example.msamemberapi.application.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/members/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @Operation(summary = "주소 목록 조회", description = "회원의 모든 주소를 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주소 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping
    public ResponseEntity<List<AddressResponseDto>> getAllAddresses(@RequestHeader("X-USER") String userId) {
        List<AddressResponseDto> addresses = addressService.getAllAddressesByUserId(Long.valueOf(userId));
        return ResponseEntity.ok(addresses);
    }

    @Operation(summary = "주소 추가", description = "새로운 주소를 추가합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "주소 추가 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @PostMapping
    public ResponseEntity<AddressResponseDto> createAddress(
            @RequestHeader("X-USER") String userId,
            @Valid @RequestBody AddressRequestDto requestDto) {
        AddressResponseDto createdAddress = addressService.createAddress(Long.valueOf(userId), requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAddress);
    }

    @Operation(summary = "주소 삭제", description = "주소 ID를 통해 특정 주소를 삭제합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주소 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "해당 주소를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "주소 검색 및 저장", description = "카카오 API를 통해 주소를 검색하고 저장합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주소 검색 및 저장 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/search/save")
    public ResponseEntity<Void> searchAndSaveAddress(
            @RequestHeader("X-USER") String userId,
            @RequestParam String keyword) {
        addressService.saveAddressFromKakao(userId, keyword, "Alias", "Detail Address");
        return ResponseEntity.ok().build();
    }
}