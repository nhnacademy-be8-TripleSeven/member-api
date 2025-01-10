package com.example.msamemberapi.application.service.impl;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.msamemberapi.application.dto.response.BookDetailResponseDto;
import com.example.msamemberapi.application.dto.response.CartDto;
import com.example.msamemberapi.application.error.CustomException;
import com.example.msamemberapi.application.feign.BookFeignClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public class MemberCartServiceTest {

    @Mock
    private RedisTemplate<String, CartDto> redisTemplate;

    @Mock
    private BookFeignClient bookFeignClient;

    @Mock
    private HashOperations<String, String, CartDto.CartItem> hashOps;

    @InjectMocks
    private MemberCartService memberCartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        doReturn(hashOps).when(redisTemplate).opsForHash();
    }

    @Test
    @DisplayName("장바구니 조회 성공 테스트")
    void getCart_success() {
        // Arrange
        String userId = "user123";
        CartDto.CartItem cartItem = new CartDto.CartItem(mock(BookDetailResponseDto.class), 1, 2);
        List<CartDto.CartItem> cartItems = new ArrayList<>();
        cartItems.add(cartItem);
        when(hashOps.values("cart:member:" + userId)).thenReturn(cartItems);

        // Act
        CartDto cart = memberCartService.getCart(userId);

        // Assert
        assertNotNull(cart);
        assertEquals(1, cart.getCartItems().size());
    }

    @Test
    @DisplayName("기존 아이템 장바구니에 추가 성공 테스트")
    void addCart_success_existingItem() {
        // Arrange
        String userId = "user123";
        long bookId = 1L;
        int quantity = 2;
        CartDto.CartItem existingItem = new CartDto.CartItem(mock(BookDetailResponseDto.class), bookId, 1);
        CartDto.CartItem newItem = new CartDto.CartItem(mock(BookDetailResponseDto.class), bookId, 3);
        when(hashOps.get("cart:member:" + userId, String.valueOf(bookId))).thenReturn(existingItem);
        BookDetailResponseDto bookDetails = mock(BookDetailResponseDto.class);
        when(bookFeignClient.getBookDetails(bookId)).thenReturn(ResponseEntity.ofNullable(bookDetails));
        when(bookDetails.getStock()).thenReturn(10);

        // Act
        memberCartService.addCart(userId, bookId, quantity);

        // Assert
        verify(hashOps).put("cart:member:" + userId, String.valueOf(bookId), newItem);
    }

    @Test
    @DisplayName("새로운 아이템 장바구니에 추가 성공 테스트")
    void addCart_success_newItem() {
        // Arrange
        String userId = "user123";
        long bookId = 1L;
        int quantity = 2;
        BookDetailResponseDto bookDetails = mock(BookDetailResponseDto.class);
        when(bookFeignClient.getBookDetails(bookId)).thenReturn(ResponseEntity.ofNullable(bookDetails));
        when(bookDetails.getStock()).thenReturn(10);

        CartDto.CartItem newItem = new CartDto.CartItem(bookDetails, bookId, quantity);

        // Act
        CartDto resultCart = memberCartService.addCart(userId, bookId, quantity);

        // Assert
        verify(hashOps).put("cart:member:" + userId, String.valueOf(bookId), newItem);
        assertNotNull(resultCart);
    }

    @Test
    @DisplayName("장바구니 아이템 수량 업데이트 성공 테스트")
    void updateCartItemQuantity_success() {
        // Arrange
        String userId = "user123";
        long bookId = 1L;
        int quantity = 5;
        BookDetailResponseDto bookDetails = mock(BookDetailResponseDto.class);
        when(bookFeignClient.getBookDetails(bookId)).thenReturn(ResponseEntity.ofNullable(bookDetails));
        when(bookDetails.getStock()).thenReturn(10);

        CartDto.CartItem updatedItem = new CartDto.CartItem(bookDetails, bookId, quantity);

        // Act
        CartDto resultCart = memberCartService.updateCartItemQuantity(userId, bookId, quantity);

        // Assert
        verify(hashOps).put("cart:member:" + userId, String.valueOf(bookId), updatedItem);
        assertNotNull(resultCart);
    }

    @Test
    @DisplayName("장바구니 아이템 삭제 성공 테스트")
    void deleteCartItem_success() {
        // Arrange
        String userId = "user123";
        long bookId = 1L;

        // Act
        CartDto resultCart = memberCartService.deleteCartItem(userId, bookId);

        // Assert
        verify(hashOps).delete("cart:member:" + userId, String.valueOf(bookId));
        assertNotNull(resultCart);
    }

    @Test
    @DisplayName("장바구니 비우기 성공 테스트")
    void clearCart_success() {
        // Arrange
        String userId = "user123";

        // Act
        CartDto resultCart = memberCartService.clearCart(userId);

        // Assert
        verify(redisTemplate).delete("cart:member:" + userId);
        assertNotNull(resultCart);
        assertTrue(resultCart.getCartItems().isEmpty());
    }

    @Test
    @DisplayName("잘못된 수량에 대한 예외 처리 테스트")
    void validBookQuantity_invalidQuantity_throwsException() {
        // Arrange
        BookDetailResponseDto bookDetails = mock(BookDetailResponseDto.class);
        when(bookDetails.getStock()).thenReturn(10);

        // Act & Assert
        assertThrows(CustomException.class, () -> memberCartService.validBookQuantity(bookDetails, -1));
        assertThrows(CustomException.class, () -> memberCartService.validBookQuantity(null, 1));
    }

    @Test
    @DisplayName("재고 부족에 대한 예외 처리 테스트")
    void validBookQuantity_insufficientStock_throwsException() {
        // Arrange
        BookDetailResponseDto bookDetails = mock(BookDetailResponseDto.class);
        when(bookDetails.getStock()).thenReturn(5);

        // Act & Assert
        assertThrows(CustomException.class, () -> memberCartService.validBookQuantity(bookDetails, 10));
    }
}
