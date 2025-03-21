package com.example.shipping_service.service;

import com.example.common.dto.ShippingRequest;
import com.example.common.dto.ShippingResponse;
import com.example.common.model.ShippingMethod;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@Service
public class ShippingService {
    private static final Logger logger = LoggerFactory.getLogger(ShippingService.class);

    public ShippingResponse processShippingRequest(ShippingRequest request) {
        try {
            MDC.put("productId", request.getProductId().toString());
            MDC.put("quantity", request.getQuantity().toString());
            MDC.put("shippingMethod", request.getShippingMethod().toString());
            MDC.put("requestId", UUID.randomUUID().toString());
            MDC.put("action", "processShipping");
            
            logger.info("Processing shipping request");

            try {
                logger.info("Calling third-party shipping provider");
                String trackingNumber = simulateThirdPartyShippingServiceCall(request);
                MDC.put("trackingNumber", trackingNumber);
                logger.info("Shipping request processed successfully");

                return new ShippingResponse(trackingNumber);
            } catch (Exception e) {
                MDC.put("errorType", "shippingProviderError");
                logger.error("Error processing shipping request", e);
                throw e;
            }
        } finally {
            MDC.clear();
        }
    }

    private String simulateThirdPartyShippingServiceCall(ShippingRequest request) {
        try {        
            if (request.getQuantity() > 75 && request.getShippingMethod() == ShippingMethod.NEXT_DAY) {
                // very slow shipping: Delaying response for 10 seconds for high quantity and NEXT_DAY shipping
                TimeUnit.SECONDS.sleep(10);
            } else if (request.getQuantity() > 75) {
                // slow shipping: Delaying response for 1 second for high quantity
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
