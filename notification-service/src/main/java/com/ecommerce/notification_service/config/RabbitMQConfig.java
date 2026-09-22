package com.ecommerce.notification_service.config;

import com.ecommerce.notification_service.event.OrderCancelledEvent;
import com.ecommerce.notification_service.event.OrderConfirmedEvent;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {
    @Bean
    public MessageConverter messageConverter() {

        JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter();
        DefaultClassMapper classMapper = new DefaultClassMapper();

        classMapper.setTrustedPackages("*");

        Map<String, Class<?>> idClassMapping = new HashMap<>();

        idClassMapping.put("com.ecommerce.inventory_service.event.OrderPlacedEvent", OrderConfirmedEvent.class);

        idClassMapping.put("com.ecommerce.inventory_service.event.OrderCancelledEvent", OrderCancelledEvent.class);

        classMapper.setIdClassMapping(idClassMapping);
        converter.setClassMapper(classMapper);

        return converter;
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable("notification-queue").
                withArgument("x-dead-letter-exchange", "notification-dlx")
                .withArgument("x-dead-letter-routing-key", "notification.dead")
                .build();
    }

    @Bean
    public TopicExchange orderEventsExchange() {
        return new TopicExchange("order-events");
    }

    @Bean
    public Binding binding(Queue notificationQueue, TopicExchange orderEventsExchange) {
        return BindingBuilder.bind(notificationQueue).to(orderEventsExchange).with("order.confirmed");
    }

    @Bean
    public Binding cancelOrderBinding(Queue notificationQueue, TopicExchange orderEventsExchange) {
        return BindingBuilder.bind(notificationQueue).to(orderEventsExchange).with("order.cancelled");
    }

    @Bean
    public DirectExchange deadLettersExchange() {
        return new DirectExchange("notification-dlx");
    }

    @Bean
    public Queue deadLettersQueue() {
        return new Queue("notification-dlq", true);
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLettersQueue, DirectExchange deadLettersExchange) {
        return BindingBuilder.bind(deadLettersQueue).to(deadLettersExchange).with("notification.dead");
    }
}
