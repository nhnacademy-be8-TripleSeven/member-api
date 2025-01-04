package com.example.msamemberapi.application.service.impl;

import com.example.msamemberapi.application.dto.response.CartDto;
import com.example.msamemberapi.application.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GuestCartService implements CartService {

    @Override
    public CartDto getCart(String userId) {
        return null;
    }

    @Override
    public CartDto addCart(String userId, long bookId, int quantity) {
        return null;
    }

    @Override
    public CartDto updateCartItemQuantity(String userId, long bookId, int quantity) {
        return null;
    }

    @Override
    public CartDto deleteCartItem(String userId, long bookId) {
        return null;
    }

    @Override
    public CartDto clearCart(String userId) {
        return null;
    }
}
