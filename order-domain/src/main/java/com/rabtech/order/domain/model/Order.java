package com.rabtech.order.domain.model;

import com.rabtech.order.domain.event.DomainEvent;
import com.rabtech.order.domain.event.OrderCancelled;
import com.rabtech.order.domain.event.OrderConfirmed;
import com.rabtech.order.domain.event.PaymentRecorded;
import com.rabtech.order.domain.port.TimePort;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Order {

    private final OrderId id;
    private OrderStatus status;
    private final List<OrderItem> items = new ArrayList<>();
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    public Order(OrderId id) {
        Objects.requireNonNull(id, "OrderId cannot be null");
        this.id = id;
        this.status = OrderStatus.DRAFT;
    }

    public Order(String id) {
        this(new OrderId(id));
    }

    public OrderId getId() {
        return id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearDomainEvents() {
        domainEvents.clear();
    }

    public void addItem(OrderItem item) {
        if (item == null) {
            throw new IllegalArgumentException("OrderItem cannot be null");
        }
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException("Items can only be added when order is in DRAFT status");
        }
        items.add(item);
    }

    public double getTotal() {
        double total = 0.0;
        for (OrderItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    public void confirm() {
        confirm(null);
    }

    public void confirm(TimePort timePort) {
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException("Order cannot be confirmed when in status: " + status);
        }
        if (items.isEmpty()) {
            throw new IllegalStateException("Order must contain at least one item before confirmation");
        }
        this.status = OrderStatus.CONFIRMED;
        Instant timestamp = timePort != null ? timePort.now() : Instant.now();
        domainEvents.add(new OrderConfirmed(id, timestamp));
    }

    public void pay() {
        pay(null);
    }

    public void pay(TimePort timePort) {
        if (status == OrderStatus.CANCELLED) {
            throw new IllegalStateException("A cancelled order cannot be paid");
        }
        if (status == OrderStatus.DRAFT) {
            throw new IllegalStateException("Order cannot be paid before confirmation");
        }
        if (status == OrderStatus.PAID) {
            throw new IllegalStateException("Order is already paid");
        }
        if (status != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Order cannot be paid when in status: " + status);
        }
        this.status = OrderStatus.PAID;
        Instant timestamp = timePort != null ? timePort.now() : Instant.now();
        domainEvents.add(new PaymentRecorded(id, getTotal(), timestamp));
    }

    public void cancel() {
        cancel("User requested cancellation", null);
    }

    public void cancel(TimePort timePort) {
        cancel("User requested cancellation", timePort);
    }

    public void cancel(String reason, TimePort timePort) {
        if (status == OrderStatus.PAID) {
            throw new IllegalStateException("Order cannot be cancelled after payment");
        }
        if (status == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order is already cancelled");
        }
        this.status = OrderStatus.CANCELLED;
        Instant timestamp = timePort != null ? timePort.now() : Instant.now();
        domainEvents.add(new OrderCancelled(id, reason != null ? reason : "Order cancelled", timestamp));
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
                "id=" + id +
                ", status=" + status +
                ", itemsCount=" + items.size() +
                ", total=" + getTotal() +
                '}';
    }
}
