package edu.kingston.service;

public class NotificationService {
    public void notifyUser(String userName, String message) {
        System.out.printf("Notification to User [%s]: %s%n", userName, message);
    }
}
