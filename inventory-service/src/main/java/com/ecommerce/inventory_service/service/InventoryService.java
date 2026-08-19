package com.ecommerce.inventory_service.service;

import com.ecommerce.inventory_service.dto.InventoryRequest;
import com.ecommerce.inventory_service.dto.InventoryResponse;
import com.ecommerce.inventory_service.model.Inventory;

import java.util.List;

public interface InventoryService {
    boolean isStock(String sku, Integer quantity);
    InventoryResponse createInventory(InventoryRequest inventoryRequest);
    List<InventoryResponse> getAllInventory();
    InventoryResponse updateInventory(InventoryRequest inventoryRequest);
    void deleteInventory(Long sku);
}
