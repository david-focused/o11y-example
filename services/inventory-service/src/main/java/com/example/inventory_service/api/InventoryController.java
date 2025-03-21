package com.example.inventory_service.api;

import java.util.logging.Logger;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.common.dto.InventoryResponse;
import com.example.inventory_service.service.InventoryService;

@RestController
@RequestMapping("inventory")
public class InventoryController {
    private static final Logger logger = Logger.getLogger(InventoryController.class.getName());
    private final InventoryService inventoryService;
    
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<InventoryResponse> getInventory(@PathVariable Long id) {
        logger.info("Checking inventory for product ID: " + id);
        return ResponseEntity.ok(new InventoryResponse( inventoryService.getInventory(id)));
    }
}
