package com.ecommerce.notification_service.listener;

import com.ecommerce.notification_service.event.OrderPlacedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderEventsListener {

    @RabbitListener(queues = "notification-queue")
    public void handleOrderPlacedEvent(OrderPlacedEvent event) {

        log.info("Event Received in Inventory for Order: {}", event.orderNumber());

        event.items().forEach(item -> {
           try {
               log.info("Sending confirmation email to: {}", event.email());
               log.info("Email succesfully for order: {}", event.orderNumber());
           } catch (Exception e) {
               log.error("Error sending confirmation email to: {}", e.getMessage());
           }
        });
    }
}
