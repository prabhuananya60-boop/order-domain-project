package com.example.order.domain.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public final class OrderConfirmed implements DomainEvent {
    private final String orderId;
    private final BigDecimal totalAmount;
    private final Instant occurredOn;

    public OrderConfirmed(String orderId, BigDecimal totalAmount, Instant occurredOn) {
        this.orderId = Objects.requireNonNull(orderId, "orderId must not be null");
        this.totalAmount = Objects.requireNonNull(totalAmount, "totalAmount must not be null");
        this.occurredOn = Objects.requireNonNull(occurredOn, "occurredOn must not be null");
    }

    public OrderConfirmed(String orderId, BigDecimal totalAmount) {
        this(orderId, totalAmount, Instant.now());
    }

    public String getOrderId() {
        return orderId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    @Override
    public Instant occurredOn() {
        return occurredOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderConfirmed that = (OrderConfirmed) o;
        return Objects.equals(orderId, that.orderId) &&
               Objects.equals(totalAmount, that.totalAmount) &&
               Objects.equals(occurredOn, that.occurredOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, totalAmount, occurredOn);
    }

    @Override
    public String toString() {
        return "OrderConfirmed{" +
                "orderId='" + orderId + '\'' +
                ", totalAmount=" + totalAmount +
                ", occurredOn=" + occurredOn +
                '}';
    }
}
