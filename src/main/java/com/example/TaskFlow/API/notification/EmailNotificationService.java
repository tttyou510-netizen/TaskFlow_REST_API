package com.example.TaskFlow.API.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService implements NotificationStrategy {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);

    @Override
    public void sendNotification(String recipient, String subject, String body) {
        log.info("[Thread: {}] Sending Email to {}: Subject: {}, Body: {}", Thread.currentThread().getName(), recipient, subject, body);
    }

    @Override
    public boolean supports(String channel) {
        return "EMAIL".equalsIgnoreCase(channel);
    }
}
