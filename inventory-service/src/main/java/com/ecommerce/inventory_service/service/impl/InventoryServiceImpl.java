package com.ecommerce.inventory_service.service.impl;

import com.ecommerce.inventory_service.dto.InventoryRequestDTO;
import com.ecommerce.inventory_service.dto.InventoryResponseDTO;
import com.ecommerce.inventory_service.exception.ResourceNotFoundException;
import com.ecommerce.inventory_service.mapper.InventoryMapper;
import com.ecommerce.inventory_service.model.Inventory;
import com.ecommerce.inventory_service.repository.InventoryRepository;
import com.ecommerce.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;

    @Override
    @Transactional(readOnly = true)
    public boolean isStock(String sku, Integer quantity) {
        return inventoryRepository.findBySku(sku)
                .map(inventory -> inventory.getQuantity() >= quantity)
                .orElse(false);
    }

    @Override
    @Transactional
    public InventoryResponseDTO createInventory(InventoryRequestDTO inventoryRequestDTO) {

        boolean exists = inventoryRepository.existBySku(inventoryRequestDTO.getSku());
        if (exists) {
            throw new RuntimeException(String.format("Inventory already exists for sku: %s", inventoryRequestDTO.getSku()));
        }

        Inventory inventory = inventoryMapper.toModel(inventoryRequestDTO);
        Inventory savedInventory = inventoryRepository.save(inventory);

        return inventoryMapper.toResponse(savedInventory);

    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponseDTO> getAllInventory() {

        return inventoryRepository.findAll()
                .stream()
                .map(inventoryMapper::toResponse)
                .toList();

    }

    @Override
    @Transactional
    public InventoryResponseDTO updateInventory(Long id, InventoryRequestDTO inventoryRequestDTO) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Inventory", "id", id)
                );
        inventory.setSku(inventoryRequestDTO.getSku());
        inventory.setQuantity(inventoryRequestDTO.getQuantity());

        Inventory savedInventory = inventoryRepository.save(inventory);

        log.info("Updating inventory with id {}", id);

        return inventoryMapper.toResponse(savedInventory);
    }

    @Override
    @Transactional
    public void deleteInventory(Long id) {
        if(!inventoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Inventory", "id", id);
        }
        inventoryRepository.deleteById(id);
        log.info("Deleting inventory with id {}", id);
    }
}
