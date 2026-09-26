package com.rabtech.order.infrastructure.adapter;

import com.rabtech.order.domain.event.DomainEvent;
import com.rabtech.order.domain.port.NotificationPort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InMemoryNotificationAdapter implements NotificationPort {
    private final List<DomainEvent> publishedNotifications = new ArrayList<>();

    @Override
    public void sendNotification(DomainEvent event) {
        if (event != null) {
            publishedNotifications.add(event);
        }
    }

    public List<DomainEvent> getPublishedNotifications() {
        return Collections.unmodifiableList(publishedNotifications);
    }

    public void clear() {
        publishedNotifications.clear();
    }
}
