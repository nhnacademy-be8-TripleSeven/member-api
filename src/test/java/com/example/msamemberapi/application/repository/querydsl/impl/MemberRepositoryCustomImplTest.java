//package com.example.msamemberapi.application.repository.querydsl.impl;
//
//import com.example.msamemberapi.application.entity.Member;
//import com.example.msamemberapi.application.entity.User;
//import com.example.msamemberapi.application.enums.Gender;
//import com.example.msamemberapi.application.enums.MemberGrade;
//import com.example.msamemberapi.application.repository.MemberRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Sort;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Date;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@Transactional
//@SpringBootTest
//public class MemberRepositoryCustomImplTest {
//
//    @Autowired
//    private MemberRepositoryCustomImpl memberRepositoryCustom;
//
//    @Autowired
//    private MemberRepository memberRepository;
//
//    private Member member1;
//    private Member member2;
//
//    @BeforeEach
//    void setUp() {
//        // Sample Members with different grades
//        User user1 = User.builder().name("John").phoneNumber("01012341234").build();
//        User user2 = User.builder().name("Alice").phoneNumber("01012341231").build();
//
//        member1 = Member.builder().user(user1).email("test1@example.com").memberGrade(MemberGrade.REGULAR).gender(Gender.MALE).birth(new Date()).build();
//        member2 = Member.builder().user(user2).email("test2@example.com").memberGrade(MemberGrade.PLATINUM).gender(Gender.MALE).birth(new Date()).build();
//
//        memberRepository.save(member1);
//        memberRepository.save(member2);
//    }
//
//    @Test
//    @DisplayName("이름을 기준으로 오름차순 정렬 테스트")
//    void testOrderByUserNameAscending() {
//        // Arrange
//        String name = null;
//        MemberGrade memberGrade = null;
//        PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("user.name")));
//
//        // Act
//        var result = memberRepositoryCustom.findMembers(name, memberGrade, pageable);
//
//        // Assert
//        assertNotNull(result);
//        assertTrue(result.getContent().get(0).getName().compareToIgnoreCase(result.getContent().get(1).getName()) <= 0);
//    }
//
//    @Test
//    @DisplayName("이름을 기준으로 내림차순 정렬 테스트")
//    void testOrderByUserNameDescending() {
//        // Arrange
//        String name = null;
//        MemberGrade memberGrade = null;
//        PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("user.name")));
//
//        // Act
//        var result = memberRepositoryCustom.findMembers(name, memberGrade, pageable);
//
//        // Assert
//        assertNotNull(result);
//        assertTrue(result.getContent().get(0).getName().compareTo(result.getContent().get(1).getName()) > 0);
//    }
//
//    @Test
//    @DisplayName("생일을 기준으로 오름차순 정렬 테스트")
//    void testOrderByBirthAscending() {
//        // Arrange
//        String name = null;
//        MemberGrade memberGrade = null;
//        PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("birth")));
//
//        // Act
//        var result = memberRepositoryCustom.findMembers(name, memberGrade, pageable);
//
//        // Assert
//        assertNotNull(result);
//        assertTrue(result.getContent().get(0).getBirth().before(result.getContent().get(1).getBirth()));
//    }
//
//    @Test
//    @DisplayName("생일을 기준으로 내림차순 정렬 테스트")
//    void testOrderByBirthDescending() {
//        // Arrange
//        String name = null;
//        MemberGrade memberGrade = null;
//        PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("birth")));
//
//        // Act
//        var result = memberRepositoryCustom.findMembers(name, memberGrade, pageable);
//
//        // Assert
//        assertNotNull(result);
//        assertTrue(result.getContent().get(0).getBirth().equals(result.getContent().get(1).getBirth()) || result.getContent().get(0).getBirth().after(result.getContent().get(1).getBirth()));
//    }
//
//    @Test
//    @DisplayName("ID를 기준으로 오름차순 정렬 테스트")
//    void testOrderByIdAscending() {
//        // Arrange
//        String name = null;
//        MemberGrade memberGrade = null;
//        PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("id")));
//
//        // Act
//        var result = memberRepositoryCustom.findMembers(name, memberGrade, pageable);
//
//        // Assert
//        assertNotNull(result);
//        assertTrue(result.getContent().get(0).getId() < result.getContent().get(1).getId());
//    }
//
//    @Test
//    @DisplayName("ID를 기준으로 내림차순 정렬 테스트")
//    void testOrderByIdDescending() {
//        // Arrange
//        String name = null;
//        MemberGrade memberGrade = null;
//        PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("id")));
//
//        // Act
//        var result = memberRepositoryCustom.findMembers(name, memberGrade, pageable);
//
//        // Assert
//        assertNotNull(result);
//        assertTrue(result.getContent().get(0).getId() > result.getContent().get(1).getId());
//    }
//}
