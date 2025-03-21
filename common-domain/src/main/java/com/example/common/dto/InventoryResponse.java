package com.example.common.dto;

public class InventoryResponse {
    private Integer quantity;

    public InventoryResponse() {
    }

    public InventoryResponse(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "InventoryResponse{" +
                "quantity=" + quantity +
                '}';
    }
}
