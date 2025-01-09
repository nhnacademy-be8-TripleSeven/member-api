package com.example.msamemberapi.application.controller;

import com.example.msamemberapi.application.dto.response.CartDto;
import com.example.msamemberapi.application.service.impl.GuestCartService;
import com.example.msamemberapi.application.service.impl.MemberCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Cart", description = "장바구니 Api")
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final MemberCartService memberCartService;
    private final GuestCartService guestCartService;

    @Operation(summary = "장바구니 조회", description = "장바구니 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "장바구니 조회 성공"),
            @ApiResponse(responseCode = "400", description = "파라미터 검증에 실패했을 때")
    })
    @GetMapping
    public CartDto getCart(@RequestHeader(value = "X-USER", required = false) Long userId,
                           @CookieValue("GUEST-ID") String guestId
    ) {
        if (userId != null) {
            return memberCartService.getCart(userId.toString());
        } else {
            return guestCartService.getCart(guestId);
        }
    }

    @Operation(summary = "장바구니에 아이템 추가", description = "장바구니에 책과 수량을 저장")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "장바구니에 책을 성공적으로 저장"),
            @ApiResponse(responseCode = "400", description = "파라미터 검증에 실패했을 때"),
            @ApiResponse(responseCode = "404", description = "책을 찾을 수 없을 때"),
            @ApiResponse(responseCode = "409", description = "이미 장바구니에 중복된 책이 있을 때"),
            @ApiResponse(responseCode = "409", description = "책의 수량이 부족하여 담을 수 없을 때"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @PostMapping
    public CartDto addCart(@RequestHeader(value = "X-USER", required = false) Long userId,
                           @CookieValue("GUEST-ID") String guestId,
                           @RequestParam Long bookId,
                           @RequestParam int quantity) {

        if (userId != null) {
            return memberCartService.addCart(userId.toString(), bookId, quantity);
        } else {
            return guestCartService.addCart(guestId, bookId, quantity);
        }
    }


    @Operation(summary = "장바구니에 있는 아이템의 수량을 변경", description = "장바구니에 책과 수량을 저장")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "장바구니의 수량을 성공적으로 변경"),
            @ApiResponse(responseCode = "400", description = "수량이 음수일 때"),
            @ApiResponse(responseCode = "404", description = "장바구니에 책이 없을 때"),
            @ApiResponse(responseCode = "409", description = "책의 수량이 부족하여 변경할 수 없을 때"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @PutMapping("/book/quantity")
    public CartDto updateCartItemQuantity(@RequestHeader(value = "X-USER", required = false) Long userId,
                                          @RequestParam Long bookId,
                                          @CookieValue("GUEST-ID") String guestId,
                                          @RequestParam int quantity) {

        if (userId != null) {
            return memberCartService.updateCartItemQuantity(userId.toString(), bookId, quantity);
        } else {
            return guestCartService.updateCartItemQuantity(guestId, bookId, quantity);
        }
    }

    @Operation(summary = "장바구니에 있는 아이템을 삭제", description = "장바구니에 있는 아이템을 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "장바구니의 책을 성공적으로 삭제"),
            @ApiResponse(responseCode = "404", description = "장바구니에 책이 없을 때"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @DeleteMapping("/book")
    public CartDto deleteCartItem(@RequestHeader(value = "X-USER", required = false) Long userId,
                                  @CookieValue("GUEST-ID") String guestId,
                                  @RequestParam Long bookId) {

        if (userId != null) {
            return memberCartService.deleteCartItem(userId.toString(), bookId);
        } else {
            return guestCartService.deleteCartItem(guestId, bookId);
        }
    }

    @Operation(summary = "장바구니 초기화", description = "장바구니에 있는 모든 아이템을 초기화")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카트의 책을 성공적으로 삭제"),
            @ApiResponse(responseCode = "404", description = "카트에 책이 없을 때"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @DeleteMapping
    public CartDto clearCart(@RequestHeader(value = "X-USER", required = false) Long userId,
                             @CookieValue("GUEST-ID") String guestId) {

        if (userId != null) {
            return memberCartService.clearCart(userId.toString());
        } else {
            return guestCartService.clearCart(guestId);
        }
    }
}
