package com.example.msamemberapi.application.controller;

import com.example.msamemberapi.application.dto.request.AddressRequestDto;
import com.example.msamemberapi.application.dto.response.AddressResponseDto;
import com.example.msamemberapi.application.dto.response.KakaoAddressResponseDto;
import com.example.msamemberapi.application.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Address", description = "주소 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/addresses")
public class AddressController {

    private final AddressService addressService;

    @Operation(summary = "주소 목록 조회", description = "모든 주소를 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주소 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping
    public ResponseEntity<List<AddressResponseDto>> getAllAddresses() {
        List<AddressResponseDto> addresses = addressService.getAllAddresses();
        return ResponseEntity.ok(addresses);
    }

    @Operation(summary = "주소 생성", description = "새로운 주소를 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "주소 생성 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @PostMapping
    public ResponseEntity<AddressResponseDto> createAddress(@Valid @RequestBody AddressRequestDto requestDto) {
        AddressResponseDto createdAddress = addressService.createAddress(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAddress);
    }

    @Operation(summary = "주소 수정", description = "기존 주소를 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주소 수정 성공"),
            @ApiResponse(responseCode = "404", description = "주소를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @PutMapping("/{id}")
    public ResponseEntity<AddressResponseDto> updateAddress(@PathVariable Long id, @Valid @RequestBody AddressRequestDto requestDto) {
        AddressResponseDto updatedAddress = addressService.updateAddress(id, requestDto);
        return ResponseEntity.ok(updatedAddress);
    }

    @Operation(summary = "주소 삭제", description = "특정 주소를 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "주소 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "주소를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "도로명 주소 검색", description = "카카오 API를 통해 도로명 주소 검색")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/search")
    public ResponseEntity<List<KakaoAddressResponseDto>> searchAddress(@RequestParam String keyword) {
        List<KakaoAddressResponseDto> results = addressService.searchRoadAddress(keyword);
        return ResponseEntity.ok(results);
    }
}