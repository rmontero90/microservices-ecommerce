package com.ecommerce.notification_service.listener;

import com.ecommerce.notification_service.event.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderEventsListener {

    private final JavaMailSender mailSender;

    @RabbitListener(queues = "notification-queue")
    public void handleOrderConfirmedEvent(OrderPlacedEvent event) {

        log.info("Confirmed for Order: {}", event.orderNumber());

           try {

               SimpleMailMessage message = new SimpleMailMessage();
               message.setFrom("pedidos@ecommerce.com");
               message.setTo(event.email());
               message.setSubject("Order Placed - "+event.orderNumber());
               message.setText("Hello!\n\n" +
                       "Thank for you purchase from us");
               mailSender.send(message);

               log.info("Sending confirmation email to: {}", event.email());
               log.info("Email successfully for order: {}", event.orderNumber());
           } catch (Exception e) {
               log.error("Error sending confirmation email to: {}", e.getMessage());
           }
    }
}
