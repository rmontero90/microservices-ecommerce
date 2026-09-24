package com.ecommerce.order_service.service.impl;

import com.ecommerce.order_service.event.OrderPlacedEvent;
import com.ecommerce.order_service.model.OutboxEvent;
import com.ecommerce.order_service.repository.OutboxRepository;
import com.ecommerce.order_service.service.OutboxService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class OutboxServiceImpl  implements OutboxService {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void saveOrderPlacedEvent(OrderPlacedEvent event, boolean isProcessed) {

        String payload = objectMapper.writeValueAsString(event);

        OutboxEvent outboxEvent = OutboxEvent.builder()
                .aggregateId(event.orderNumber())
                .type("ORDER_PLACED")
                .payload(payload)
                .createAt(LocalDateTime.now())
                .processed(isProcessed)
                .build();
        outboxRepository.save(outboxEvent);
        log.info("Event assured in Outbox saved with order: {}", event.orderNumber() );

    }
}
