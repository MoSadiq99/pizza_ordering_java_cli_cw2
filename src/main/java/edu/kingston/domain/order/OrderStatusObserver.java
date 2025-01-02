package edu.kingston.domain.order;

import edu.kingston.service.NotificationService;

public interface OrderStatusObserver {
    void updateOrderStatus(Order order, OrderStatus status, NotificationService notificationService);
}
