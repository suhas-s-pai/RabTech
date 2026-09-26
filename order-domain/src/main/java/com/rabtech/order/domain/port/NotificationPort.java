package com.rabtech.order.domain.port;

import com.rabtech.order.domain.event.DomainEvent;

public interface NotificationPort {
    void sendNotification(DomainEvent event);
}
