package com.example.common.dto;

import com.example.common.model.ShippingMethod;

public class ShippingRequest {
    private Long productId;
    private Integer quantity;
    private ShippingMethod shippingMethod;

    public ShippingRequest() {
    }

    public ShippingRequest(Long productId, Integer quantity, ShippingMethod shippingMethod) {
        this.productId = productId;
        this.quantity = quantity;
        this.shippingMethod = shippingMethod;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public ShippingMethod getShippingMethod() {
        return shippingMethod;
    }

    public void setShippingMethod(ShippingMethod shippingMethod) {
        this.shippingMethod = shippingMethod;
    }

    @Override
    public String toString() {
        return "ShippingRequest{" +
                "productId=" + productId +
                ", quantity=" + quantity +
                ", shippingMethod=" + shippingMethod +
                '}';
    }
}
