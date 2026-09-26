package com.rabtech.order.infrastructure.adapter;

import com.rabtech.order.domain.port.TimePort;

import java.time.Instant;

public class InMemoryTimeAdapter implements TimePort {
    private Instant fixedInstant;

    public InMemoryTimeAdapter() {
        this.fixedInstant = Instant.now();
    }

    public InMemoryTimeAdapter(Instant fixedInstant) {
        this.fixedInstant = fixedInstant;
    }

    @Override
    public Instant now() {
        return fixedInstant;
    }

    public void setFixedInstant(Instant fixedInstant) {
        this.fixedInstant = fixedInstant;
    }
}
