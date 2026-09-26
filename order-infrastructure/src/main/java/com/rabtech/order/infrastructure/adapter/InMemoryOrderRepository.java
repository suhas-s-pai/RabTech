package com.rabtech.order.infrastructure.adapter;

import com.rabtech.order.domain.model.Order;
import com.rabtech.order.domain.model.OrderId;
import com.rabtech.order.domain.port.OrderRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryOrderRepository implements OrderRepository {
    private final Map<OrderId, Order> store = new ConcurrentHashMap<>();

    @Override
    public void save(Order order) {
        if (order == null || order.getId() == null) {
            throw new IllegalArgumentException("Cannot save null order or order with null ID");
        }
        store.put(order.getId(), order);
    }

    @Override
    public Optional<Order> findById(OrderId orderId) {
        if (orderId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(store.get(orderId));
    }

    public int count() {
        return store.size();
    }

    public void clear() {
        store.clear();
    }
}
