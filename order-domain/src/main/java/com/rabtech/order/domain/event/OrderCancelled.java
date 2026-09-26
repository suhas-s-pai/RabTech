package com.rabtech.order.domain.event;

import com.rabtech.order.domain.model.OrderId;
import java.time.Instant;
import java.util.Objects;

public record OrderCancelled(OrderId orderId, String reason, Instant occurredAt) implements DomainEvent {
    public OrderCancelled {
        Objects.requireNonNull(orderId, "orderId cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
    }

    public OrderCancelled(OrderId orderId) {
        this(orderId, "Order cancelled", Instant.now());
    }

    public OrderCancelled(OrderId orderId, String reason) {
        this(orderId, reason, Instant.now());
    }
}
