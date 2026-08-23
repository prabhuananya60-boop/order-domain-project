package com.example.order.domain;

import com.example.order.domain.events.DomainEvent;
import com.example.order.domain.events.OrderCancelled;
import com.example.order.domain.events.OrderConfirmed;
import com.example.order.domain.events.PaymentRecorded;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderDomainTest {

    @Test
    @DisplayName("New order should start in DRAFT state with empty lines and events")
    void testNewOrderInitialState() {
        Order order = new Order("ORD-001");
        assertEquals("ORD-001", order.getId());
        assertEquals(OrderStatus.DRAFT, order.getStatus());
        assertTrue(order.getLines().isEmpty());
        assertTrue(order.getDomainEvents().isEmpty());
        assertEquals(BigDecimal.ZERO, order.getTotal());
    }

    @Test
    @DisplayName("Quantity must be a positive whole number")
    void testQuantityMustBePositiveWholeNumber() {
        // Valid positive quantities
        assertDoesNotThrow(() -> new OrderLine("P1", new BigDecimal("10.00"), 1));
        assertDoesNotThrow(() -> new OrderLine("P1", new BigDecimal("10.00"), 100));

        // Invalid quantities (0 or negative)
        IllegalArgumentException zeroEx = assertThrows(IllegalArgumentException.class,
                () -> new OrderLine("P1", new BigDecimal("10.00"), 0));
        assertTrue(zeroEx.getMessage().contains("positive whole number"));

        IllegalArgumentException negEx = assertThrows(IllegalArgumentException.class,
                () -> new OrderLine("P1", new BigDecimal("10.00"), -5));
        assertTrue(negEx.getMessage().contains("positive whole number"));
    }

    @Test
    @DisplayName("An order must contain at least one line before confirmation")
    void testCannotConfirmEmptyOrder() {
        Order order = new Order("ORD-002");
        IllegalStateException ex = assertThrows(IllegalStateException.class, order::confirm);
        assertTrue(ex.getMessage().contains("at least one line"));
        assertEquals(OrderStatus.DRAFT, order.getStatus());
        assertTrue(order.getDomainEvents().isEmpty());
    }

    @Test
    @DisplayName("Confirmation succeeds when order has at least one line and emits OrderConfirmed event")
    void testConfirmOrderWithLines() {
        Order order = new Order("ORD-003");
        order.addLine("PROD-A", new BigDecimal("25.00"), 2);
        order.addLine("PROD-B", new BigDecimal("15.50"), 1);

        order.confirm();

        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
        List<DomainEvent> events = order.getDomainEvents();
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof OrderConfirmed);

        OrderConfirmed confirmedEvent = (OrderConfirmed) events.get(0);
        assertEquals("ORD-003", confirmedEvent.getOrderId());
        assertEquals(new BigDecimal("65.50"), confirmedEvent.getTotalAmount());
        assertNotNull(confirmedEvent.occurredOn());
    }

    @Test
    @DisplayName("A cancelled order cannot be paid")
    void testCancelledOrderCannotBePaid() {
        Order order = new Order("ORD-004");
        order.addLine("PROD-A", new BigDecimal("50.00"), 1);
        order.cancel("Customer changed mind");

        assertEquals(OrderStatus.CANCELLED, order.getStatus());

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> order.recordPayment(new BigDecimal("50.00")));
        assertTrue(ex.getMessage().contains("A cancelled order cannot be paid"));
    }

    @Test
    @DisplayName("A paid order cannot return to draft")
    void testPaidOrderCannotReturnToDraft() {
        Order order = new Order("ORD-005");
        order.addLine("PROD-A", new BigDecimal("100.00"), 1);
        order.confirm();
        order.recordPayment();

        assertEquals(OrderStatus.PAID, order.getStatus());

        IllegalStateException ex = assertThrows(IllegalStateException.class, order::returnToDraft);
        assertTrue(ex.getMessage().contains("A paid order cannot return to draft"));

        // Attempting to add lines to a PAID order should also be forbidden
        assertThrows(IllegalStateException.class, () -> order.addLine("PROD-B", new BigDecimal("10.00"), 1));
    }

    @Test
    @DisplayName("Order total must be derived from immutable line prices and quantities")
    void testOrderTotalCalculation() {
        Order order = new Order("ORD-006");
        // Line 1: 3 * 19.99 = 59.97
        OrderLine line1 = new OrderLine("P1", new BigDecimal("19.99"), 3);
        // Line 2: 2 * 5.00 = 10.00
        OrderLine line2 = new OrderLine("P2", new BigDecimal("5.00"), 2);

        assertEquals(new BigDecimal("59.97"), line1.getTotalPrice());
        assertEquals(new BigDecimal("10.00"), line2.getTotalPrice());

        order.addLine(line1);
        order.addLine(line2);

        assertEquals(new BigDecimal("69.97"), order.getTotal());
    }

    @Test
    @DisplayName("Payment recording transitions order to PAID and emits PaymentRecorded event")
    void testPaymentRecordedEvent() {
        Order order = new Order("ORD-007");
        order.addLine("PROD-A", new BigDecimal("40.00"), 2);
        order.confirm();
        order.clearDomainEvents(); // Clear confirmation event to isolate payment event test

        order.recordPayment(new BigDecimal("80.00"));

        assertEquals(OrderStatus.PAID, order.getStatus());
        List<DomainEvent> events = order.getDomainEvents();
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof PaymentRecorded);

        PaymentRecorded paymentEvent = (PaymentRecorded) events.get(0);
        assertEquals("ORD-007", paymentEvent.getOrderId());
        assertEquals(new BigDecimal("80.00"), paymentEvent.getAmount());
    }

    @Test
    @DisplayName("Order cancellation emits OrderCancelled event")
    void testOrderCancelledEvent() {
        Order order = new Order("ORD-008");
        order.addLine("PROD-A", new BigDecimal("30.00"), 1);

        order.cancel("Out of stock");

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        List<DomainEvent> events = order.getDomainEvents();
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof OrderCancelled);

        OrderCancelled cancelledEvent = (OrderCancelled) events.get(0);
        assertEquals("ORD-008", cancelledEvent.getOrderId());
        assertEquals("Out of stock", cancelledEvent.getReason());
    }
}
