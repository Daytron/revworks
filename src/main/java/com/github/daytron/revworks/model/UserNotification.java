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
package com.github.daytron.revworks.model;

import com.github.daytron.revworks.data.UserNotificationType;
import java.time.LocalDateTime;

/**
 * Model for UserNotification table.
 * 
 * @author Ryan Gilera
 */
public class UserNotification {
    
    private final int id;
    private final String title;
    private final String message;
    private final LocalDateTime dateSubmitted;
    private final boolean read;
    private final UserNotificationType userNotificationType;
    private final int fromUserID;
    private final String fromUserName;
    private final Integer courseworkID;
    private final String courseworkTitle;

    /**
     * A class constructor that takes three integers, four Strings, a 
     * LocalDateTime object, a boolean and a UserNotificationType object.
     * 
     * @param id notification id
     * @param title notification title
     * @param message notification message
     * @param dateSubmitted date submitted
     * @param read status if the current user has read it or not
     * @param userNotificationType type of notification
     * @param fromUserID the user id of the source of the notification
     * @param fromUserName the user name of the source of the notification
     * @param courseworkID the associated coursework id
     * @param courseworkTitle  the associated coursework title
     */
    public UserNotification(int id, String title, String message,
            LocalDateTime dateSubmitted, boolean read,
            UserNotificationType userNotificationType,
            int fromUserID, String fromUserName, int courseworkID,
            String courseworkTitle) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.dateSubmitted = dateSubmitted;
        this.read = read;
        this.userNotificationType = userNotificationType;
        this.fromUserID = fromUserID;
        this.fromUserName = fromUserName;
        this.courseworkID = courseworkID;
        this.courseworkTitle = courseworkTitle;
    }

    /**
     * Access the notificaiton id.
     * 
     * @return id as integer
     */
    public int getId() {
        return id;
    }

    /**
     * Access the notification title.
     * 
     * @return title as String
     */
    public String getTitle() {
        return title;
    }

    /**
     * Access the notification message.
     * 
     * @return message as String
     */
    public String getMessage() {
        return message;
    }

    /**
     * Access the submission date time of the notification. 
     * 
     * @return LocalDateTime object
     */
    public LocalDateTime getDateSubmitted() {
        return dateSubmitted;
    }

    /**
     * Determines whether the current user has read the notification.
     * 
     * @return true if user has read it, otherwise false
     */
    public boolean isRead() {
        return read;
    }

    /**
     * Access the type of notification.
     * 
     * @return UserNotificationType enum item
     */
    public UserNotificationType getUserNotificationType() {
        return userNotificationType;
    }

    /**
     * Access the user id of the source of the event of the notification.
     * 
     * @return user id as integer
     */
    public int getFromUserID() {
        return fromUserID;
    }
    
    /**
     * Access the user name of the source of the event of the notification.
     * 
     * @return user name as String
     */
    public String getFromUserName() {
        return fromUserName;
    }

    /**
     * Access the associated coursework id.
     * 
     * @return id as integer
     */
    public int getCourseworkID() {
        return courseworkID;
    }

    /**
     * Access the associated coursework title.
     * 
     * @return title as String
     */
    public String getCourseworkTitle() {
        return courseworkTitle;
    }
    
}
