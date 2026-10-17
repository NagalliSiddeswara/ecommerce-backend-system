package com.ecommerce.ecommerce_backend.service;
import com.ecommerce.ecommerce_backend.dto.OrderDetailsResponse;
import com.ecommerce.ecommerce_backend.dto.OrderItemResponse;
import com.ecommerce.ecommerce_backend.dto.OrderResponse;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.ecommerce_backend.entity.CartItem;
import com.ecommerce.ecommerce_backend.entity.Order;
import com.ecommerce.ecommerce_backend.entity.OrderItem;
import com.ecommerce.ecommerce_backend.entity.Product;
import com.ecommerce.ecommerce_backend.entity.User;

import com.ecommerce.ecommerce_backend.repository.CartItemRepository;
import com.ecommerce.ecommerce_backend.repository.OrderRepository;
import com.ecommerce.ecommerce_backend.repository.OrderItemRepository;
import com.ecommerce.ecommerce_backend.repository.ProductRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Order checkout(User user) {

        // Step 1: Get customer's cart items
        List<CartItem> cartItems =
                cartItemRepository.findByUserId(user.getId());

        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException(
                    "Shopping cart is empty");
        }

        // Step 2: Validate stock and calculate total
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {

            Product product = cartItem.getProduct();
            int quantity = cartItem.getQuantity();

            if (quantity < 1 ||
                    product.getStockQuantity() == null ||
                    quantity > product.getStockQuantity()) {

                throw new IllegalArgumentException(
                        "Insufficient stock for product: "
                                + product.getName());
            }

            BigDecimal itemTotal =
                    product.getPrice().multiply(
                            BigDecimal.valueOf(quantity));

            totalAmount = totalAmount.add(itemTotal);
        }

        // Step 3: Create order
        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(totalAmount);
        order.setStatus("PLACED");

        Order savedOrder = orderRepository.save(order);

        // Step 4: Create order items and update stock
        for (CartItem cartItem : cartItems) {

            Product product = cartItem.getProduct();
            int quantity = cartItem.getQuantity();

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(product);
            orderItem.setQuantity(quantity);
            orderItem.setPrice(product.getPrice());

            BigDecimal itemTotal =
                    product.getPrice().multiply(
                            BigDecimal.valueOf(quantity));

            orderItem.setTotalPrice(itemTotal);

            orderItemRepository.save(orderItem);

            // Reduce available product stock
            product.setStockQuantity(
                    product.getStockQuantity() - quantity);

            productRepository.save(product);
        }

        // Step 5: Clear customer's shopping cart
        cartItemRepository.deleteAll(cartItems);

        return savedOrder;
    }
    @Transactional(readOnly = true)
    public List<Order> getOrderHistory(User user) {

        return orderRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId());
    }
    @Transactional(readOnly = true)
    public OrderDetailsResponse getOrderDetails(User user, Long orderId) {

        // Find the order
	Order order = orderRepository.findById(orderId)
	        .orElseThrow(() ->
	                new IllegalArgumentException("Order not found"));

        // Ensure the order belongs to the logged-in customer
        if (!order.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Order not found");
        }

        // Retrieve purchased products
        List<OrderItem> orderItems =
                orderItemRepository.findByOrderId(orderId);

        // Convert order items into response DTOs
        List<OrderItemResponse> items = orderItems.stream()
                .map(item -> new OrderItemResponse(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getTotalPrice()
                ))
                .toList();

        // Return complete order details
        return new OrderDetailsResponse(
                order.getId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt(),
                items
        );
    }
    @Transactional
    public OrderResponse cancelOrder(User user, Long orderId) {

        // Find the order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Order not found"));

        // Check ownership
        if (!order.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Order not found");
        }

        // Prevent cancelling an already cancelled order
        if (!"PLACED".equals(order.getStatus())) {
            throw new IllegalArgumentException(
                    "Only placed orders can be cancelled");
        }

        // Get purchased products
        List<OrderItem> orderItems =
                orderItemRepository.findByOrderId(orderId);

        // Restore stock
        for (OrderItem item : orderItems) {

            Product product = item.getProduct();

            product.setStockQuantity(
                    product.getStockQuantity() + item.getQuantity()
            );

            productRepository.save(product);
        }

        // Update order status
        order.setStatus("CANCELLED");

        Order savedOrder = orderRepository.save(order);

        return new OrderResponse(
                savedOrder.getId(),
                savedOrder.getTotalAmount(),
                savedOrder.getStatus(),
                savedOrder.getCreatedAt()
        );
    }
}
