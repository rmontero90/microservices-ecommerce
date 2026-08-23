package com.ecommerce.inventory_service.service;

import com.ecommerce.inventory_service.dto.InventoryRequestDTO;
import com.ecommerce.inventory_service.dto.InventoryResponseDTO;

import java.util.List;

public interface InventoryService {
    boolean isStock(String sku, Integer quantity);
    InventoryResponseDTO createInventory(InventoryRequestDTO inventoryRequestDTO);
    List<InventoryResponseDTO> getAllInventory();
    InventoryResponseDTO updateInventory(Long id, InventoryRequestDTO inventoryRequestDTO);
    void deleteInventory(Long sku);
    void reduceStock(String sku, Integer quantity);

}
