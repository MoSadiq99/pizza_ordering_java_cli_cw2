package edu.kingston.domain.payment;

import edu.kingston.domain.order.Order;

public class LoyaltyProgram implements PaymentStrategy {
    private final int id;
    private int points;
    private int tierId;
    private static final int[] TIER_THRESHOLDS = {0, 100, 500, 1000};
    private static final double[] TIER_DISCOUNTS = {0.0, 0.1, 0.2, 0.3}; // Discount rates for tiers

    // Constructors
    public LoyaltyProgram() {
        this.id = 0;
        this.points = 0;
        this.tierId = 0;
    }

    // Accumulate loyalty points based on order total
    public void accumulatePoints(double orderTotal) {
        points += (int) (orderTotal * 10);
        updateTier();
    }

    // Calculate discount based on tier and available points
    public double calculateDiscount(double orderTotal) {
        double discountRate = TIER_DISCOUNTS[tierId];
        double potentialDiscount = orderTotal * discountRate;

        int pointsRequired = (int) (potentialDiscount * 100);
        double actualDiscount = (points >= pointsRequired)
                ? potentialDiscount
                : points / 100.0;

        points -= (int) (actualDiscount * 100);
        updateTier();
        return actualDiscount;
    }

    // Process payment using loyalty points
    @Override
    public boolean processPayment(Order order) {
        double discount = calculateDiscount(order.calculateTotal());
        order.setTotalAmountAfterDiscount(order.calculateTotal() - discount);
        return true; // Payment successful
    }

    // Redeem a specified number of points for a discount
    public double redeemPointsForDiscount(double orderTotal, int pointsToRedeem) {
        if (points < pointsToRedeem || pointsToRedeem <= 50) {
            throw new IllegalArgumentException("Not enough loyalty points to redeem the discount.");
        }
        double discount = pointsToRedeem / 100.0; // 100 points = $1
        points -= pointsToRedeem;
        updateTier();
        return discount;
    }

    // Update the user's tier based on their current points
    private void updateTier() {
        for (int i = TIER_THRESHOLDS.length - 1; i >= 0; i--) {
            if (points >= TIER_THRESHOLDS[i]) {
                tierId = i;
                break;
            }
        }
    }

    // Getters
    @Override
    public int getId() {
        return this.id;
    }

    public int getPoints() {
        return points;
    }
}
