package com.ecommerce.ecommerce_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.ecommerce_backend.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // Find all cart items belonging to a customer
    List<CartItem> findByUserId(Long userId);

    // Find a specific product in a customer's cart
    Optional<CartItem> findByUserIdAndProductId(
            Long userId,
            Long productId
    );

    // Remove all items from a customer's cart
    void deleteByUserId(Long userId);
}
