package com.rabtech.order.domain.event;

import com.rabtech.order.domain.model.OrderId;
import java.time.Instant;
import java.util.Objects;

public record PaymentRecorded(OrderId orderId, double amount, Instant occurredAt) implements DomainEvent {
    public PaymentRecorded {
        Objects.requireNonNull(orderId, "orderId cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
    }

    public PaymentRecorded(OrderId orderId, double amount) {
        this(orderId, amount, Instant.now());
    }
}
