package edu.kingston.repository;

import edu.kingston.domain.order.Order;
import edu.kingston.util.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class OrderRepository {

    public void addOrder(Order order) {
        String sql = "INSERT INTO orders (order_id, customer_id, order_time, order_type, payment_method_id, status, estimated_delivery_time, discount_amount, total_amount_after_discount) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, order.getOrderId());
            stmt.setString(2, order.getCustomer().getUserId());
            stmt.setTimestamp(3, java.sql.Timestamp.valueOf(order.getOrderTime()));
            stmt.setString(4, order.getOrderType().toString());
            stmt.setInt(5, order.getPaymentMethod().getId());
            stmt.setString(6, order.getStatus().toString());
            stmt.setTimestamp(7, java.sql.Timestamp.valueOf(order.getEstimatedDeliveryTime()));
            stmt.setDouble(8, order.getDiscountAmount());
            stmt.setDouble(9, order.getTotalAmountAfterDiscount());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
