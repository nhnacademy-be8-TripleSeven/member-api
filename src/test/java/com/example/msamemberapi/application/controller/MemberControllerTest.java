package com.example.msamemberapi.application.controller;

import com.example.msamemberapi.application.dto.request.GradeUpdateRequestDto;
import com.example.msamemberapi.application.dto.request.MemberUpdateRequestDto;
import com.example.msamemberapi.application.dto.response.MemberDto;
import com.example.msamemberapi.application.dto.response.MemberGradeDto;
import com.example.msamemberapi.application.entity.Address;
import com.example.msamemberapi.application.entity.Member;
import com.example.msamemberapi.application.enums.MemberGrade;
import com.example.msamemberapi.application.error.CustomException;
import com.example.msamemberapi.application.error.ErrorCode;
import com.example.msamemberapi.application.repository.AddressRepository;
import com.example.msamemberapi.application.repository.MemberRepository;
import com.example.msamemberapi.application.service.AddressService;
import com.example.msamemberapi.application.service.GradePolicyService;
import com.example.msamemberapi.application.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc

class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Mock
    private AddressService addressService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private AddressRepository addressRepository;

    @MockBean
    private GradePolicyService gradePolicyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("멤버 정보 조회 테스트")
    void getMemberInfoTest() throws Exception {
        // Arrange
        MemberDto mockMember = MemberDto.builder()
                .id(1L)
                .email("test@example.com")
                .phoneNumber("010-1234-5678")
                .name("John Doe")
                .memberGrade(MemberGrade.REGULAR.name())
                .build();

        when(memberService.getMemberInfo(1L)).thenReturn(mockMember);

        // Act & Assert
        mockMvc.perform(get("/api/members/info")
                        .header("X-USER", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.memberGrade").value("REGULAR"));

        verify(memberService, times(1)).getMemberInfo(1L);
    }

    @Test
    @DisplayName("멤버 수정 테스트")
    void updateMemberTest() throws Exception {
        // Arrange
        MemberUpdateRequestDto updateRequest = MemberUpdateRequestDto.builder()
                .email("newemail@example.com")
                .phone("010-9876-5432")
                .postcode("12345")
                .address("Seoul")
                .detailAddress("Gangnam-gu")
                .newPassword("newPassword123")
                .build();

        MemberDto updatedMember = MemberDto.builder()
                .id(1L)
                .email("newemail@example.com")
                .phoneNumber("010-9876-5432")
                .name("Updated Name")
                .memberGrade(MemberGrade.GOLD.name())
                .build();

        when(memberService.updateMember(eq(1L), any())).thenReturn(updatedMember);

        // Act & Assert
        mockMvc.perform(put("/api/members/1/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("newemail@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("010-9876-5432"))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.memberGrade").value("GOLD"));

        verify(memberService, times(1)).updateMember(eq(1L), any());
    }

    @Test
    @DisplayName("비밀번호 검증 테스트 - 성공")
    void checkPasswordSuccessTest() throws Exception {
        // Arrange
        Map<String, String> payload = new HashMap<>();
        payload.put("password", "validPassword");

        when(memberService.verifyPassword(1L, "validPassword")).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/members/1/verify-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(content().string("비밀번호 검증 성공"));

        verify(memberService, times(1)).verifyPassword(1L, "validPassword");
    }

    @Test
    @DisplayName("비밀번호 검증 테스트 - 실패")
    void checkPasswordFailureTest() throws Exception {
        // Arrange
        Map<String, String> payload = new HashMap<>();
        payload.put("password", "invalidPassword");

        when(memberService.verifyPassword(1L, "invalidPassword")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/api/members/1/verify-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("비밀번호가 틀렸습니다."));

        verify(memberService, times(1)).verifyPassword(1L, "invalidPassword");
    }

    @Test
    @DisplayName("주소 업데이트 성공")
    void updateAddress_success() {
        // Arrange
        Member member = Member.builder().id(1L).build();
        Address address = Address.builder()
                .id(1L)
                .roadAddress("Old Road")
                .detailAddress("Old Detail")
                .alias("Old Alias")
                .postcode("12345")
                .isDefault(true)
                .member(member)
                .build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));

        // Act
        address.updateDetails("New Road", "New Detail", "New Alias", "67890", false);

        // Assert
        assertEquals("New Road", address.getRoadAddress());
        assertEquals("New Detail", address.getDetailAddress());
        assertEquals("New Alias", address.getAlias());
        assertEquals("67890", address.getPostcode());
        assertFalse(address.getIsDefault());
    }

    @Test
    @DisplayName("주소 업데이트 실패 - 존재하지 않는 주소")
    void updateAddress_notFound() {
        // Arrange
        when(addressRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            Address address =
                    addressRepository.findById(999L).orElseThrow(() -> new RuntimeException("Address not found"));
            address.updateDetails("New Road", "New Detail", "New Alias", "67890", false);
        });

        assertEquals("Address not found", exception.getMessage());
    }

}