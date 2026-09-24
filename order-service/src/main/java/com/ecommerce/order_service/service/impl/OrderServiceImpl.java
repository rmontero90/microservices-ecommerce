package com.ecommerce.order_service.service.impl;

import com.ecommerce.order_service.dto.OrderRequest;
import com.ecommerce.order_service.dto.OrderResponse;
import com.ecommerce.order_service.event.OrderPlacedEvent;
import com.ecommerce.order_service.exception.ResourceNotFoundException;
import com.ecommerce.order_service.mapper.OrderMapper;
import com.ecommerce.order_service.model.Order;
import com.ecommerce.order_service.model.OrderStatus;
import com.ecommerce.order_service.repository.OrderRepository;
import com.ecommerce.order_service.service.OrderService;
import com.ecommerce.order_service.service.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@RefreshScope
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
//    private final WebClient.Builder webClientBuilder;
//    private final InventoryClient inventoryClient;
    private final RabbitTemplate rabbitTemplate;
    private final OutboxService outboxService;

    @Value("${order.enabled:true}")
    private boolean ordersEnabled;

    public OrderResponse fallbackMethod(OrderRequest orderRequest, String userId, Throwable throwable) {
        log.error("Circuit Breaker activated. Cause: {}", throwable.getMessage());
        throw new RuntimeException("Inventory service not available. Try again later.");
    }

    @Override
    @Transactional
//    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
//    @Retry(name = "inventory")
    public OrderResponse placeOrder(OrderRequest orderRequest, String userId) {

        log.info("Placing new order");

        if (!ordersEnabled) {
            log.warn("Orders are disabled by config.");
            throw new RuntimeException("Orders are currently disabled. Maintenance in progress. Try again later.");
        }

        Order order = orderMapper.toOrder(orderRequest);
        order.setUserId(userId);
//        for (var item : order.getOrderLineItemsList()) {
//            String sku = item.getSku();
//            Integer quantity = item.getQuantity();
//
//            try {
////            webClientBuilder.build().put()
////                    .uri("http://localhost:8080/api/v1/inventory/reduce/" + sku,
////                            uriBuilder -> uriBuilder.queryParam("quantity", quantity).build())
////                    .retrieve()
////                    .bodyToMono(String.class)
////                    .block();
//                inventoryClient.reduceStock(sku, quantity);
//            } catch (Exception e) {
//                log.error("Error while trying to reduce stock of product {}: {}", sku, e.getMessage());
//                throw new IllegalArgumentException("Cannot place order: Insufficient quantity in the inventory");
//            }
//        }

        order.setOrderNumber(UUID.randomUUID().toString());
        order.setOrderStatus(OrderStatus.PLACED);
        Order savedOrder = orderRepository.save(order);
        log.info("Saved order id: {}", savedOrder.getId());

        List<OrderPlacedEvent.OrderItemEvent> orderItems =
                order.getOrderLineItemsList().stream()
                        .map(item -> new OrderPlacedEvent.OrderItemEvent(
                                item.getSku(), item.getPrice().toString(), item.getQuantity()
                        )).toList();
        OrderPlacedEvent event = new OrderPlacedEvent(
                savedOrder.getOrderNumber(), orderRequest.getEmail(), orderItems
        );

        boolean sentToRabbit = false;

        try {
            rabbitTemplate.convertAndSend("order-events", "order.placed", event);
            sentToRabbit = true;
            log.info("Sent order: {} placed sent to RabbitMQ", savedOrder.getOrderNumber());
        } catch (AmqpException e) {
            log.error("Error while sending order: {} to RabbitMQ.", savedOrder.getOrderNumber());
        }

        outboxService.saveOrderPlacedEvent(event, sentToRabbit);

        return orderMapper.toOrderResponse(savedOrder);
    }
//    @Override
//    @Transactional(readOnly = true)
//    public List<OrderResponse> getAllOrders() {
//        return orderRepository.findAll()
//                .stream()
//                .map(orderMapper::toOrderResponse)
//                .toList();
//    }

    @Override
    public List<OrderResponse> getOrders(String userId, boolean isAdmin) {
        List<Order> orders;

        if (isAdmin) {
            orders = orderRepository.findAll();
        } else {
            orders = orderRepository.findByUserId(userId);
        }
        return orders.stream()
                .map(orderMapper::toOrderResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(
                () -> new ResourceNotFoundException("Order", "id", id)
        );
        return orderMapper.toOrderResponse(order);
    }

    @Override
    @Transactional
    public void deleteOrderById(Long id) {

        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order", "id", id);
        }
        orderRepository.deleteById(id);
        log.info("Deleted order ID: {}", id);
    }

    @Override
    @Transactional
    public void updateOrderStatus(String orderNumber, OrderStatus newStatus) {
        orderRepository.findByOrderNumber(orderNumber)
                .ifPresentOrElse(order -> {
                    order.setOrderStatus(newStatus);
                    orderRepository.save(order);
                    log.info("Updated order status: {}", newStatus);
                },
                        () -> log.error("No order found with order number: {}", orderNumber)
                );
    }
}
