package com.example.msamemberapi.application.controller;

import com.example.msamemberapi.application.dto.response.CartDto;
import com.example.msamemberapi.application.dto.response.BookDetailResponseDto;
import com.example.msamemberapi.application.service.impl.GuestCartService;
import com.example.msamemberapi.application.service.impl.MemberCartService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CartControllerTest {

    private MockMvc mockMvc;

    private MemberCartService memberCartService;
    private GuestCartService guestCartService;
    private CartController cartController;

    @BeforeEach
    public void setup() {
        memberCartService = mock(MemberCartService.class);
        guestCartService = mock(GuestCartService.class);
        cartController = new CartController(memberCartService, guestCartService);
        mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();
    }

    @Test
    @DisplayName("장바구니 조회 - 회원")
    public void testGetCartForMember() throws Exception {
        BookDetailResponseDto bookDetailResponseDto = new BookDetailResponseDto(
                "Book Title",
                "This is a description",
                LocalDate.of(2020, 5, 20),
                100,
                80,
                "978-3-16-148410-0",
                50,
                300,
                "coverUrl",
                "Publisher"
        );

        CartDto.CartItem cartItem = new CartDto.CartItem(bookDetailResponseDto, 1L, 2);
        CartDto cartDto = new CartDto("userId", Collections.singletonList(cartItem));

        when(memberCartService.getCart("1")).thenReturn(cartDto);

        mockMvc.perform(get("/cart")
                        .header("X-USER", "1")
                        .cookie(new Cookie("GUEST-ID", "GUEST-ID"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("장바구니 조회 - 비회원")
    public void testGetCartForGuest() throws Exception {
        BookDetailResponseDto bookDetailResponseDto = new BookDetailResponseDto(
                "Book Title",
                "This is a description",
                LocalDate.of(2020, 5, 20),
                100,
                80,
                "978-3-16-148410-0",
                50,
                300,
                "coverUrl",
                "Publisher"
        );

        CartDto.CartItem cartItem = new CartDto.CartItem(bookDetailResponseDto, 1L, 2);
        CartDto cartDto = new CartDto("guestId", Collections.singletonList(cartItem));

        when(guestCartService.getCart("GUEST-ID")).thenReturn(cartDto);

        mockMvc.perform(get("/cart")
                        .cookie(new Cookie("GUEST-ID", "GUEST-ID"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("장바구니에 아이템 추가 - 회원")
    public void testAddCartForMember() throws Exception {
        CartDto.CartItem cartItem = new CartDto.CartItem(new BookDetailResponseDto(
                "Book Title",
                "Description",
                LocalDate.of(2020, 5, 20),
                100,
                80,
                "978-3-16-148410-0",
                50,
                300,
                "coverUrl",
                "Publisher"
        ), 1L, 2);

        CartDto cartDto = new CartDto("userId", Collections.singletonList(cartItem));


        when(memberCartService.addCart(anyString(), anyLong(), anyInt())).thenReturn(cartDto);

        mockMvc.perform(post("/cart")
                        .header("X-USER", "1")
                        .param("bookId", "1")
                        .cookie(new Cookie("GUEST-ID", "GUEST-ID"))
                        .param("quantity", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("장바구니 수량 변경 - 회원")
    public void testUpdateCartItemQuantityForMember() throws Exception {
        CartDto.CartItem cartItem = new CartDto.CartItem(new BookDetailResponseDto(
                "Book Title",
                "Description",
                LocalDate.of(2020, 5, 20),
                100,
                80,
                "978-3-16-148410-0",
                50,
                300,
                "coverUrl",
                "Publisher"
        ), 1L, 3);

        CartDto cartDto = new CartDto("userId", Collections.singletonList(cartItem));

        when(memberCartService.updateCartItemQuantity("1", 1L, 3)).thenReturn(cartDto);

        mockMvc.perform(put("/cart/book/quantity")
                        .header("X-USER", "1")
                        .cookie(new Cookie("GUEST-ID", "GUEST-ID"))
                        .param("bookId", "1")
                        .param("quantity", "3")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("장바구니 아이템 삭제 - 회원")
    public void testDeleteCartItemForMember() throws Exception {
        CartDto.CartItem cartItem = new CartDto.CartItem(new BookDetailResponseDto(
                "Book Title",
                "Description",
                LocalDate.of(2020, 5, 20),
                100,
                80,
                "978-3-16-148410-0",
                50,
                300,
                "coverUrl",
                "Publisher"
        ), 1L, 2);

        CartDto cartDto = new CartDto("userId", Collections.singletonList(cartItem));

        when(memberCartService.deleteCartItem("1", 1L)).thenReturn(cartDto);

        mockMvc.perform(delete("/cart/book")
                        .header("X-USER", "1")
                        .cookie(new Cookie("GUEST-ID", "GUEST-ID"))
                        .param("bookId", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("장바구니 초기화 - 비회원")
    public void testClearCartForGuest() throws Exception {
        CartDto cartDto = new CartDto("guestId", Collections.emptyList());

        when(guestCartService.clearCart("GUEST-ID")).thenReturn(cartDto);

        mockMvc.perform(delete("/cart")
                        .cookie(new Cookie("GUEST-ID", "GUEST-ID"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
