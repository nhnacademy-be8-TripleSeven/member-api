package com.example.msamemberapi.application.controller;

import com.example.msamemberapi.application.dto.request.AddressRequestDto;
import com.example.msamemberapi.application.dto.request.MemberAddressRequestDto;
import com.example.msamemberapi.application.dto.response.AddressResponseDto;
import com.example.msamemberapi.application.dto.response.MemberAddressResponseDto;
import com.example.msamemberapi.application.service.AddressService;
import com.example.msamemberapi.application.service.MemberAddressService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({AddressController.class, MemberAddressController.class})
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AddressService addressService;

    @MockBean
    private MemberAddressService memberAddressService;

    @Test
    @DisplayName("주소 목록 조회 - 성공")
    void getAllAddresses_Success() throws Exception {
        List<AddressResponseDto> addresses = Arrays.asList(
                new AddressResponseDto(1L, "12345", "서울특별시 강남구 삼성동", "상세주소1", "Alias1"),
                new AddressResponseDto(2L, "54321", "서울특별시 서초구 서초동", "상세주소2", "Alias2")
        );

        when(addressService.getAllAddressesByUserId(1L)).thenReturn(addresses);

        mockMvc.perform(get("/api/members/addresses")
                        .header("X-USER", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].postcode").value("12345"))
                .andExpect(jsonPath("$[0].roadAddress").value("서울특별시 강남구 삼성동"))
                .andExpect(jsonPath("$[1].id").value(2));
    }


    @Test
    @DisplayName("주소 추가 - 성공")
    void createAddress_Success() throws Exception {
        AddressRequestDto requestDto = new AddressRequestDto("12345", "서울특별시 강남구 삼성동", "상세주소", "Alias", true);
        AddressResponseDto responseDto = new AddressResponseDto(1L, "12345", "서울특별시 강남구 삼성동", "상세주소", "Alias");

        when(addressService.createAddress(anyLong(), any(AddressRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/members/addresses")
                        .header("X-USER", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "postcode": "12345",
                                    "roadAddress": "서울특별시 강남구 삼성동",
                                    "detailAddress": "상세주소",
                                    "alias": "Alias",
                                    "isDefault": true
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.roadAddress").value("서울특별시 강남구 삼성동"));
    }


    @Test
    @DisplayName("주소 삭제 - 성공")
    void deleteAddress_Success() throws Exception {
        doNothing().when(addressService).deleteAddress(1L);

        mockMvc.perform(delete("/api/members/addresses/1"))
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("주소 검색 및 저장 - 성공")
    void searchAndSaveAddress_Success() throws Exception {
        doNothing().when(addressService).saveAddressFromKakao("1", "서울특별시 강남구 삼성동", "Alias", "Detail Address");

        mockMvc.perform(get("/api/members/addresses/search/save")
                        .header("X-USER", "1")
                        .param("keyword", "서울특별시 강남구 삼성동"))
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("회원 주소 조회 - 성공")
    void getMemberAddresses_Success() throws Exception {
        List<MemberAddressResponseDto> addresses = Collections.singletonList(
                new MemberAddressResponseDto(1L, "12345", "서울특별시 강남구 삼성동", "상세주소", "Alias", true)
        );

        when(memberAddressService.getAddressesByMemberId(1L)).thenReturn(addresses);

        mockMvc.perform(get("/members/1/addresses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @DisplayName("회원 주소 추가 - 성공")
    void addAddressToMember_Success() throws Exception {
        MemberAddressRequestDto requestDto = new MemberAddressRequestDto(
                "Alias", "서울특별시 강남구 삼성동", "12345", "상세주소", true
        );

        MemberAddressResponseDto responseDto = new MemberAddressResponseDto(
                1L, "Alias", "상세주소", "서울특별시 강남구 삼성동", "12345", true
        );

        when(memberAddressService.addAddressToMember(anyLong(), any(MemberAddressRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/members/1/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "alias": "Alias",
                        "roadAddress": "서울특별시 강남구 삼성동",
                        "postcode": "12345",
                        "detailAddress": "상세주소",
                        "isDefault": true
                    }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.roadAddress").value("서울특별시 강남구 삼성동"))
                .andExpect(jsonPath("$.detailAddress").value("상세주소"))
                .andExpect(jsonPath("$.alias").value("Alias"))
                .andExpect(jsonPath("$.postcode").value("12345"))
                .andExpect(jsonPath("$.isDefault").value(true));
    }



    @Test
    @DisplayName("회원 주소 삭제 - 성공")
    void removeAddressFromMember_Success() throws Exception {
        doNothing().when(memberAddressService).removeAddressFromMember(1L, 1L);

        mockMvc.perform(delete("/members/1/addresses/1"))
                .andExpect(status().isNoContent());
    }


    @Test
    @DisplayName("회원 주소 수정 - 성공")
    void updateAliasAndDefault_Success() throws Exception {
        doNothing().when(memberAddressService).updateAlias(1L, "New Alias", true);

        mockMvc.perform(patch("/members/1/addresses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "alias": "New Alias",
                                    "isDefault": true
                                }
                                """))
                .andExpect(status().isOk());
    }
}