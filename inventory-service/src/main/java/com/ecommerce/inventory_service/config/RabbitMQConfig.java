package com.ecommerce.inventory_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String INVENTORY_QUEUE = "inventory-queue";

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }


    @Bean
    public Queue orderQueue() {
        return new Queue(INVENTORY_QUEUE, true);
    }

    @Bean
    public TopicExchange orderEventsExchange() {
        return new TopicExchange("order-events");
    }

    @Bean
    public Binding orderEventsBinding(Queue inventoriesQueue, TopicExchange orderEventsExchange) {
        return BindingBuilder.bind(inventoriesQueue).to(orderEventsExchange).with("order.placed");
    }
}
