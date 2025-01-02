package edu.kingston.domain.payment;

import edu.kingston.domain.order.Order;
import edu.kingston.domain.user.User;

public class DigitalWalletPayment implements PaymentStrategy {
    private final int id;
    private String walletId;

    public DigitalWalletPayment(int id, String walletId) {
        this.id = id;
        this.walletId = walletId;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public boolean processPayment(Order order) {
        // Simulated digital wallet payment
        System.out.println("Processing digital wallet payment of $" + order.calculateTotal());
        return true;
    }

    public double calculateDiscount(User user) {
        // Higher discount for digital wallet users
        return user.getLoyaltyProgram().getPoints() > 200 ? 0.15 : 0;
    }

}