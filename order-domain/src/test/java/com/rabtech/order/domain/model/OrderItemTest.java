package com.rabtech.order.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

    @Test
    @DisplayName("Should create valid OrderItem")
    void testValidOrderItem() {
        OrderItem item = new OrderItem("Keyboard", 49.99, 2);
        assertEquals("Keyboard", item.getProduct());
        assertEquals(49.99, item.getPrice(), 0.001);
        assertEquals(2, item.getQuantity());
        assertEquals(99.98, item.getSubtotal(), 0.001);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10})
    @DisplayName("Rule 2: Quantity must be a positive whole number")
    void testRule2QuantityMustBePositive(int quantity) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new OrderItem("Monitor", 199.99, quantity)
        );
        assertTrue(exception.getMessage().contains("Quantity must be positive"));
    }

    @Test
    @DisplayName("Price cannot be negative")
    void testNegativePriceThrowsException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new OrderItem("Monitor", -10.00, 1)
        );
    }

    @Test
    @DisplayName("Product name cannot be blank or null")
    void testBlankProductThrowsException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new OrderItem("   ", 10.00, 1)
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> new OrderItem(null, 10.00, 1)
        );
    }
}
