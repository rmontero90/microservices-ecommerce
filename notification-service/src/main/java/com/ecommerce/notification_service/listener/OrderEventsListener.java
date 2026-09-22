package com.ecommerce.notification_service.listener;

import com.ecommerce.notification_service.event.OrderCancelledEvent;
import com.ecommerce.notification_service.event.OrderConfirmedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@RabbitListener(queues = "notification-queue")
public class OrderEventsListener {

    private final JavaMailSender mailSender;

    @RabbitHandler
    public void handleOrderConfirmedEvent(OrderConfirmedEvent event) {

        log.info("Confirmed for Order: {}", event.orderNumber());

//        throw new RuntimeException("SMTP Error");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("pedidos@ecommerce.com");
        message.setTo(event.email());
        message.setSubject("Order Placed - " + event.orderNumber());
        message.setText("Hello!\n\n" + "Thank for you purchase from us");
        mailSender.send(message);

        log.info("Sending confirmation email to: {}", event.email());
        log.info("Email successfully for order: {}", event.orderNumber());
    }
    @RabbitHandler
    public void handleOrderCancelledEvent(OrderCancelledEvent event) {
        log.info("Cancelled for Order: {}", event.orderNumber());

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(event.email());
        message.setSubject("Order Cancelled - " + event.orderNumber());
        message.setText("Hello!\n\n" +
                "Sorry, the order has been cancelled.\n\n" + event.reason());
        mailSender.send(message);

        log.info("Sending Cancellation email to: {}", event.email());
        log.info("Email successfully for cancellation of order: {}", event.orderNumber());
    }
}
