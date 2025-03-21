package com.example.common.dto;

public class ShippingResponse {
    private String trackingNumber;

    public ShippingResponse() {
    }

    public ShippingResponse(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    @Override
    public String toString() {
        return "ShippingResponse{" +
                "trackingNumber='" + trackingNumber + '\'' +
                '}';
    }
}
