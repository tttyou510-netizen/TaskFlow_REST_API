package com.example.TaskFlow.API.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationManager {

    private static final Logger log = LoggerFactory.getLogger(NotificationManager.class);

    private final List<NotificationStrategy> notifications;

    public NotificationManager(List<NotificationStrategy> notifications) {
        this.notifications = notifications;
    }

    @org.springframework.scheduling.annotation.Async("notificationExecutor")
    public void notify(String channel, String recipient, String subject, String body) {
        NotificationStrategy strategy = notifications.stream()
                .filter(n -> n.supports(channel))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No notification strategy found for channel: " + channel));

        log.info("[Thread: {}] Dispatching notification via channel '{}' to '{}'", Thread.currentThread().getName(), channel, recipient);
        strategy.sendNotification(recipient, subject, body);
    }
}
