package com.phunware.locationmessaging.sample;

import android.app.Notification;
import android.media.RingtoneManager;

import com.phunware.locationmessaging.messages.services.NotificationCustomizationService;

public class EditNotificationService extends NotificationCustomizationService {

    @Override
    public void editNotification(Notification notification) {
        // Use the default notification sound for all notifications
        notification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    }

}
