package com.ecommerce.ecommerce_backend.dto;

import java.math.BigDecimal;

public class CartResponse {

    private Long cartItemId;
    private Long productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal totalPrice;

    public CartResponse(
            Long cartItemId,
            Long productId,
            String productName,
            BigDecimal price,
            Integer quantity) {

        this.cartItemId = cartItemId;
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;

        this.totalPrice = price.multiply(
                BigDecimal.valueOf(quantity)
        );
    }

    public Long getCartItemId() {
        return cartItemId;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
}
