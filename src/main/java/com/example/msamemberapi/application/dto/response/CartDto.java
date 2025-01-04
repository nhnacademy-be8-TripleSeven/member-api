package com.example.msamemberapi.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
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
        private String tag;
        private String name;
        private int quantity;
    }
}
