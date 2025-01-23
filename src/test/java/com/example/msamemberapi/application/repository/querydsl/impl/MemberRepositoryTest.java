//package com.example.msamemberapi.application.repository.querydsl.impl;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
//
//import com.example.msamemberapi.application.dto.request.MemberUpdateRequestDto;
//import com.example.msamemberapi.application.entity.Member;
//import com.example.msamemberapi.application.repository.MemberRepository;
//import java.util.Optional;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//class MemberRepositoryTest {
//
//    @Autowired
//    private MemberRepository memberRepository;
//
//    @Test
//    @DisplayName("이메일로 회원 조회 성공")
//    void findByEmail_success() {
//        // Arrange
//        Member member = Member.builder()
//                .email("test@example.com")
//                .build();
//        memberRepository.save(member);
//
//        // Act
//        Optional<Member> result = memberRepository.findByEmail("test@example.com");
//
//        // Assert
//        assertTrue(result.isPresent());
//        assertEquals("test@example.com", result.get().getEmail());
//    }
//
//
//    @Test
//    @DisplayName("존재하지 않는 이메일로 조회 실패")
//    void findByEmail_notFound() {
//        // Act
//        Optional<Member> result = memberRepository.findByEmail("nonexistent@example.com");
//
//        // Assert
//        assertFalse(result.isPresent());
//    }
//
//
//}