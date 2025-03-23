package com.example.inventory_service.service;

import org.springframework.stereotype.Service;

import io.opentelemetry.instrumentation.annotations.WithSpan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class InventoryService {
    private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);

    @WithSpan
    public Integer getInventory(Long id) {
        logger.info("Getting inventory for productId: {}", id);

        Integer quantity = simulateDbLookup();

        logger.info("Inventory retrieved: {}", quantity);

        return quantity;
    }

    @WithSpan
    private Integer simulateDbLookup() {
        return 100;
    }
}
