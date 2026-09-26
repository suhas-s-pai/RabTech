package com.rabtech.order.infrastructure.adapter;

import com.rabtech.order.domain.model.Order;
import com.rabtech.order.domain.model.OrderId;
import com.rabtech.order.domain.model.OrderItem;
import com.rabtech.order.domain.port.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryOrderRepositoryTest {

    private OrderRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryOrderRepository();
    }

    @Test
    @DisplayName("Should save and find order by OrderId")
    void testSaveAndFindOrder() {
        Order order = new Order("ORD-101");
        order.addItem(new OrderItem("Book", 15.0, 1));
        repository.save(order);

        Optional<Order> found = repository.findById(new OrderId("ORD-101"));
        assertTrue(found.isPresent());
        assertEquals("ORD-101", found.get().getId().getValue());
        assertEquals(15.0, found.get().getTotal(), 0.001);
    }

    @Test
    @DisplayName("Should find order by string id default overload")
    void testFindOrderByIdString() {
        Order order = new Order("ORD-102");
        repository.save(order);

        Optional<Order> found = repository.findById("ORD-102");
        assertTrue(found.isPresent());
    }

    @Test
    @DisplayName("Should return empty optional when order is not found")
    void testFindNonExistingOrder() {
        Optional<Order> found = repository.findById("ORD-NON-EXISTENT");
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Saving null order throws IllegalArgumentException")
    void testSaveNullOrderThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> repository.save(null));
    }
}
