package com.rabtech.order.domain.model;

import com.rabtech.order.domain.event.DomainEvent;
import com.rabtech.order.domain.event.OrderCancelled;
import com.rabtech.order.domain.event.OrderConfirmed;
import com.rabtech.order.domain.event.PaymentRecorded;
import com.rabtech.order.domain.port.TimePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    private Order order;
    private OrderItem item1;
    private OrderItem item2;
    private TimePort fixedTimePort;
    private Instant testTime;

    @BeforeEach
    void setUp() {
        order = new Order("ORD-100");
        item1 = new OrderItem("Laptop", 1200.00, 1);
        item2 = new OrderItem("Mouse", 25.00, 2);
        testTime = Instant.parse("2026-09-26T12:00:00Z");
        fixedTimePort = () -> testTime;
    }

    @Test
    @DisplayName("New order should start in DRAFT status")
    void testNewOrderInitialStatus() {
        assertEquals("ORD-100", order.getId().getValue());
        assertEquals(OrderStatus.DRAFT, order.getStatus());
        assertTrue(order.getItems().isEmpty());
        assertEquals(0.0, order.getTotal());
        assertTrue(order.getDomainEvents().isEmpty());
    }

    @Test
    @DisplayName("Should add items and calculate total correctly")
    void testAddItemsAndCalculateTotal() {
        order.addItem(item1);
        order.addItem(item2);

        assertEquals(2, order.getItems().size());
        assertEquals(1250.00, order.getTotal(), 0.001);
    }

    @Test
    @DisplayName("Rule 1: Confirmation fails when order contains no items")
    void testConfirmationFailsWhenEmpty() {
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> order.confirm()
        );
        assertTrue(exception.getMessage().contains("at least one item"));
        assertEquals(OrderStatus.DRAFT, order.getStatus());
    }

    @Test
    @DisplayName("Legal transition: DRAFT -> CONFIRMED with items")
    void testConfirmOrderWithItems() {
        order.addItem(item1);
        order.confirm(fixedTimePort);

        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
        List<DomainEvent> events = order.getDomainEvents();
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof OrderConfirmed);

        OrderConfirmed confirmedEvent = (OrderConfirmed) events.get(0);
        assertEquals(order.getId(), confirmedEvent.orderId());
        assertEquals(testTime, confirmedEvent.occurredAt());
    }

    @Test
    @DisplayName("Illegal transition: Cannot confirm already confirmed order")
    void testCannotConfirmTwice() {
        order.addItem(item1);
        order.confirm();

        assertThrows(IllegalStateException.class, () -> order.confirm());
    }

    @Test
    @DisplayName("Illegal transition: Cannot pay an order in DRAFT status")
    void testCannotPayDraftOrder() {
        order.addItem(item1);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> order.pay()
        );
        assertTrue(exception.getMessage().contains("before confirmation"));
        assertEquals(OrderStatus.DRAFT, order.getStatus());
    }

    @Test
    @DisplayName("Legal transition: CONFIRMED -> PAID")
    void testPayConfirmedOrder() {
        order.addItem(item1);
        order.confirm();
        order.pay(fixedTimePort);

        assertEquals(OrderStatus.PAID, order.getStatus());
        List<DomainEvent> events = order.getDomainEvents();
        assertEquals(2, events.size());
        assertTrue(events.get(1) instanceof PaymentRecorded);

        PaymentRecorded paymentEvent = (PaymentRecorded) events.get(1);
        assertEquals(order.getId(), paymentEvent.orderId());
        assertEquals(1200.00, paymentEvent.amount(), 0.001);
        assertEquals(testTime, paymentEvent.occurredAt());
    }

    @Test
    @DisplayName("Illegal transition: Cannot pay an already paid order")
    void testCannotPayAlreadyPaidOrder() {
        order.addItem(item1);
        order.confirm();
        order.pay();

        assertThrows(IllegalStateException.class, () -> order.pay());
    }

    @Test
    @DisplayName("Rule 3: A cancelled order cannot be paid")
    void testRule3CancelledOrderCannotBePaid() {
        order.addItem(item1);
        order.cancel(fixedTimePort);

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> order.pay()
        );
        assertEquals("A cancelled order cannot be paid", exception.getMessage());
    }

    @Test
    @DisplayName("Legal transition: Cancel order in DRAFT status")
    void testCancelDraftOrder() {
        order.cancel("Customer requested cancellation", fixedTimePort);

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        List<DomainEvent> events = order.getDomainEvents();
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof OrderCancelled);

        OrderCancelled cancelledEvent = (OrderCancelled) events.get(0);
        assertEquals(order.getId(), cancelledEvent.orderId());
        assertEquals("Customer requested cancellation", cancelledEvent.reason());
        assertEquals(testTime, cancelledEvent.occurredAt());
    }

    @Test
    @DisplayName("Legal transition: Cancel order in CONFIRMED status")
    void testCancelConfirmedOrder() {
        order.addItem(item1);
        order.confirm();
        order.cancel();

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    @DisplayName("Rule 4 & Illegal transition: Paid order cannot be cancelled")
    void testCannotCancelPaidOrder() {
        order.addItem(item1);
        order.confirm();
        order.pay();

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> order.cancel()
        );
        assertTrue(exception.getMessage().contains("after payment"));
        assertEquals(OrderStatus.PAID, order.getStatus());
    }

    @Test
    @DisplayName("Cannot add items after order is confirmed")
    void testCannotAddItemToConfirmedOrder() {
        order.addItem(item1);
        order.confirm();

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> order.addItem(item2)
        );
        assertTrue(exception.getMessage().contains("DRAFT status"));
    }

    @Test
    @DisplayName("Cannot add null item")
    void testCannotAddNullItem() {
        assertThrows(IllegalArgumentException.class, () -> order.addItem(null));
    }
}
