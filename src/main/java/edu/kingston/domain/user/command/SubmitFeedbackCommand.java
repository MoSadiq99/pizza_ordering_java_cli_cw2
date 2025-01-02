package edu.kingston.domain.user.command;

import edu.kingston.domain.user.FeedbackSystem;
import edu.kingston.domain.order.Order;

public class SubmitFeedbackCommand implements Command {
    private FeedbackSystem feedbackSystem;
    private Order order;
    private int rating;
    private String comment;

    public SubmitFeedbackCommand(FeedbackSystem feedbackSystem, Order order, int rating, String comment) {
        this.feedbackSystem = feedbackSystem;
        this.order = order;
        this.rating = rating;
        this.comment = comment;
    }

    @Override
    public void execute() {
        feedbackSystem.submitFeedback(order, rating, comment);
    }

    @Override
    public void undo() {
        feedbackSystem.removeFeedback(order);
        System.out.println("Feedback for Order " + order.getOrderId() + " has been removed.");
    }

    @Override
    public String getDescription() {
        return "Submit Feedback for Order: " + order.getOrderId();
    }
}
