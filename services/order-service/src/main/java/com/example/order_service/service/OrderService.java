package com.example.order_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

    public String createOrder(Order order) {
        try {
            MDC.put("orderId", String.valueOf(System.currentTimeMillis()));
            MDC.put("productId", order.getProductId().toString());
            MDC.put("quantity", order.getQuantity().toString());
            MDC.put("shippingMethod", order.getShippingMethod().toString());
            
            logger.info("Creating order");

            boolean inventoryAvailable = checkInventoryAvailability(order.getProductId(), order.getQuantity());
            if (!inventoryAvailable) {
                logger.error("Insufficient inventory");
                throw new RuntimeException("Insufficient inventory for product ID: " + order.getProductId());
            }

            return ship(order);
        } finally {
            MDC.clear();
        }
    }

    private boolean checkInventoryAvailability(Long productId, Integer requiredQuantity) {
        try {
            String path = "/inventory/" + productId;
            MDC.put("action", "checkInventory");
            logger.info("Checking inventory");

            ResponseEntity<InventoryResponse> response = inventoryRestTemplate.getForEntity(
                    path,
                    InventoryResponse.class);

            InventoryResponse inventoryResponse = response.getBody();

            Integer availableQuantity = inventoryResponse.getQuantity();
            MDC.put("availableQuantity", availableQuantity.toString());
            MDC.put("requiredQuantity", requiredQuantity.toString());
            logger.info("Inventory check completed");

            return availableQuantity >= requiredQuantity;
        } catch (Exception e) {
            MDC.put("errorType", "inventoryServiceError");
            logger.error("Inventory Service Error", e);
            return false;
        }
    }

    private String ship(Order order) {
        try {
            String path = "/shipping/ship";
            MDC.put("action", "shipOrder");
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
            MDC.put("trackingNumber", trackingNumber);
            logger.info("Shipment created");

            return trackingNumber;
        } catch (Exception e) {
            MDC.put("errorType", "shippingServiceError");
            logger.error("Shipping Service Error", e);
            throw new RuntimeException("Shipping Service Error");
        }
    }

}
