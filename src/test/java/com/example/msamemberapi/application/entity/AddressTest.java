package com.example.msamemberapi.application.entity;

import com.example.msamemberapi.application.enums.MemberGrade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class EntityTest {

        @Test
        @DisplayName("Address 엔티티 생성 및 값 확인")
        void testAddressEntityCreation() {
            Address address = Address.builder()
                    .id(1L)
                    .member(null)
                    .postcode("12345")
                    .roadAddress("123 Road")
                    .detailAddress("Apt 456")
                    .alias("Home")
                    .isDefault(true)
                    .build();

            assertThat(address.getId()).isEqualTo(1L);
            assertThat(address.getPostcode()).isEqualTo("12345");
            assertThat(address.getRoadAddress()).isEqualTo("123 Road");
            assertThat(address.getDetailAddress()).isEqualTo("Apt 456");
            assertThat(address.getAlias()).isEqualTo("Home");
            assertThat(address.getIsDefault()).isTrue();
        }

        @Test
        @DisplayName("Address 엔티티 업데이트")
        void testAddressEntityUpdate() {
            Address address = new Address(null, "12345", "123 Road", "Apt 456", "Home", true);

            address.updateDetails("456 New Road", "Apt 789", "Office", "67890", false);

            assertThat(address.getRoadAddress()).isEqualTo("456 New Road");
            assertThat(address.getDetailAddress()).isEqualTo("Apt 789");
            assertThat(address.getAlias()).isEqualTo("Office");
            assertThat(address.getPostcode()).isEqualTo("67890");
            assertThat(address.getIsDefault()).isFalse();
        }

        @Test
        @DisplayName("GradePolicy 엔티티 생성")
        void testGradePolicyCreation() {
            GradePolicy gradePolicy = GradePolicy.addGradePolicy(
                    "Gold Member",
                    MemberGrade.GOLD,
                    "Gold tier benefits",
                    BigDecimal.valueOf(0.15),
                    1000,
                    5000
            );

            assertThat(gradePolicy.getName()).isEqualTo("Gold Member");
            assertThat(gradePolicy.getGrade()).isEqualTo(MemberGrade.GOLD);
            assertThat(gradePolicy.getDescription()).isEqualTo("Gold tier benefits");
            assertThat(gradePolicy.getRate()).isEqualTo(BigDecimal.valueOf(0.15));
            assertThat(gradePolicy.getMin()).isEqualTo(1000);
            assertThat(gradePolicy.getMax()).isEqualTo(5000);
        }

        @Test
        @DisplayName("MemberAddress 엔티티 생성 및 업데이트 복사본 생성")
        void testMemberAddressEntity() {
            Address address = Address.builder()
                    .id(1L)
                    .roadAddress("123 Road")
                    .detailAddress("Apt 456")
                    .alias("Home")
                    .isDefault(true)
                    .build();

            MemberAddress memberAddress = MemberAddress.builder()
                    .id(1L)
                    .member(null) // Member는 null 처리
                    .address(address)
                    .alias("Home")
                    .isDefault(true)
                    .build();

            // 값 확인
            assertThat(memberAddress.getId()).isEqualTo(1L);
            assertThat(memberAddress.getAlias()).isEqualTo("Home");
            assertThat(memberAddress.getIsDefault()).isTrue();

            // 복사본 생성 후 값 확인
            MemberAddress updatedAddress = memberAddress.createUpdatedCopy("Office", false);
            assertThat(updatedAddress.getAlias()).isEqualTo("Office");
            assertThat(updatedAddress.getIsDefault()).isFalse();
            assertThat(updatedAddress.getMember()).isEqualTo(memberAddress.getMember());
        }
    }