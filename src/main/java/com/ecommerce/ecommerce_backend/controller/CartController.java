package com.ecommerce.ecommerce_backend.controller;
import com.ecommerce.ecommerce_backend.dto.UpdateCartRequest;
import java.util.List;
import com.ecommerce.ecommerce_backend.dto.AddToCartRequest;
import com.ecommerce.ecommerce_backend.dto.CartResponse;
import com.ecommerce.ecommerce_backend.entity.CartItem;
import com.ecommerce.ecommerce_backend.entity.Product;
import com.ecommerce.ecommerce_backend.entity.User;
import com.ecommerce.ecommerce_backend.service.CartService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping
    public ResponseEntity<CartResponse> addToCart(
            @Valid @RequestBody AddToCartRequest request,
            Authentication authentication) {

        // Get the logged-in user from the JWT authentication
        User user = (User) authentication.getPrincipal();

        // Add product to the customer's cart
        CartItem cartItem = cartService.addToCart(user, request);

        Product product = cartItem.getProduct();

        // Convert entity to response DTO
        CartResponse response = new CartResponse(
                cartItem.getId(),
                product.getId(),
                product.getName(),
                product.getPrice(),
                cartItem.getQuantity()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
    @GetMapping
    public ResponseEntity<List<CartResponse>> getCart(
            Authentication authentication) {

        // Get the authenticated customer
        User user = (User) authentication.getPrincipal();

        // Fetch the customer's cart items
        List<CartResponse> cartItems = cartService.getCartItems(user);

        return ResponseEntity.ok(cartItems);
    }
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> removeFromCart(
            @PathVariable Long cartItemId,
            Authentication authentication) {

        // Get the currently logged-in customer
        User user = (User) authentication.getPrincipal();

        // Remove the item from the customer's cart
        cartService.removeFromCart(user, cartItemId);

        // Return 204 No Content
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{cartItemId}")
    public ResponseEntity<CartResponse> updateCartQuantity(
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartRequest request,
            Authentication authentication) {

        // Get the logged-in customer
        User user = (User) authentication.getPrincipal();

        // Update the cart item quantity
        CartResponse response = cartService.updateCartQuantity(
                user,
                cartItemId,
                request
        );

        // Return updated cart details
        return ResponseEntity.ok(response);
    }
}
