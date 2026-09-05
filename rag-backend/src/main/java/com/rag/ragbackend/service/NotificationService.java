package com.rag.ragbackend.service;

import com.rag.ragbackend.entity.Notification;

import java.util.List;

public interface NotificationService {

    Notification createNotification(Long userId, String type, String title, String message);

    List<Notification> getNotificationsByUserId(Long userId);

    List<Notification> getUnreadNotificationsByUserId(Long userId);

    Notification markAsRead(Long notificationId);
}