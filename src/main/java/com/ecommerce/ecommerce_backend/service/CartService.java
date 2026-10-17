package com.ecommerce.ecommerce_backend.service;
import java.util.List;
import com.ecommerce.ecommerce_backend.dto.UpdateCartRequest;
import com.ecommerce.ecommerce_backend.dto.CartResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.ecommerce_backend.dto.AddToCartRequest;
import com.ecommerce.ecommerce_backend.entity.CartItem;
import com.ecommerce.ecommerce_backend.entity.Product;
import com.ecommerce.ecommerce_backend.entity.User;
import com.ecommerce.ecommerce_backend.repository.CartItemRepository;
import com.ecommerce.ecommerce_backend.repository.ProductRepository;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(
            CartItemRepository cartItemRepository,
            ProductRepository productRepository) {

        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public CartItem addToCart(
            User user,
            AddToCartRequest request) {

        // Find the selected product
        Product product = productRepository
                .findById(request.getProductId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Product not found"
                        )
                );

        // Check whether the product is already in the cart
        CartItem cartItem = cartItemRepository
                .findByUserIdAndProductId(
                        user.getId(),
                        product.getId()
                )
                .orElse(null);

        int existingQuantity = cartItem == null
                ? 0
                : cartItem.getQuantity();

        int requestedQuantity = request.getQuantity();

        // Validate stock using long to avoid integer overflow
        long totalQuantity =
                (long) existingQuantity + requestedQuantity;

        if (product.getStockQuantity() == null
                || totalQuantity > product.getStockQuantity()) {

            throw new IllegalArgumentException(
                    "Insufficient product stock"
            );
        }

        // Create a new cart item if the product isn't already added
        if (cartItem == null) {

            cartItem = new CartItem();

            cartItem.setUser(user);
            cartItem.setProduct(product);
        }

        // Update the quantity
        cartItem.setQuantity((int) totalQuantity);

        // Save to MySQL
        return cartItemRepository.save(cartItem);
    }
    @Transactional(readOnly = true)
    public List<CartResponse> getCartItems(User user) {

        return cartItemRepository.findByUserId(user.getId())
                .stream()
                .map(cartItem -> {

                    Product product = cartItem.getProduct();

                    return new CartResponse(
                            cartItem.getId(),
                            product.getId(),
                            product.getName(),
                            product.getPrice(),
                            cartItem.getQuantity()
                    );
                })
                .toList();
    }
    @Transactional
    public void removeFromCart(User user, Long cartItemId) {

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Cart item not found"));

        // Prevent customers from deleting another user's cart items
        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Cart item not found");
        }

        cartItemRepository.delete(cartItem);
    }
    @Transactional
    public CartResponse updateCartQuantity(
            User user,
            Long cartItemId,
            UpdateCartRequest request) {

        // Find the cart item
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Cart item not found"));

        // Check that the cart item belongs to the logged-in user
        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Cart item not found");
        }

        // Get the product
        Product product = cartItem.getProduct();

        // Get the requested quantity
        Integer newQuantity = request.getQuantity();

        // Validate quantity
        if (newQuantity == null || newQuantity < 1) {
            throw new IllegalArgumentException(
                    "Quantity must be at least 1");
        }

        // Check available stock
        if (product.getStockQuantity() == null ||
                newQuantity > product.getStockQuantity()) {

            throw new IllegalArgumentException(
                    "Insufficient product stock");
        }

        // Update the quantity
        cartItem.setQuantity(newQuantity);

        // Save changes to MySQL
        cartItemRepository.save(cartItem);

        // Return updated cart details
        return new CartResponse(
                cartItem.getId(),
                product.getId(),
                product.getName(),
                product.getPrice(),
                cartItem.getQuantity()
        );
    }
}
