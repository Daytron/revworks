/*
 * Copyright 2015 Ryan Gilera.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.daytron.revworks.util;

import com.github.daytron.revworks.data.FontAwesomeIcon;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.ui.Notification;

/**
 * Utility class for formatted notifications. These notifications last for few 
 * seconds as soon the user triggers mouse movement. Other notification like 
 * information notification can be closed by clicking the balloon.
 * 
 * <p>
 * These notifications are different from the user notifications which can be 
 * found in the notification pop window.
 *
 * @author Ryan Gilera
 */
@SuppressWarnings("serial")
public final class NotificationUtil {

    private NotificationUtil() {
    }

    /**
     * Creates an error notification that takes a single argument for caption.
     *
     * @param caption The main message of the notification
     */
    public static void showError(String caption) {
        showError(caption, "");
    }

    /**
     * Creates an error notification that takes two arguments for caption and time
     * delay.
     *
     * @param caption The main message of the notification
     * @param delay The time delay in milliseconds
     */
    public static void showError(String caption, int delay) {
        showError(caption, "", delay);
    }

    /**
     * Creates an error notification that takes two arguments for caption and
     * description.
     *
     * @param caption The main message of the notification
     * @param description Secondary description message
     */
    public static void showError(String caption, String description) {
        showError(caption, description, 0);
    }

    /**
     * Creates an error notification that takes three arguments for caption,
     * description and time delay.
     *
     * @param caption The main message of the notification
     * @param description Secondary description message
     * @param delay The time delay in milliseconds
     */
    public static void showError(String caption, String description, int delay) {
        Notification errorNotification;

        caption = "<i class=\"fa fa-exclamation-circle fa-2x\"></i>&nbsp;&nbsp;"
                + caption;
        if (description.isEmpty()) {
            errorNotification = new Notification(caption,
                    Notification.Type.ERROR_MESSAGE);
        } else {
            errorNotification = new Notification(caption, description,
                    Notification.Type.ERROR_MESSAGE);
        }

        errorNotification.setHtmlContentAllowed(true);
        errorNotification.setPosition(Position.TOP_CENTER);

        if (delay != 0) {
            errorNotification.setDelayMsec(delay);
        }

        errorNotification.show(Page.getCurrent());
    }

    /**
     * Creates an information notification that takes two arguments for title 
     * and message.
     * 
     * @param title the title of the message
     * @param message the message itself
     */
    public static void showInformation(String title, String message) {
        showInformation("", title, message);
    }

    /**
     * Creates an information notification that takes three arguments for icon, 
     * title and message.
     * 
     * @param fontAwesomeIcon an icon from FontAwesomeIcon
     * @param title the title of the message
     * @param message the message itself 
     */
    public static void showInformation(String fontAwesomeIcon, String title,
            String message) {
        if (!fontAwesomeIcon.isEmpty()) {
            title = fontAwesomeIcon + title;
        }

        Notification newNotification;
        if (message == null || message.isEmpty()) {
            newNotification = new Notification(title,
                    Notification.Type.TRAY_NOTIFICATION);
        } else {
            newNotification = new Notification(title, message,
                    Notification.Type.TRAY_NOTIFICATION);
        }

        newNotification.setHtmlContentAllowed(true);
        newNotification.setStyleName("custom-tray-notification-info");
        newNotification.setDelayMsec(3000);
        newNotification.setPosition(Position.TOP_RIGHT);
        newNotification.show(Page.getCurrent());
    }

    /**
     * Creates a warning notification that takes two arguments for title and 
     * message.
     * 
     * @param title the title of the message
     * @param message the message itself 
     */
    public static void showWarning(String title, String message) {
        
        title = FontAwesomeIcon.EXCLAMATION_CIRCLE.get2xSize() + title;
       
        Notification newNotification;
        
        if (message == null || message.isEmpty()) {
            newNotification = new Notification(title,
                    Notification.Type.TRAY_NOTIFICATION);
        } else {
            newNotification = new Notification(title, message,
                    Notification.Type.TRAY_NOTIFICATION);
        }

        newNotification.setHtmlContentAllowed(true);
        newNotification.setStyleName("custom-tray-notification-warning");
        newNotification.setDelayMsec(3000);
        newNotification.setPosition(Position.TOP_RIGHT);
        newNotification.show(Page.getCurrent());
    }

}
