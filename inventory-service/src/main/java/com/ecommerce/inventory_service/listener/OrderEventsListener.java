package com.ecommerce.inventory_service.listener;

import com.ecommerce.inventory_service.event.OrderPlacedEvent;
import com.ecommerce.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class OrderEventsListener {
    private final InventoryService inventoryService;

    @RabbitListener(queues = "inventory-queue")
    public void handleOrderPlacedEvent(OrderPlacedEvent event) {

        log.info("Event Received in Inventory for Order: {}", event.orderNumber());

        event.items().forEach(item -> {
           try {
               inventoryService.reduceStock(item.sku(), item.quantity());
               log.info("Order placed item {} with quantity {}", item.sku(), item.quantity());
           } catch (Exception e) {
               log.error("Error while processing order placed item SKU {}: {}", item.sku(), e.getMessage());
           }
        });
    }
}
