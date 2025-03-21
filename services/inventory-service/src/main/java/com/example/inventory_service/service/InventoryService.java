package com.example.inventory_service.service;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@Service
public class InventoryService {
    private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);

    public Integer getInventory(Long id) {
        try {
            MDC.put("productId", id.toString());
            MDC.put("action", "getInventory");
            logger.info("Getting inventory");

            Integer quantity = simulateDbLookup();

            MDC.put("quantity", quantity.toString());
            logger.info("Inventory retrieved");

            return quantity;
        } finally {
            MDC.clear();
        }
    }

    private Integer simulateDbLookup() {
        return 100;
    }
}
