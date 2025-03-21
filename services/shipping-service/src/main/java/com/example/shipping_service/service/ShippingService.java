package com.example.shipping_service.service;

import com.example.common.dto.ShippingRequest;
import com.example.common.dto.ShippingResponse;
import com.example.common.model.ShippingMethod;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.logging.Level;

@Service
public class ShippingService {
    private static final Logger logger = Logger.getLogger(ShippingService.class.getName());

    public ShippingResponse processShippingRequest(ShippingRequest request) {
        logger.info("Processing shipping request for product: " + request.getProductId() + ", quantity: "
                + request.getQuantity());

        try {
            // oops - forgot to add shipping method to log
            logger.info(
                    "Calling third-party shipping provider for product: " + request.getProductId() + ", quantity: "
                            + request.getQuantity());
            String trackingNumber = simulateThirdPartyShippingServiceCall(request);
            logger.info("Third-party shipping provider call completed. Tracking number: " + trackingNumber);

            ShippingResponse response = new ShippingResponse(trackingNumber);

            logger.info("Shipping request processed successfully: " + response);
            return response;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing shipping request", e);
            throw e;
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
