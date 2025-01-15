package com.example.msamemberapi.application.repository;

import com.example.msamemberapi.application.entity.MemberAddress;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberAddressRepository extends JpaRepository<MemberAddress, Long> {
    List<MemberAddress> findByMemberId(Long memberId);
    Optional<MemberAddress> findByMemberIdAndAddressId(Long memberId, Long addressId);
}