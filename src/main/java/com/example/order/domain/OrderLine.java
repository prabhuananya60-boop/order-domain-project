package com.example.order.domain;

import java.math.BigDecimal;
import java.util.Objects;

public final class OrderLine {
    private final String productId;
    private final BigDecimal unitPrice;
    private final int quantity;

    public OrderLine(String productId, BigDecimal unitPrice, int quantity) {
        this.productId = Objects.requireNonNull(productId, "productId must not be null");
        this.unitPrice = Objects.requireNonNull(unitPrice, "unitPrice must not be null");
        if (unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("unitPrice must not be negative");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be a positive whole number");
        }
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getTotalPrice() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderLine orderLine = (OrderLine) o;
        return quantity == orderLine.quantity &&
               Objects.equals(productId, orderLine.productId) &&
               Objects.equals(unitPrice, orderLine.unitPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, unitPrice, quantity);
    }

    @Override
    public String toString() {
        return "OrderLine{" +
                "productId='" + productId + '\'' +
                ", unitPrice=" + unitPrice +
                ", quantity=" + quantity +
                '}';
    }
}
