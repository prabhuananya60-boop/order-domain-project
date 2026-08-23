package com.example.order.domain;

import com.example.order.domain.events.DomainEvent;
import com.example.order.domain.events.OrderCancelled;
import com.example.order.domain.events.OrderConfirmed;
import com.example.order.domain.events.PaymentRecorded;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Order {
    private final String id;
    private OrderStatus status;
    private final List<OrderLine> lines = new ArrayList<>();
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    public Order(String id) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.status = OrderStatus.DRAFT;
    }

    public String getId() {
        return id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderLine> getLines() {
        return Collections.unmodifiableList(lines);
    }

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearDomainEvents() {
        this.domainEvents.clear();
    }

    public void addLine(OrderLine line) {
        Objects.requireNonNull(line, "line must not be null");
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException("Cannot add line when order is not in DRAFT state");
        }
        lines.add(line);
    }

    public void addLine(String productId, BigDecimal unitPrice, int quantity) {
        addLine(new OrderLine(productId, unitPrice, quantity));
    }

    public BigDecimal getTotal() {
        return lines.stream()
                .map(OrderLine::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void confirm() {
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException("Order must be in DRAFT state to be confirmed");
        }
        if (lines.isEmpty()) {
            throw new IllegalStateException("An order must contain at least one line before confirmation.");
        }
        this.status = OrderStatus.CONFIRMED;
        this.domainEvents.add(new OrderConfirmed(id, getTotal()));
    }

    public void recordPayment(BigDecimal amount) {
        Objects.requireNonNull(amount, "amount must not be null");
        if (status == OrderStatus.CANCELLED) {
            throw new IllegalStateException("A cancelled order cannot be paid.");
        }
        if (status != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Order must be in CONFIRMED state to record payment");
        }
        this.status = OrderStatus.PAID;
        this.domainEvents.add(new PaymentRecorded(id, amount));
    }

    public void recordPayment() {
        recordPayment(getTotal());
    }

    public void cancel(String reason) {
        if (status == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order is already cancelled");
        }
        this.status = OrderStatus.CANCELLED;
        this.domainEvents.add(new OrderCancelled(id, reason));
    }

    public void cancel() {
        cancel("Order cancelled");
    }

    public void returnToDraft() {
        if (status == OrderStatus.PAID) {
            throw new IllegalStateException("A paid order cannot return to draft.");
        }
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException("Order cannot return to draft from current state: " + status);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", status=" + status +
                ", lines=" + lines.size() +
                ", total=" + getTotal() +
                '}';
    }
}
