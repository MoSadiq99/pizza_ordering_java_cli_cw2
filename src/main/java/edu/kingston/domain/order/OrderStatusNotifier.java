package edu.kingston.domain.order;

import edu.kingston.service.NotificationService;

public class OrderStatusNotifier implements OrderStatusObserver {
    private NotificationService notificationService;

    public OrderStatusNotifier(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void updateOrderStatus(Order order, OrderStatus status, NotificationService notificationService) {
        String message = String.format("Your order [%s] is now %s.", order.getOrderId(), status);
        notificationService.notifyUser(order.getCustomer().getUserId(), message);
    }
}
