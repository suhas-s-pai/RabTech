package com.rabtech.order.infrastructure;

import com.rabtech.order.domain.event.DomainEvent;
import com.rabtech.order.domain.model.Order;
import com.rabtech.order.domain.model.OrderId;
import com.rabtech.order.domain.model.OrderItem;
import com.rabtech.order.domain.port.NotificationPort;
import com.rabtech.order.domain.port.OrderRepository;
import com.rabtech.order.domain.port.TimePort;
import com.rabtech.order.infrastructure.adapter.InMemoryNotificationAdapter;
import com.rabtech.order.infrastructure.adapter.InMemoryOrderRepository;
import com.rabtech.order.infrastructure.adapter.InMemoryTimeAdapter;

import java.time.Instant;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  RabTech Order Management System - Task 02 Demo  ");
        System.out.println("==================================================");

        // Initialize In-Memory Adapters (Hexagonal Architecture Ports)
        OrderRepository repository = new InMemoryOrderRepository();
        InMemoryNotificationAdapter notificationAdapter = new InMemoryNotificationAdapter();
        NotificationPort notificationPort = notificationAdapter;
        TimePort timePort = new InMemoryTimeAdapter(Instant.now());

        // 1. Create a new Order
        Order order = new Order("ORD-001");
        System.out.println("\n[1] Created Order: " + order.getId() + " | Initial Status: " + order.getStatus());

        // Attempting to confirm an empty order (Demonstrating Business Rule 1)
        try {
            System.out.println("\n[2] Attempting to confirm order with 0 items...");
            order.confirm(timePort);
        } catch (IllegalStateException e) {
            System.out.println("    Caught Expected Error (Rule 1): " + e.getMessage());
        }

        // 2. Add Items to the Order (Demonstrating Business Rule 2 & 5)
        OrderItem laptop = new OrderItem("Laptop", 50000.0, 2);
        OrderItem mouse = new OrderItem("Wireless Mouse", 1500.0, 1);

        order.addItem(laptop);
        order.addItem(mouse);
        System.out.println("\n[3] Added line items:");
        System.out.println("    - " + laptop.getProduct() + " x " + laptop.getQuantity() + " @ $" + laptop.getPrice());
        System.out.println("    - " + mouse.getProduct() + " x " + mouse.getQuantity() + " @ $" + mouse.getPrice());
        System.out.println("    Derived Total Price: $" + order.getTotal());

        // 3. Confirm the Order
        order.confirm(timePort);
        System.out.println("\n[4] Confirmed Order | Current Status: " + order.getStatus());

        // Publish events via NotificationPort
        for (DomainEvent event : order.getDomainEvents()) {
            notificationPort.sendNotification(event);
        }
        order.clearDomainEvents();

        // Save order to repository
        repository.save(order);
        System.out.println("    Saved Order " + order.getId() + " to InMemoryOrderRepository.");

        // 4. Record Payment
        order.pay(timePort);
        System.out.println("\n[5] Recorded Payment | Current Status: " + order.getStatus());

        for (DomainEvent event : order.getDomainEvents()) {
            notificationPort.sendNotification(event);
        }
        order.clearDomainEvents();

        // 5. Attempt Illegal State Transitions
        System.out.println("\n[6] Testing Illegal State Transitions:");

        // Attempting to cancel a paid order (Rule 4 / Illegal transition)
        try {
            order.cancel(timePort);
        } catch (IllegalStateException e) {
            System.out.println("    - Cannot cancel paid order: " + e.getMessage());
        }

        // 6. Test Rule 3: Cancelled order cannot be paid
        Order order2 = new Order("ORD-002");
        order2.addItem(new OrderItem("Keyboard", 80.0, 1));
        order2.cancel(timePort);
        System.out.println("\n[7] Created Order ORD-002 and Cancelled it immediately | Status: " + order2.getStatus());

        try {
            order2.pay(timePort);
        } catch (IllegalStateException e) {
            System.out.println("    - Caught Expected Error (Rule 3): " + e.getMessage());
        }

        // 7. Verify Persistence Port
        System.out.println("\n[8] Verifying Order Persistence:");
        Optional<Order> retrieved = repository.findById(new OrderId("ORD-001"));
        retrieved.ifPresent(o -> System.out.println("    Retrieved from Repo: ID=" + o.getId() + ", Status=" + o.getStatus() + ", Total=$" + o.getTotal()));

        // 8. Verify Notification Port
        System.out.println("\n[9] Published Domain Events Log:");
        for (DomainEvent notification : notificationAdapter.getPublishedNotifications()) {
            System.out.println("    - Event: " + notification.getClass().getSimpleName() + " at " + notification.occurredAt());
        }

        System.out.println("\n==================================================");
        System.out.println("  Task 02 Domain & Hexagonal Architecture Verified!");
        System.out.println("==================================================");
    }
}
