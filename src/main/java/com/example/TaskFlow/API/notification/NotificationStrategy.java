package com.example.TaskFlow.API.notification;

public interface NotificationStrategy {
    void  sendNotification(String recipient, String subject, String body);
    boolean supports(String channel);
}
