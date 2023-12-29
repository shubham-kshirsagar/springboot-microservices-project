package com.demo.inventoryservice.service;

import com.demo.inventoryservice.model.Inventory;
import com.demo.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    @Transactional(readOnly = true)
    public Optional<Inventory> isInStock(String skuId) {
        return inventoryRepository.findBySkuId(skuId);
    }
}
