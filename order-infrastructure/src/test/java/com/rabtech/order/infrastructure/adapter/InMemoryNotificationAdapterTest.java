package com.rabtech.order.infrastructure.adapter;

import com.rabtech.order.domain.event.OrderConfirmed;
import com.rabtech.order.domain.model.OrderId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryNotificationAdapterTest {

    @Test
    @DisplayName("Should collect sent notifications")
    void testSendNotification() {
        InMemoryNotificationAdapter adapter = new InMemoryNotificationAdapter();
        OrderConfirmed event = new OrderConfirmed(new OrderId("ORD-1"), Instant.now());

        adapter.sendNotification(event);

        assertEquals(1, adapter.getPublishedNotifications().size());
        assertEquals(event, adapter.getPublishedNotifications().get(0));
    }
}
