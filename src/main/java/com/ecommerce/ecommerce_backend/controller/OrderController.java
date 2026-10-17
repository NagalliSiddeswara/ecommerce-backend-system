package com.ecommerce.ecommerce_backend.controller;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import com.ecommerce.ecommerce_backend.dto.OrderResponse;
import com.ecommerce.ecommerce_backend.dto.OrderDetailsResponse;
import org.springframework.web.bind.annotation.GetMapping;

import com.ecommerce.ecommerce_backend.dto.OrderResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.ecommerce_backend.entity.Order;
import com.ecommerce.ecommerce_backend.entity.User;
import com.ecommerce.ecommerce_backend.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponse> checkout(
            Authentication authentication) {

        // Get the authenticated customer
        User user = (User) authentication.getPrincipal();

        // Place the order
        Order order = orderService.checkout(user);

        // Return order details
        CheckoutResponse response = new CheckoutResponse(
                order.getId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    public record CheckoutResponse(
            Long orderId,
            java.math.BigDecimal totalAmount,
            String status,
            java.time.LocalDateTime createdAt
    ) {
    }
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrderHistory(
            Authentication authentication) {

        // Get the logged-in customer
        User user = (User) authentication.getPrincipal();

        // Fetch the customer's previous orders
        List<Order> orders = orderService.getOrderHistory(user);

        // Convert Order entities into response DTOs
        List<OrderResponse> response = orders.stream()
                .map(order -> new OrderResponse(
                        order.getId(),
                        order.getTotalAmount(),
                        order.getStatus(),
                        order.getCreatedAt()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailsResponse> getOrderDetails(
            @PathVariable Long orderId,
            Authentication authentication) {

        // Get the logged-in customer
        User user = (User) authentication.getPrincipal();

        // Fetch the customer's order details
        OrderDetailsResponse response =
                orderService.getOrderDetails(user, orderId);

        return ResponseEntity.ok(response);
    }
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable Long orderId,
            Authentication authentication) {

        // Get the logged-in customer
        User user = (User) authentication.getPrincipal();

        // Cancel the customer's order
        OrderResponse response =
                orderService.cancelOrder(user, orderId);

        return ResponseEntity.ok(response);
    }
}
