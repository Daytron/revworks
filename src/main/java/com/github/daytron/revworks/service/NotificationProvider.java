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
package com.github.daytron.revworks.service;

import com.github.daytron.revworks.event.AppEvent;
import com.github.daytron.revworks.event.AppEventBus;
import com.github.daytron.revworks.model.UserNotification;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author Ryan Gilera
 */
public class NotificationProvider extends QueryManagerAbstract {

    private CopyOnWriteArrayList<UserNotification> listOfNotifications;
    private boolean pause;
    
    public NotificationProvider() {
        this.listOfNotifications = new CopyOnWriteArrayList<>();
        this.pause = false;
    }

    public synchronized void setListOfNotifications(CopyOnWriteArrayList<UserNotification> 
            listOfNotifications) {
        this.listOfNotifications = listOfNotifications;
        
        // Count how many unread notifications
        int unreadCount = 0;
        for (UserNotification userNotification : listOfNotifications) {
            if (!userNotification.isRead()) {
                unreadCount += 1;
            }
        }
        
        // Call an event to trigger in the button to update unread badge count
        AppEventBus.post(
                new AppEvent.UpdateNotificationButtonBadgeCountEvent(unreadCount));
    }

    public CopyOnWriteArrayList<UserNotification> getListOfNotifications() {
        return listOfNotifications;
    }

    public boolean isPause() {
        return pause;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }
    
}
