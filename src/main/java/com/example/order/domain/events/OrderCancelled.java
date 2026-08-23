package com.example.order.domain.events;

import java.time.Instant;
import java.util.Objects;

public final class OrderCancelled implements DomainEvent {
    private final String orderId;
    private final String reason;
    private final Instant occurredOn;

    public OrderCancelled(String orderId, String reason, Instant occurredOn) {
        this.orderId = Objects.requireNonNull(orderId, "orderId must not be null");
        this.reason = reason;
        this.occurredOn = Objects.requireNonNull(occurredOn, "occurredOn must not be null");
    }

    public OrderCancelled(String orderId, String reason) {
        this(orderId, reason, Instant.now());
    }

    public String getOrderId() {
        return orderId;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public Instant occurredOn() {
        return occurredOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderCancelled that = (OrderCancelled) o;
        return Objects.equals(orderId, that.orderId) &&
               Objects.equals(reason, that.reason) &&
               Objects.equals(occurredOn, that.occurredOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, reason, occurredOn);
    }

    @Override
    public String toString() {
        return "OrderCancelled{" +
                "orderId='" + orderId + '\'' +
                ", reason='" + reason + '\'' +
                ", occurredOn=" + occurredOn +
                '}';
    }
}
