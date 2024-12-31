package com.example.msamemberapi.application.service.impl;

import com.example.msamemberapi.application.dto.request.AddressRequestDto;
import com.example.msamemberapi.application.dto.response.AddressResponseDto;
import com.example.msamemberapi.application.entity.Address;
import com.example.msamemberapi.application.repository.AddressRepository;
import com.example.msamemberapi.application.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponseDto> getAllAddresses() {
        return addressRepository.findAll().stream()
                .map(address -> new AddressResponseDto(
                        address.getId(),
                        address.getRoadAddress(),
                        address.getDetail(),
                        address.getAlias()
                ))
                .toList();
    }

    @Override
    @Transactional
    public AddressResponseDto createAddress(AddressRequestDto requestDto) {
        var savedAddress = addressRepository.save(Address.builder()
                .roadAddress(requestDto.getRoadAddress())
                .detail(requestDto.getDetailAddress())
                .alias(requestDto.getAlias())
                .build());
        return new AddressResponseDto(
                savedAddress.getId(),
                savedAddress.getRoadAddress(),
                savedAddress.getDetail(),
                savedAddress.getAlias()
        );
    }

    @Override
    @Transactional
    public AddressResponseDto updateAddress(Long id, AddressRequestDto requestDto) {
        var address = addressRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 주소를 찾을 수 없습니다."));

        address.updateDetails(
                requestDto.getRoadAddress(),
                requestDto.getDetailAddress(),
                requestDto.getAlias()
        );

        return new AddressResponseDto(
                address.getId(),
                address.getRoadAddress(),
                address.getDetail(),
                address.getAlias()
        );
    }

    @Override
    @Transactional
    public void deleteAddress(Long id) {
        if (!addressRepository.existsById(id)) {
            throw new IllegalArgumentException("해당 주소를 찾을 수 없습니다.");
        }
        addressRepository.deleteById(id);
    }
}