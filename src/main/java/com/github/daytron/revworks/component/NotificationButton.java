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
package com.github.daytron.revworks.component;

import com.github.daytron.revworks.event.AppEvent;
import com.google.common.eventbus.Subscribe;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;

/**
 * A custom component derived class of Button for user notifications.
 * 
 * @author Ryan Gilera
 */
public class NotificationButton extends Button {

    private final String UNREAD_STYLE = "unread";
    private final String COMPONENT_ID = "dashboard-notifications";
    
    /**
     * Constructor. Apply custom icon and design.
     */
    public NotificationButton() {
        setIcon(FontAwesome.BELL);
        setId(COMPONENT_ID);
        addStyleName("notifications");
        addStyleName(ValoTheme.BUTTON_SMALL);
        addStyleName(ValoTheme.BUTTON_ICON_ONLY);
    }
    
    /**
     * Retrieves the total unread notifications and calls updateUnreadBadge 
     * method.
     * 
     * @param event a custom event object defined in 
     * {@link com.github.daytron.revworks.event.AppEvent} class
     */
    @Subscribe
    public void retrieveCurrentBadgeCount(
            AppEvent.UpdateNotificationButtonBadgeCountEvent event) {
        updateUnreadBadge(event.getCount());
    }
    
    /**
     * Updates the number of unread notifications in the notification button.
     * 
     * @param count the total unread notifications
     */
    public void updateUnreadBadge(int count) {
        setCaption(String.valueOf(count));
        
        String tooltipDescription = "";
        if (count > 0) {
            addStyleName(UNREAD_STYLE);
            tooltipDescription += count + " unread ";
        } else {
            removeStyleName(UNREAD_STYLE);
            tooltipDescription += "0 unread ";
        }
        
        tooltipDescription += "notifications";
        setDescription(tooltipDescription);
    }
    
}
