package com.example.order_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.opentelemetry.instrumentation.annotations.SpanAttribute;

import com.example.common.dto.InventoryResponse;
import com.example.common.dto.ShippingRequest;
import com.example.common.dto.ShippingResponse;
import com.example.order_service.model.Order;

@Service
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final RestTemplate inventoryRestTemplate;
    private final RestTemplate shippingRestTemplate;

    public OrderService(
            @Qualifier("inventoryRestTemplate") RestTemplate inventoryRestTemplate,
            @Qualifier("shippingRestTemplate") RestTemplate shippingRestTemplate) {
        this.inventoryRestTemplate = inventoryRestTemplate;
        this.shippingRestTemplate = shippingRestTemplate;
    }

    @WithSpan
    public String createOrder(@SpanAttribute("order") Order order) {
        logger.info("Creating order");

        boolean inventoryAvailable = checkInventoryAvailability(order.getProductId(), order.getQuantity());
        if (!inventoryAvailable) {
            logger.error("Insufficient inventory");
            throw new RuntimeException("Insufficient inventory for product ID: " + order.getProductId());
        }

        try {
            return ship(order);
        } catch (Exception e) {
            logger.error("Error shipping order", e);
            throw e;
        }
    }

    @WithSpan
    private boolean checkInventoryAvailability(Long productId, Integer requiredQuantity) {
        try {
            String path = "/inventory/" + productId;
            logger.info("Checking inventory");

            ResponseEntity<InventoryResponse> response = inventoryRestTemplate.getForEntity(
                    path,
                    InventoryResponse.class);

            InventoryResponse inventoryResponse = response.getBody();

            Integer availableQuantity = inventoryResponse.getQuantity();
            logger.info("Inventory check completed - available: {}, required: {}", availableQuantity, requiredQuantity);

            return availableQuantity >= requiredQuantity;
        } catch (Exception e) {
            logger.error("Inventory Service Error", e);
            return false;
        }
    }

    @WithSpan
    private String ship(Order order) {
        try {
            String path = "/shipping/ship";
            logger.info("Shipping order");

            ShippingRequest shippingRequest = new ShippingRequest(
                    order.getProductId(),
                    order.getQuantity(),
                    order.getShippingMethod());

            ResponseEntity<ShippingResponse> response = shippingRestTemplate.postForEntity(
                    path,
                    shippingRequest,
                    ShippingResponse.class);

            String trackingNumber = response.getBody().getTrackingNumber();
            logger.info("Shipment created with tracking number: {}", trackingNumber);

            return trackingNumber;
        } catch (Exception e) {
            logger.error("Shipping Service Error", e);
            throw new RuntimeException("Shipping Service Error");
        }
    }

}
