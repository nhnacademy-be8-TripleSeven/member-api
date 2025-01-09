package com.example.msamemberapi.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@Getter
public class CartDto implements Serializable {

    private final String Id;
    private List<CartItem> cartItems;

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class CartItem implements Serializable {
        private Long bookId;
        private String name;
        private String coverUrl;
        private int regularPrice;
        private int salePrice;
        private int quantity;

        public CartItem(BookDetailResponseDto bookDetail, long bookId, int quantity) {
            this.bookId = bookId;
            this.name = bookDetail.getTitle();
            this.coverUrl = bookDetail.getCoverUrl();
            this.regularPrice = bookDetail.getRegularPrice();
            this.salePrice = bookDetail.getSalePrice();
            this.quantity = quantity;
        }
    }

}
