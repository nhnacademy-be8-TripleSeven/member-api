package com.example.msamemberapi.application.service;

import com.example.msamemberapi.application.dto.response.CartDto;

public interface CartService {

    CartDto getCart(String userId);
    CartDto addCart(String userId, long bookId, int quantity);
    CartDto updateCartItemQuantity(String userId, long bookId, int quantity);
    CartDto deleteCartItem(String userId, long bookId);
    CartDto clearCart(String userId);
}
