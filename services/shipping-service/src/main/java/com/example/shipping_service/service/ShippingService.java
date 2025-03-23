package com.example.shipping_service.service;

import com.example.common.dto.ShippingRequest;
import com.example.common.dto.ShippingResponse;
import com.example.common.model.ShippingMethod;
import org.springframework.stereotype.Service;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.opentelemetry.instrumentation.annotations.SpanAttribute;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ShippingService {
    private static final Logger logger = LoggerFactory.getLogger(ShippingService.class);

    @WithSpan
    public ShippingResponse processShippingRequest(@SpanAttribute("shippingRequest") ShippingRequest request) {
        logger.info("Processing shipping request for productId: {}, quantity: {}, method: {}", 
                   request.getProductId(), request.getQuantity(), request.getShippingMethod());

        Span currentSpan = Span.current();
        currentSpan.setAttribute("productId", request.getProductId());
        currentSpan.setAttribute("quantity", request.getQuantity());
        currentSpan.setAttribute("shippingMethod", request.getShippingMethod().name());


        try {
            logger.info("Calling third-party shipping provider");
            String trackingNumber = simulateThirdPartyShippingServiceCall(request);
            logger.info("Shipping request processed successfully with tracking number: {}", trackingNumber);

            return new ShippingResponse(trackingNumber);
        } catch (Exception e) {
            currentSpan.recordException(e);
            logger.error("Error processing shipping request", e);
            throw e;
        }
    }

    @WithSpan
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
