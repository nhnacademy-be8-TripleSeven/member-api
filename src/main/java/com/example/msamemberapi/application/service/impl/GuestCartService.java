package com.example.msamemberapi.application.service.impl;

import com.example.msamemberapi.application.dto.response.BookDetailResponseDto;
import com.example.msamemberapi.application.dto.response.CartDto;
import com.example.msamemberapi.application.error.CustomException;
import com.example.msamemberapi.application.error.ErrorCode;
import com.example.msamemberapi.application.feign.BookFeignClient;
import com.example.msamemberapi.application.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class GuestCartService implements CartService {

    private static final String GUEST_CART_KEY_PREFIX = "cart:guest:";
    private static final long CART_EXPIRATION_TIME_IN_SECONDS = 60 * 60 * 24 * 3; // 3 days

    private final RedisTemplate<String, CartDto> redisTemplate;
    private final BookFeignClient bookFeignClient;

    @Override
    public CartDto getCart(String userId) {
        List<CartDto.CartItem> items = getGuestCartItemsById(userId);
        return new CartDto(userId, items);
    }

    @Override
    public CartDto addCart(String userId, long bookId, int quantity) {
        return addItemToCart(userId, bookId, quantity);
    }

    @Override
    public CartDto updateCartItemQuantity(String userId, long bookId, int quantity) {
        return updateItemQuantity(userId, bookId, quantity);
    }

    @Override
    public CartDto deleteCartItem(String userId, long bookId) {
        return deleteItemFromCart(userId, bookId);
    }

    @Override
    public CartDto clearCart(String userId) {
        return clearCartById(userId);
    }

    private CartDto addItemToCart(String userId, long bookId, int quantity) {
        HashOperations<String, String, CartDto.CartItem> hashOps = redisTemplate.opsForHash();
        String cartKey = GUEST_CART_KEY_PREFIX.concat(userId);
        String bookKey = String.valueOf(bookId);

        CartDto.CartItem existingItem = hashOps.get(cartKey, bookKey);
        if (existingItem != null) {
            quantity += existingItem.getQuantity();
        }

        BookDetailResponseDto bookDetails = bookFeignClient.getBookDetails(bookId).getBody();
        validBookQuantity(bookDetails, quantity);

        CartDto.CartItem newItem = new CartDto.CartItem(bookDetails, bookId, quantity);
        hashOps.put(cartKey, bookKey, newItem);

        redisTemplate.expire(cartKey, CART_EXPIRATION_TIME_IN_SECONDS, TimeUnit.SECONDS);

        return getCart(userId);
    }

    private CartDto updateItemQuantity(String userId, long bookId, int quantity) {
        BookDetailResponseDto bookDetails = bookFeignClient.getBookDetails(bookId).getBody();
        validBookQuantity(bookDetails, quantity);

        HashOperations<String, String, CartDto.CartItem> hashOps = redisTemplate.opsForHash();
        String cartKey = GUEST_CART_KEY_PREFIX.concat(userId);
        String bookKey = String.valueOf(bookId);

        if (quantity <= 0) {
            hashOps.delete(cartKey, bookKey);
        } else {
            CartDto.CartItem updatedItem = new CartDto.CartItem(bookDetails, bookId, quantity);
            hashOps.put(cartKey, bookKey, updatedItem);
        }

        redisTemplate.expire(cartKey, CART_EXPIRATION_TIME_IN_SECONDS, TimeUnit.SECONDS);

        return getCart(userId);
    }

    private CartDto deleteItemFromCart(String userId, long bookId) {
        HashOperations<String, String, CartDto.CartItem> hashOps = redisTemplate.opsForHash();
        String cartKey = GUEST_CART_KEY_PREFIX.concat(userId);
        String bookKey = String.valueOf(bookId);

        hashOps.delete(cartKey, bookKey);

        redisTemplate.expire(cartKey, CART_EXPIRATION_TIME_IN_SECONDS, TimeUnit.SECONDS);

        return getCart(userId);
    }

    private CartDto clearCartById(String userId) {
        String cartKey = GUEST_CART_KEY_PREFIX.concat(userId);
        CartDto emptyCart = new CartDto(userId, new ArrayList<>());

        redisTemplate.delete(cartKey);

        return emptyCart;
    }

    private List<CartDto.CartItem> getGuestCartItemsById(String userId) {
        HashOperations<String, String, CartDto.CartItem> hashOps = redisTemplate.opsForHash();
        String cartKey = GUEST_CART_KEY_PREFIX.concat(userId);

        return new ArrayList<>(hashOps.values(cartKey));
    }

    private void validBookQuantity(BookDetailResponseDto bookDetails, int quantity) {
        if (quantity < 0 || bookDetails == null) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        if (bookDetails.getStock() < quantity) {
            throw new CustomException(ErrorCode.INSUFFICIENT_BOOK_QUANTITY);
        }
    }
}
