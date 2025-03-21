package com.example.shipping_service.api;

import java.util.logging.Logger;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.common.dto.ShippingRequest;
import com.example.common.dto.ShippingResponse;
import com.example.shipping_service.service.ShippingService;

@RestController
@RequestMapping("/shipping")
public class ShippingController {
    private static final Logger logger = Logger.getLogger(ShippingController.class.getName());
    
    private final ShippingService shippingService;
    
    public ShippingController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }
    
    @PostMapping("/ship")
    public ResponseEntity<ShippingResponse> createShipment(@RequestBody ShippingRequest request) {
        logger.info("Received shipping request: " + request);
        
        ShippingResponse response = shippingService.processShippingRequest(request);
        
        logger.info("Returning shipping response: " + response);
        return ResponseEntity.ok(response);
    }
}
