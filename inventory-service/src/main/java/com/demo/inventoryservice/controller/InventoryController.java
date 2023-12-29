package com.demo.inventoryservice.controller;

import com.demo.inventoryservice.model.Inventory;
import com.demo.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;
    @GetMapping("/{skuId}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<Inventory> isInStock(@PathVariable String skuId){
        return inventoryService.isInStock(skuId);
    }
}
