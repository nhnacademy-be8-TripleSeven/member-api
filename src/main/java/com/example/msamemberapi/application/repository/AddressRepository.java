package com.example.msamemberapi.application.repository;

import com.example.msamemberapi.application.entity.Address;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByMemberId(Long userId);
    boolean existsByIdAndMemberId(Long addressId, Long memberId);
}