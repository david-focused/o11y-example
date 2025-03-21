package com.example.order_service.model;

import com.example.common.model.ShippingMethod;

public class Order {
    private Long productId;
    private Integer quantity;
    private ShippingMethod shippingMethod;

    public Order() {
    }

    public Order(Long productId, Integer quantity, ShippingMethod shippingMethod) {
        this.productId = productId;
        this.quantity = quantity;
        this.shippingMethod = shippingMethod;
    }
    
    public Order(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
        this.shippingMethod = ShippingMethod.GROUND; // Default to ground shipping
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
        return "Order{" +
                "productId=" + productId +
                ", quantity=" + quantity +
                ", shippingMethod=" + shippingMethod +
                '}';
    }
}
