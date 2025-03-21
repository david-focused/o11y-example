package com.example.inventory_service.service;

import org.springframework.stereotype.Service;
import java.util.logging.Logger;

@Service
public class InventoryService {
    private static final Logger logger = Logger.getLogger(InventoryService.class.getName());

    public Integer getInventory(Long id) {
        logger.info("Getting inventory for product ID: " + id);

        Integer quantity = simulateDbLookup();

        logger.info("Available quantity: " + quantity);

        return quantity;
    }

    private Integer simulateDbLookup() {
        return 100;
    }
}
