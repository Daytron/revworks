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
package com.github.daytron.revworks.view.dashboard;

import com.github.daytron.revworks.MainUI;
import com.github.daytron.revworks.component.NotificationButton;
import com.github.daytron.revworks.data.PreparedQueryStatement;
import com.github.daytron.revworks.data.UserNotificationType;
import com.github.daytron.revworks.event.AppEvent;
import com.github.daytron.revworks.event.AppEventBus;
import com.github.daytron.revworks.model.User;
import com.github.daytron.revworks.model.UserNotification;
import com.github.daytron.revworks.presenter.NotificationButtonListener;
import com.github.daytron.revworks.service.CurrentUserSession;
import com.github.daytron.revworks.service.DataProviderAbstract;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Ui widget component for dashboard header.
 *
 * @author Ryan Gilera
 */
@SuppressWarnings("serial")
public class DashboardHeader extends HorizontalLayout {

    private final ScheduledExecutorService scheduler
            = Executors.newScheduledThreadPool(1);
    private final Runnable runnableTask;
    private final ScheduledFuture scheduledFuture;

    public DashboardHeader() {
        setSpacing(true);
        addStyleName("view-dashboard-header");

        Label titleLabel = new Label("RevWorks");
        titleLabel.setSizeUndefined();
        titleLabel.addStyleName(ValoTheme.LABEL_H2);
        titleLabel.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        titleLabel.addStyleName(ValoTheme.LABEL_BOLD);
        addComponent(titleLabel);

        Label welcomeUserLabel = new Label("Welcome "
                + MainUI.get().getAccessControl().getFullName());
        welcomeUserLabel.setStyleName("welcome-text");

        final NotificationButton notificationButton
                = new NotificationButton();
        notificationButton.addClickListener(
                new NotificationButtonListener(MainUI.get().getNotificationsWindow()));
        AppEventBus.register(notificationButton);

        Button logOutButton = new Button();
        logOutButton.setDescription("Sign out");
        logOutButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        logOutButton.addStyleName(ValoTheme.BUTTON_SMALL);
        logOutButton.setIcon(FontAwesome.SIGN_OUT);
        logOutButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                AppEventBus.post(new AppEvent.UserLogoutRequestEvent());
            }
        });

        HorizontalLayout toolbar = new HorizontalLayout(welcomeUserLabel,
                notificationButton, logOutButton);
        toolbar.setSpacing(true);
        toolbar.setStyleName("dashboard-toolbar");
        toolbar.setComponentAlignment(welcomeUserLabel, Alignment.BOTTOM_LEFT);

        addComponent(toolbar);

        runnableTask = new NotificationsExtractorRunnable();
        scheduledFuture = scheduler.scheduleWithFixedDelay(runnableTask,
                0, 3, TimeUnit.SECONDS);

    }

    public void shutdownNotificationExecutor() {
        scheduledFuture.cancel(true);
        scheduler.shutdownNow();
    }

    final class NotificationsExtractorRunnable extends DataProviderAbstract
            implements Runnable {

        private int previousUnreadCount = 0;
        private int previousReadCount = 0;

        @Override
        public void run() {

            // Skip retrieval if notification button is pressed
            if (MainUI.get().getNotificationsProvider().isPause()) {
                return;
            }

            CopyOnWriteArrayList<UserNotification> listOfUserNotifications
                    = new CopyOnWriteArrayList<>();
            if (reserveConnectionPool()) {
                try {
                    try (PreparedStatement preparedStatementUnread = getConnection().prepareStatement(
                            PreparedQueryStatement.SELECT_USER_NOTIFICATION_UNREAD
                            .getQuery())) {
                        preparedStatementUnread.setInt(1,
                                ((User) CurrentUserSession.getPrincipal()).getId());
                        ResultSet resultSetUnread = preparedStatementUnread.executeQuery();

                        if (resultSetUnread.next()) {

                            // Calculate the current resulting row count
                            int numberOfResultedRows = 0;
                            resultSetUnread.beforeFirst();
                            while (resultSetUnread.next()) {
                                numberOfResultedRows += 1;
                            }

                            if (numberOfResultedRows == 0
                                    || numberOfResultedRows == previousUnreadCount) {
                                preparedStatementUnread.close();
                                resultSetUnread.close();
                                releaseConnection();
                                return;
                            }

                            // save new unread count
                            previousUnreadCount = numberOfResultedRows;

                            resultSetUnread.beforeFirst();
                            DateTimeFormatter formatter
                                    = DateTimeFormatter.ofPattern("dd-MMM hh:mm a");
                            while (resultSetUnread.next()) {
                                Timestamp timestamp = resultSetUnread.getTimestamp(4);
                                LocalDateTime submittedDateTime = timestamp.toLocalDateTime();

                                int notificationTypeID = resultSetUnread.getInt(6);
                                UserNotificationType userNotificationType;

                                switch (notificationTypeID) {
                                    case 1:
                                        userNotificationType
                                                = UserNotificationType.COURSEWORK;
                                        break;
                                    case 2:
                                        userNotificationType
                                                = UserNotificationType.NOTE;
                                        break;
                                    case 3:
                                        userNotificationType
                                                = UserNotificationType.COMMENT;
                                        break;
                                    default:
                                        userNotificationType = null;
                                        break;
                                }

                                UserNotification userNotification
                                        = new UserNotification(resultSetUnread.getInt(1),
                                                resultSetUnread.getString(2),
                                                resultSetUnread.getString(3),
                                                submittedDateTime,
                                                resultSetUnread.getBoolean(5),
                                                userNotificationType,
                                                resultSetUnread.getInt(7),
                                                resultSetUnread.getString(8),
                                                resultSetUnread.getInt(9),
                                                resultSetUnread.getString(10));

                                listOfUserNotifications.add(userNotification);

                            }

                            preparedStatementUnread.close();
                            resultSetUnread.close();
                        } else {
                            // else empty
                            // get last 30 notifications (aggregate later on)

                            // close first
                            preparedStatementUnread.close();
                            resultSetUnread.close();

                            // Begin extraction of read notificaitons
                            PreparedStatement preparedStatementRead
                                    = getConnection().prepareStatement(
                                            PreparedQueryStatement.SELECT_USER_NOTIFICATION_READ
                                            .getQuery());
                            preparedStatementRead.setInt(1,
                                    ((User) CurrentUserSession.getPrincipal()).getId());
                            ResultSet resultSetRead
                                    = preparedStatementRead.executeQuery();

                            if (!resultSetRead.next()) {
                                preparedStatementRead.close();
                                resultSetRead.close();
                                releaseConnection();
                                return;
                            }

                            // Calculate the current resulting row count
                            int numberOfResultedRows = 0;
                            resultSetRead.beforeFirst();
                            while (resultSetRead.next()) {
                                numberOfResultedRows += 1;
                            }

                            if (numberOfResultedRows == 0
                                    || numberOfResultedRows == previousReadCount) {
                                preparedStatementRead.close();
                                resultSetRead.close();
                                releaseConnection();
                                return;
                            }

                            // save new unread count
                            previousReadCount = numberOfResultedRows;

                            resultSetRead.beforeFirst();
                            DateTimeFormatter formatter
                                    = DateTimeFormatter.ofPattern("dd-MMM hh:mm a");
                            while (resultSetRead.next()) {
                                Timestamp timestamp = resultSetRead.getTimestamp(4);
                                LocalDateTime submittedDateTime = timestamp.toLocalDateTime();

                                int notificationTypeID = resultSetRead.getInt(6);
                                UserNotificationType userNotificationType;

                                switch (notificationTypeID) {
                                    case 1:
                                        userNotificationType
                                                = UserNotificationType.COURSEWORK;
                                        break;
                                    case 2:
                                        userNotificationType
                                                = UserNotificationType.NOTE;
                                        break;
                                    case 3:
                                        userNotificationType
                                                = UserNotificationType.COMMENT;
                                        break;
                                    default:
                                        userNotificationType = null;
                                        break;
                                }

                                UserNotification userNotification
                                        = new UserNotification(resultSetRead.getInt(1),
                                                resultSetRead.getString(2),
                                                resultSetRead.getString(3),
                                                submittedDateTime,
                                                resultSetRead.getBoolean(5),
                                                userNotificationType,
                                                resultSetRead.getInt(7),
                                                resultSetRead.getString(8),
                                                resultSetRead.getInt(9),
                                                resultSetRead.getString(10));

                                listOfUserNotifications.add(userNotification);

                            }

                            preparedStatementRead.close();
                            resultSetRead.close();

                        }

                    }

                    MainUI.get().getNotificationsProvider()
                            .setListOfNotifications(listOfUserNotifications);

                } catch (SQLException ex) {
                    Logger.getLogger(DashboardHeader.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    releaseConnection();
                }
            }
        }

    }
}
