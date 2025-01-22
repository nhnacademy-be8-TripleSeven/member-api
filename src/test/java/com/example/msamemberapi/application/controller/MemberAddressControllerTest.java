package com.example.msamemberapi.application.controller;

import com.example.msamemberapi.application.dto.request.MemberAddressRequestDto;
import com.example.msamemberapi.application.dto.response.MemberAddressResponseDto;
import com.example.msamemberapi.application.service.MemberAddressService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberAddressController.class)
class MemberAddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberAddressService memberAddressService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원 주소 조회")
    void getMemberAddresses_success() throws Exception {
        Long memberId = 1L;
        List<MemberAddressResponseDto> mockResponse = List.of(
                MemberAddressResponseDto.builder()
                        .id(1L)
                        .alias("Alias1")
                        .detail("Detail Address 1")
                        .roadAddress("Road Address 1")
                        .postalCode("12345")
                        .isDefault(true)
                        .build(),
                MemberAddressResponseDto.builder()
                        .id(2L)
                        .alias("Alias2")
                        .detail("Detail Address 2")
                        .roadAddress("Road Address 2")
                        .postalCode("67890")
                        .isDefault(false)
                        .build()
        );

        when(memberAddressService.getAddressesByMemberId(memberId)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/members/{memberId}/addresses", memberId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].alias").value("Alias1"))
                .andExpect(jsonPath("$[1].alias").value("Alias2"));
    }

    @Test
    @DisplayName("회원 주소 추가")
    void addAddressToMember_success() throws Exception {
        Long memberId = 1L;
        MemberAddressRequestDto requestDto = MemberAddressRequestDto.builder()
                .alias("New Alias")
                .roadAddress("New Road Address")
                .postalCode("54321")
                .detail("New Detail")
                .isDefault(true)
                .build();
        MemberAddressResponseDto responseDto = MemberAddressResponseDto.builder()
                .id(1L)
                .alias("New Alias")
                .roadAddress("New Road Address")
                .postalCode("54321")
                .detail("New Detail")
                .isDefault(true)
                .build();

        when(memberAddressService.addAddressToMember(Mockito.eq(memberId), Mockito.any(MemberAddressRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/api/members/{memberId}/addresses", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.alias").value("New Alias"))
                .andExpect(jsonPath("$.roadAddress").value("New Road Address"))
                .andExpect(jsonPath("$.postcode").value("54321")) // 변경된 JSON 경로
                .andExpect(jsonPath("$.detailAddress").value("New Detail"))
                .andExpect(jsonPath("$.isDefault").value(true));
    }

    @Test
    @DisplayName("회원 주소 삭제")
    void removeAddressFromMember_success() throws Exception {
        Long memberId = 1L;
        Long addressId = 1L;

        doNothing().when(memberAddressService).removeAddressFromMember(memberId, addressId);

        mockMvc.perform(delete("/api/members/{memberId}/addresses/{addressId}", memberId, addressId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("주소 별칭 및 기본 주소 수정")
    void updateAliasAndDefault_success() throws Exception {
        Long memberId = 1L;
        Long addressId = 1L;
        Map<String, Object> request = Map.of(
                "alias", "Updated Alias",
                "isDefault", true
        );

        doNothing().when(memberAddressService).updateAlias(addressId, "Updated Alias", true);

        mockMvc.perform(patch("/api/members/{memberId}/addresses/{addressId}", memberId, addressId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}