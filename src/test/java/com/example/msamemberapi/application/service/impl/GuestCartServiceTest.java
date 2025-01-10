package com.example.msamemberapi.application.service.impl;

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

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

class GuestCartServiceTest {

    @Mock
    private RedisTemplate<String, CartDto> redisTemplate;

    @Mock
    private BookFeignClient bookFeignClient;

    @Mock
    private HashOperations<String, String, CartDto.CartItem> hashOps;

    @InjectMocks
    private GuestCartService guestCartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        doReturn(hashOps).when(redisTemplate).opsForHash();
    }

    @Test
    @DisplayName("게스트 사용자의 장바구니를 성공적으로 조회한다")
    void getCart_success() {
        // Arrange
        String userId = "guest123";

        // BookDetailResponseDto 객체를 생성하고 값을 설정
        BookDetailResponseDto bookDetail = new BookDetailResponseDto(
                "Example Book Title",        // title
                "A great description.",      // description
                LocalDate.of(2021, 5, 10),   // publishedDate
                25000,                       // regularPrice
                20000,                       // salePrice
                "1234567890123",             // isbn13
                10,                          // stock
                350,                         // page
                "http://example.com/cover",  // coverUrl
                "Publisher Name"             // publisher
        );

        CartDto.CartItem cartItem = new CartDto.CartItem(bookDetail, 1L, 2);
        List<CartDto.CartItem> cartItems = new ArrayList<>();
        cartItems.add(cartItem);

        when(hashOps.values("cart:guest:" + userId)).thenReturn(cartItems);

        // Act
        CartDto result = guestCartService.getCart(userId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getCartItems().size());
    }

    @Test
    @DisplayName("기존 아이템을 장바구니에 성공적으로 추가한다")
    void addCart_success_existingItem() {
        // Arrange
        String userId = "guest123";
        long bookId = 1L;
        int quantity = 3;

        // BookDetailResponseDto 객체를 생성하고 값을 설정
        BookDetailResponseDto bookDetail = new BookDetailResponseDto(
                "Existing Book Title",       // title
                "Description of the book",   // description
                LocalDate.of(2020, 1, 1),    // publishedDate
                20000,                       // regularPrice
                15000,                       // salePrice
                "9876543210987",             // isbn13
                20,                          // stock
                300,                         // page
                "http://example.com/cover2", // coverUrl
                "Another Publisher"          // publisher
        );

        CartDto.CartItem existingItem = new CartDto.CartItem(bookDetail, bookId, 2);
        CartDto.CartItem updatedItem = new CartDto.CartItem(bookDetail, bookId, 5);
        when(hashOps.get("cart:guest:" + userId, String.valueOf(bookId))).thenReturn(existingItem);
        when(bookFeignClient.getBookDetails(bookId)).thenReturn(ResponseEntity.ok(bookDetail));

        // Act
        CartDto result = guestCartService.addCart(userId, bookId, quantity);

        // Assert
        verify(hashOps).put("cart:guest:" + userId, String.valueOf(bookId), updatedItem);
        assertNotNull(result);
    }

    @Test
    @DisplayName("새로운 아이템을 장바구니에 성공적으로 추가한다")
    void addCart_success_newItem() {
        // Arrange
        String userId = "guest123";
        long bookId = 1L;
        int quantity = 2;

        // BookDetailResponseDto 객체를 생성하고 값을 설정
        BookDetailResponseDto bookDetails = new BookDetailResponseDto(
                "New Book Title",            // title
                "A brief description.",      // description
                LocalDate.of(2021, 6, 15),   // publishedDate
                22000,                       // regularPrice
                18000,                       // salePrice
                "1112223334445",             // isbn13
                15,                          // stock
                400,                         // page
                "http://example.com/newcover", // coverUrl
                "New Publisher"              // publisher
        );

        when(bookFeignClient.getBookDetails(bookId)).thenReturn(ResponseEntity.ok(bookDetails));

        CartDto.CartItem newItem = new CartDto.CartItem(bookDetails, bookId, quantity);

        // Act
        CartDto result = guestCartService.addCart(userId, bookId, quantity);

        // Assert
        verify(hashOps).put("cart:guest:" + userId, String.valueOf(bookId), newItem);
        assertNotNull(result);
    }

    @Test
    @DisplayName("장바구니 아이템의 수량을 성공적으로 업데이트한다")
    void updateCartItemQuantity_success() {
        // Arrange
        String userId = "guest123";
        long bookId = 1L;
        int quantity = 5;

        // BookDetailResponseDto 객체를 생성하고 값을 설정
        BookDetailResponseDto bookDetails = new BookDetailResponseDto(
                "Updated Book Title",       // title
                "Updated description",      // description
                LocalDate.of(2022, 3, 20),  // publishedDate
                27000,                      // regularPrice
                22000,                      // salePrice
                "6543210987654",            // isbn13
                12,                         // stock
                450,                        // page
                "http://example.com/updatedcover", // coverUrl
                "Updated Publisher"         // publisher
        );

        when(bookFeignClient.getBookDetails(anyLong())).thenReturn(ResponseEntity.ofNullable(bookDetails));

        CartDto.CartItem updatedItem = new CartDto.CartItem(bookDetails, bookId, quantity);

        // Act
        CartDto result = guestCartService.updateCartItemQuantity(userId, bookId, quantity);

        // Assert
        verify(hashOps).put("cart:guest:" + userId, String.valueOf(bookId), updatedItem);
        assertNotNull(result);
    }

    @Test
    @DisplayName("장바구니에서 아이템을 성공적으로 삭제한다")
    void deleteCartItem_success() {
        // Arrange
        String userId = "guest123";
        long bookId = 1L;

        // Act
        CartDto result = guestCartService.deleteCartItem(userId, bookId);

        // Assert
        verify(hashOps).delete("cart:guest:" + userId, String.valueOf(bookId));
        assertNotNull(result);
    }

    @Test
    @DisplayName("장바구니를 성공적으로 비운다")
    void clearCart_success() {
        // Arrange
        String userId = "guest123";

        // Act
        CartDto result = guestCartService.clearCart(userId);

        // Assert
        verify(redisTemplate).delete("cart:guest:" + userId);
        assertNotNull(result);
        assertTrue(result.getCartItems().isEmpty());
    }

    @Test
    @DisplayName("잘못된 수량이 제공될 경우 예외를 발생시킨다")
    void validBookQuantity_invalidQuantity_throwsException() {
        // Arrange
        BookDetailResponseDto bookDetails = mock(BookDetailResponseDto.class);
        when(bookDetails.getStock()).thenReturn(10);

        // Act & Assert
        assertThrows(CustomException.class, () -> guestCartService.validBookQuantity(bookDetails, -1));
        assertThrows(CustomException.class, () -> guestCartService.validBookQuantity(null, 1));
    }

    @Test
    @DisplayName("요청된 수량에 재고가 부족할 경우 예외를 발생시킨다")
    void validBookQuantity_insufficientStock_throwsException() {
        // Arrange
        BookDetailResponseDto bookDetails = mock(BookDetailResponseDto.class);
        when(bookDetails.getStock()).thenReturn(5);

        // Act & Assert
        assertThrows(CustomException.class, () -> guestCartService.validBookQuantity(bookDetails, 10));
    }
}
