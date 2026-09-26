package com.rabtech.order.domain.port;

import com.rabtech.order.domain.model.Order;
import com.rabtech.order.domain.model.OrderId;
import java.util.Optional;

public interface OrderRepository {
    void save(Order order);
    Optional<Order> findById(OrderId orderId);

    default Optional<Order> findById(String orderId) {
        if (orderId == null || orderId.isBlank()) {
            return Optional.empty();
        }
        return findById(new OrderId(orderId));
    }
}
