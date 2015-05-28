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

import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.ui.Notification;

/**
 * Utility class for formatted notifications.
 * 
 * @author Ryan Gilera
 */
public class NotificationUtil {
    /**
     * Creates an error notification with a single parameter for caption.
     * 
     * @param caption The main message of the notification
     */
    public static void showError(String caption) {
        showError(caption, "");
    }
    
    /**
     * Creates an error notification with two parameters for caption and 
     * description.
     * 
     * @param caption The main message of the notification
     * @param description Secondary description message
     */
    public static void showError(String caption, String description) {
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
        
        errorNotification.show(Page.getCurrent());
    }
}