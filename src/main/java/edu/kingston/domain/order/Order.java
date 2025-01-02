package edu.kingston.domain.order;

import edu.kingston.domain.payment.PaymentStrategy;
import edu.kingston.domain.pizza.Pizza;
import edu.kingston.domain.user.User;
import edu.kingston.service.NotificationService;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class Order {
    private final String orderId;
    private final User customer;
    private final List<Pizza> pizzas;
    private final LocalDateTime orderTime;
    private final OrderType orderType;
    private PaymentStrategy paymentMethod;
    private OrderStatus status;
    private List<OrderStatusObserver> observers = new CopyOnWriteArrayList<>();
    private LocalDateTime estimatedDeliveryTime;
    private double discountAmount;
    private double totalAmountAfterDiscount;
    private Map<OrderStatus, LocalDateTime> statusTimestamps = new EnumMap<>(OrderStatus.class);
    private NotificationService notificationService;

    private Order(OrderBuilder builder) {
        this.orderId = generateID();
        this.customer = builder.customer;
        this.pizzas = builder.pizzas;
        this.orderTime = LocalDateTime.now();
        this.orderType = builder.orderType;
        this.paymentMethod = builder.paymentMethod;
        this.status = OrderStatus.PLACED;
        statusTimestamps.put(this.status, LocalDateTime.now());
    }

    //? Generate ID
    String baseID = "O";
    int count = 1;
    public String generateID() {
        return String.format("%s-%04d", baseID, count++);
    }

    //? Calculate Total
    public double calculateTotal() {
        return pizzas.stream()
                .mapToDouble(Pizza::calculateTotalPrice)
                .sum();
    }

    //? Notify Observers
    private void notifyObservers() {
        for (OrderStatusObserver observer : observers) {
            try {
                observer.updateOrderStatus(this, status, notificationService);
            } catch (Exception e) {
                System.err.println("Failed to notify observer: " + e.getMessage());
            }
        }
    }

    public void setPaymentMethod(PaymentStrategy paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    //? Builder for Order
    public static class OrderBuilder {
        private User customer;
        private List<Pizza> pizzas;
        private OrderType orderType;
        private PaymentStrategy paymentMethod;

        public OrderBuilder customer(User customer) {
            this.customer = customer;
            return this;
        }

        public OrderBuilder pizzas(List<Pizza> pizzas) {
            this.pizzas = pizzas;
            return this;
        }

        public OrderBuilder orderType(OrderType orderType) {
            this.orderType = orderType;
            return this;
        }

        public OrderBuilder paymentMethod(PaymentStrategy paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public Order build() {
            validate();
            return new Order(this);
        }

        private void validate() {
            if (customer == null) {
                throw new IllegalStateException("Customer must be specified");
            }
            if (pizzas == null || pizzas.isEmpty()) {
                throw new IllegalStateException("At least one pizza must be included in the order");
            }
            if (orderType == null) {
                throw new IllegalStateException("Order type must be specified");
            }
        }
    }

    // Enum for Order Types
    public enum OrderType {
        PICKUP, DELIVERY
    }

    //! Getters and Setters
    public String getOrderId() { return orderId; }
    public User getCustomer() { return customer; }
    public List<Pizza> getPizzas() { return pizzas; }
    public LocalDateTime getOrderTime() { return orderTime; }
    public OrderType getOrderType() { return orderType; }
    public PaymentStrategy getPaymentMethod() { return paymentMethod; }
    public double getDiscountAmount() { return discountAmount; }
    public double getTotalAmountAfterDiscount() { return totalAmountAfterDiscount; }

    public void setDiscountAmount(double discountAmount) { this.discountAmount = discountAmount; }
    public void setTotalAmountAfterDiscount(double totalAmountAfterDiscount) {
        this.totalAmountAfterDiscount = totalAmountAfterDiscount;
    }

    //? Order Status
    public void setStatus(OrderStatus status) {
        this.status = status;
        statusTimestamps.put(status, LocalDateTime.now());
        notifyObservers();
    }

    public OrderStatus getStatus() { return status; }

    public LocalDateTime getStatusTimestamp(OrderStatus status) {
        return statusTimestamps.get(status);
    }

    //? Observers
    public void addObserver(OrderStatusObserver observer) {
        observers.add(observer);
    }
    public void removeObserver(OrderStatusObserver observer) {
        observers.remove(observer);
    }

    //? Estimated Delivery Time
    public LocalDateTime getEstimatedDeliveryTime() {
        return estimatedDeliveryTime;
    }


    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void setEstimatedDeliveryTime(LocalDateTime estimatedDeliveryTime) { this.estimatedDeliveryTime = estimatedDeliveryTime; }
}