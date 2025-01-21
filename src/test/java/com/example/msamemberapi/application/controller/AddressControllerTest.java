package com.example.msamemberapi.application.controller;

import com.example.msamemberapi.application.dto.request.AddressRequestDto;
import com.example.msamemberapi.application.dto.response.AddressResponseDto;
import com.example.msamemberapi.application.service.AddressService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AddressController.class)
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AddressService addressService;

    @Autowired
    private ObjectMapper objectMapper;

    private AddressResponseDto addressResponseDto;

    @BeforeEach
    void setUp() {
        addressResponseDto = AddressResponseDto.builder()
                .id(1L)
                .alias("Home")
                .roadAddress("123 Main St")
                .postcode("12345")
                .detailAddress("Apt 101")
                .isDefault(true)
                .build();
    }

    @Test
    @DisplayName("주소 목록 조회 - 성공")
    void testGetAllAddresses() throws Exception {
        when(addressService.getAllAddressesById(1L)).thenReturn(List.of(addressResponseDto));

        mockMvc.perform(get("/api/members/{memberId}/addresses/manage", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(addressResponseDto.getId()))
                .andExpect(jsonPath("$[0].alias").value(addressResponseDto.getAlias()));

        verify(addressService, times(1)).getAllAddressesById(1L);
    }

    @Test
    @DisplayName("주소 수정 - 성공")
    void testUpdateAddress() throws Exception {
        AddressRequestDto requestDto = AddressRequestDto.builder()
                .alias("Office")
                .roadAddress("456 Office Blvd")
                .detailAddress("Suite 200")
                .postcode("54321")
                .isDefault(false)
                .build();

        when(addressService.updateAddress(eq(1L), any(AddressRequestDto.class)))
                .thenReturn(addressResponseDto);

        mockMvc.perform(put("/api/members/{memberId}/addresses", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(addressResponseDto.getId()))
                .andExpect(jsonPath("$.alias").value(addressResponseDto.getAlias()));

        verify(addressService, times(1)).updateAddress(eq(1L), any(AddressRequestDto.class));
    }

    @Test
    @DisplayName("주소 삭제 - 성공")
    void testDeleteAddress() throws Exception {
        doNothing().when(addressService).deleteAddress(1L);

        mockMvc.perform(delete("/api/members/{memberId}/addresses/manage/{addressId}", 1L, 1L))
                .andExpect(status().isOk());

        verify(addressService, times(1)).deleteAddress(1L);
    }

    @Test
    @DisplayName("주소 검색 및 저장 - 성공")
    void testSearchAndSaveAddress() throws Exception {
        Map<String, String> payload = Map.of(
                "keyword", "Office Address",
                "alias", "Office",
                "detailAddress", "Suite 200"
        );

        doNothing().when(addressService).saveAddressFromKakao(eq(1L), anyString(), anyString(), anyString());

        mockMvc.perform(post("/api/members/{memberId}/addresses/search/save", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated());

        verify(addressService, times(1)).saveAddressFromKakao(
                eq(1L),
                eq("Office Address"),
                eq("Office"),
                eq("Suite 200")
        );
    }
}