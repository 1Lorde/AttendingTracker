// created by Vlad Savchuk 24.11.2019 22:54
package com.savchuk.app.services;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

public class UiUtils {
    public static void showNotification(String text, NotificationVariant variant) {
        Notification notification = new Notification(text, 3000, Notification.Position.BOTTOM_END);
        notification.addThemeVariants(variant);
        notification.open();
    }
    public static void showCenterNotification(String text, NotificationVariant variant) {
        Notification notification = new Notification(text, 5000, Notification.Position.BOTTOM_CENTER);
        notification.addThemeVariants(variant);
        notification.open();
    }
}
