package com.rabtech.order.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderIdTest {

    @Test
    @DisplayName("Should create valid OrderId")
    void testValidOrderId() {
        OrderId id = new OrderId("ORD-999");
        assertEquals("ORD-999", id.getValue());
        assertEquals("ORD-999", id.toString());
        assertEquals(new OrderId("ORD-999"), id);
    }

    @Test
    @DisplayName("Null or blank OrderId should throw IllegalArgumentException")
    void testInvalidOrderId() {
        assertThrows(IllegalArgumentException.class, () -> new OrderId(null));
        assertThrows(IllegalArgumentException.class, () -> new OrderId("  "));
    }

    @Test
    @DisplayName("Should generate random UUID OrderId")
    void testGenerateOrderId() {
        OrderId id = OrderId.generate();
        assertNotNull(id);
        assertNotNull(id.getValue());
        assertFalse(id.getValue().isBlank());
    }
}
