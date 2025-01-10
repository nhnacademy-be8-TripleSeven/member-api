package com.example.msamemberapi.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CartItem cartItem)) return false;
            return regularPrice == cartItem.regularPrice && salePrice == cartItem.salePrice && quantity == cartItem.quantity && Objects.equals(bookId, cartItem.bookId) && Objects.equals(name, cartItem.name) && Objects.equals(coverUrl, cartItem.coverUrl);
        }

        @Override
        public int hashCode() {
            return Objects.hash(bookId, name, coverUrl, regularPrice, salePrice, quantity);
        }
    }

}
