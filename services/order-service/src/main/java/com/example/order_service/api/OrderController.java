package com.example.order_service.api;

import com.example.order_service.model.Order;
import com.example.order_service.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private static final Logger logger = Logger.getLogger(OrderController.class.getName());
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createOrder(@RequestBody Order order) {
        try {
            String trackingNumber = orderService.createOrder(order);
            return ResponseEntity.ok(trackingNumber);
        } catch (Exception e) {
            logger.severe("Error creating order: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
