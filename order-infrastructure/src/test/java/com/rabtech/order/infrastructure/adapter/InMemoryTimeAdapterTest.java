package com.rabtech.order.infrastructure.adapter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTimeAdapterTest {

    @Test
    @DisplayName("Should return fixed instant")
    void testFixedTime() {
        Instant expected = Instant.parse("2026-09-26T10:00:00Z");
        InMemoryTimeAdapter adapter = new InMemoryTimeAdapter(expected);

        assertEquals(expected, adapter.now());
    }
}
