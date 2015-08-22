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

import com.github.daytron.revworks.data.PreparedQueryStatement;
import com.github.daytron.revworks.data.UserNotificationType;
import com.github.daytron.revworks.event.AppEvent;
import com.github.daytron.revworks.model.User;
import com.github.daytron.revworks.model.UserNotification;
import com.google.common.eventbus.Subscribe;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ryan Gilera
 */
public class NotificationInserter extends QueryManagerAbstract {

    @Subscribe
    public void insertNotificationNote(AppEvent.InsertNotificationNewNoteEvent event) {
        if (reserveConnectionPool()) {
            try {
                PreparedStatement preparedStatement
                        = getConnection().prepareStatement(
                                PreparedQueryStatement.INSERT_NOTIFICATION.getQuery(),
                                Statement.RETURN_GENERATED_KEYS);

                preparedStatement.setString(1, event.getTitle());
                preparedStatement.setString(2, event.getMessage());

                preparedStatement.setBoolean(3, false);

                int notificationTypeId;
                
                if (event.getUserNotificationType() == UserNotificationType.COURSEWORK) {
                    notificationTypeId = 1;
                } else if (event.getUserNotificationType() == UserNotificationType.NOTE) {
                    notificationTypeId = 2;
                } else {
                    notificationTypeId = 3;
                }

                preparedStatement.setInt(4, notificationTypeId);

                preparedStatement.setInt(5, event.getToUserId());
                preparedStatement.setInt(6, 
                        ((User)CurrentUserSession.getPrincipal()).getId());

                preparedStatement.executeUpdate();
                getConnection().commit();

                // Get the new id from insert query
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                int newId;

                if (resultSet.next()) {
                    newId = resultSet.getInt(1);
                    
                    preparedStatement.close();
                    resultSet.close();
                } else {
                    System.out.println("Cannot retrieve generated Id.");
                    preparedStatement.close();
                    resultSet.close();

                    releaseConnection();
                    notifyDataSendError();
                    return;
                }

                if (notificationTypeId <= 3 && notificationTypeId > 0) {
                    PreparedStatement prepareStatementNC
                            = getConnection().prepareStatement(
                                    PreparedQueryStatement
                                            .INSERT_NOTIFICATIONS_COURSEWORK.getQuery());
                    prepareStatementNC.setInt(1, newId);
                    prepareStatementNC.setInt(2, event.getCourseworkId());

                    prepareStatementNC.executeUpdate();
                    getConnection().commit();

                    prepareStatementNC.close();
                }

            } catch (SQLException ex) {
                Logger.getLogger(NotificationInserter.class.getName()).log(Level.SEVERE, null, ex);
                notifyDataSendError();
            } finally {
                releaseConnection();
            }
        } else {
            notifyDataSendError();
        }
    }

    @Subscribe
    public void updateNotificationToRead(AppEvent.UpdateNotificationToReadEvent event) {

        if (reserveConnectionPool()) {
            try {
                for (UserNotification userNotification : event.getListOfUserNotifications()) {
                    // Skip if it is already read
                    if (userNotification.isRead()) {
                        continue;
                    }

                    PreparedStatement preparedStatement
                            = getConnection().prepareStatement(PreparedQueryStatement.UPDATE_NOTIFICATION_TO_READ.getQuery());
                    preparedStatement.setInt(1, userNotification.getId());

                    preparedStatement.executeUpdate();
                    getConnection().commit();

                    preparedStatement.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(NotificationInserter.class.getName())
                        .log(Level.SEVERE, null, ex);
                notifyDataSendError();
            } finally {
                releaseConnection();
            }
        } else {
            notifyDataSendError();
        }

    }
    
}
