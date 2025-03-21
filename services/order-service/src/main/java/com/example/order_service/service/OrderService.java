package com.example.order_service.service;

import org.springframework.beans.factory.annotation.Qualifier;
import com.example.common.dto.InventoryResponse;
import com.example.common.dto.ShippingRequest;
import com.example.common.dto.ShippingResponse;
import com.example.common.model.ShippingMethod;
import com.example.order_service.model.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class OrderService {
    private static final Logger logger = Logger.getLogger(OrderService.class.getName());

    private final RestTemplate inventoryRestTemplate;
    private final RestTemplate shippingRestTemplate;

    public OrderService(
            @Qualifier("inventoryRestTemplate") RestTemplate inventoryRestTemplate,
            @Qualifier("shippingRestTemplate") RestTemplate shippingRestTemplate) {
        this.inventoryRestTemplate = inventoryRestTemplate;
        this.shippingRestTemplate = shippingRestTemplate;
    }

    public String createOrder(Order order) {
        logger.info("Creating order with productId: " + order.getProductId() +
                ", quantity: " + order.getQuantity() +
                ", shipping method: " + order.getShippingMethod());

        boolean inventoryAvailable = checkInventoryAvailability(order.getProductId(), order.getQuantity());
        if (!inventoryAvailable) {
            logger.severe("Insufficient inventory");
            throw new RuntimeException("Insufficient inventory for product ID: " + order.getProductId());
        }

        return ship(order);
    }

    private boolean checkInventoryAvailability(Long productId, Integer requiredQuantity) {
        try {
            String path = "/inventory/" + productId;
            logger.info("Checking inventory for product ID: " + productId);

            ResponseEntity<InventoryResponse> response = inventoryRestTemplate.getForEntity(
                    path,
                    InventoryResponse.class);

            InventoryResponse inventoryResponse = response.getBody();

            Integer availableQuantity = inventoryResponse.getQuantity();
            logger.info("Inventory check for product ID: " + productId +
                    ", available: " + availableQuantity +
                    ", required: " + requiredQuantity);

            return availableQuantity >= requiredQuantity;
        } catch (Exception e) {
            logger.severe("Inventory Service Error");
            return false;
        }
    }

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

            logger.info("Shipment created for product ID: " + order.getProductId() +
                    ", quantity: " + order.getQuantity() +
                    ", and shipping method: " + order.getShippingMethod());

            return response.getBody().getTrackingNumber();
        } catch (Exception e) {
            logger.severe("Shipping Service Error");
            throw new RuntimeException("Shipping Service Error");
        }
    }

}
