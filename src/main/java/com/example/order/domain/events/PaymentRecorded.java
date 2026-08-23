package com.example.order.domain.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public final class PaymentRecorded implements DomainEvent {
    private final String orderId;
    private final BigDecimal amount;
    private final Instant occurredOn;

    public PaymentRecorded(String orderId, BigDecimal amount, Instant occurredOn) {
        this.orderId = Objects.requireNonNull(orderId, "orderId must not be null");
        this.amount = Objects.requireNonNull(amount, "amount must not be null");
        this.occurredOn = Objects.requireNonNull(occurredOn, "occurredOn must not be null");
    }

    public PaymentRecorded(String orderId, BigDecimal amount) {
        this(orderId, amount, Instant.now());
    }

    public String getOrderId() {
        return orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public Instant occurredOn() {
        return occurredOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentRecorded that = (PaymentRecorded) o;
        return Objects.equals(orderId, that.orderId) &&
               Objects.equals(amount, that.amount) &&
               Objects.equals(occurredOn, that.occurredOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, amount, occurredOn);
    }

    @Override
    public String toString() {
        return "PaymentRecorded{" +
                "orderId='" + orderId + '\'' +
                ", amount=" + amount +
                ", occurredOn=" + occurredOn +
                '}';
    }
}
