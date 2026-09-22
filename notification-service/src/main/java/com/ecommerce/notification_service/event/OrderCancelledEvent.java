package com.ecommerce.notification_service.event;

public record OrderCancelledEvent(String orderNumber, String reason, String email) {
}
