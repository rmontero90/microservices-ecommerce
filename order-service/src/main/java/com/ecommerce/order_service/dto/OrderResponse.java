package com.ecommerce.order_service.dto;

import java.util.List;

public class OrderResponse {
    private Long id;
    private String orderNumber;
    private List<OrderLineItemsResponse> orderLineItemsList;
}
