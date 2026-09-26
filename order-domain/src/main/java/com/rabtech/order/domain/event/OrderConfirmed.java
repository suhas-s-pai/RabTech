package com.rabtech.order.domain.event;

import com.rabtech.order.domain.model.OrderId;
import java.time.Instant;
import java.util.Objects;

public record OrderConfirmed(OrderId orderId, Instant occurredAt) implements DomainEvent {
    public OrderConfirmed {
        Objects.requireNonNull(orderId, "orderId cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
    }

    public OrderConfirmed(OrderId orderId) {
        this(orderId, Instant.now());
    }
}
