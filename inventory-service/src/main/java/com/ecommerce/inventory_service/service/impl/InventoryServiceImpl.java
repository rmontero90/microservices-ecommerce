package com.ecommerce.inventory_service.service.impl;

import com.ecommerce.inventory_service.dto.InventoryRequest;
import com.ecommerce.inventory_service.dto.InventoryResponse;
import com.ecommerce.inventory_service.mapper.InventoryMapper;
import com.ecommerce.inventory_service.repository.InventoryRepository;
import com.ecommerce.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;

    @Override
    public boolean isStock(String sku, Integer quantity) {
        return false;
    }

    @Override
    public InventoryResponse createInventory(InventoryRequest inventoryRequest) {
        return null;
    }

    @Override
    public List<InventoryResponse> getAllInventory() {
        return List.of();
    }

    @Override
    public InventoryResponse updateInventory(InventoryRequest inventoryRequest) {
        return null;
    }

    @Override
    public void deleteInventory(Long sku) {

    }
}
