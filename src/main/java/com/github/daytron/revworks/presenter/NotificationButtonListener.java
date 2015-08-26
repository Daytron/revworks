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
package com.github.daytron.revworks.presenter;

import com.github.daytron.revworks.MainUI;
import com.github.daytron.revworks.event.AppEvent;
import com.github.daytron.revworks.event.AppEventBus;
import com.github.daytron.revworks.model.UserNotification;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import elemental.events.KeyboardEvent;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Event handler for clicking notification button.
 * 
 * @author Ryan Gilera
 */
public class NotificationButtonListener implements Button.ClickListener {

    public NotificationButtonListener() {
    }

    /**
     * Opens a new pop window and populate it with notifications if now window  
     * is currently opened, otherwise it closes the current popup window.
     * 
     * @param event button click event
     */
    @Override
    public void buttonClick(Button.ClickEvent event) {

        final VerticalLayout popupLayout = new VerticalLayout();
        popupLayout.setMargin(true);
        popupLayout.setSpacing(true);

        Label popupTitle = new Label("Notifications");
        popupTitle.addStyleName(ValoTheme.LABEL_H3);
        popupTitle.addStyleName(ValoTheme.LABEL_BOLD);
        popupTitle.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        popupTitle.addStyleName("notifications-window-title-layout");
        popupLayout.addComponent(popupTitle);

        final CopyOnWriteArrayList<UserNotification> lisOfUserNotifications
                = MainUI.get().getNotificationsProvider().getListOfNotifications();

        // notify button to update count badge to zero
        AppEventBus.post(new AppEvent.UpdateNotificationButtonBadgeCountEvent(0));

        // Create sub title
        Label subtitleLabel = new Label();
        subtitleLabel.addStyleName(ValoTheme.LABEL_SMALL);
        subtitleLabel.addStyleName(ValoTheme.LABEL_NO_MARGIN);

        if (lisOfUserNotifications.isEmpty()) {
            subtitleLabel.setValue("No notifications found.");
        } else {
            if (lisOfUserNotifications.get(0).isRead()) {
                subtitleLabel.setValue("Showing the last 5 read notifications..");
            } else {
                subtitleLabel.setValue("Showing all unread notifications..");
            }
        }
        popupLayout.addComponent(subtitleLabel);

        // Create panel holder
        Panel notificationPanel = new Panel();
        notificationPanel.addStyleName("notifications-panel-layout");
        VerticalLayout panelContentLayout = new VerticalLayout();
        panelContentLayout.setMargin(true);
        panelContentLayout.setWidth("100%");
        panelContentLayout.setHeightUndefined();
        notificationPanel.setContent(panelContentLayout);
        notificationPanel.setWidth("100%");
        notificationPanel.setHeight("350px");

        popupLayout.addComponent(notificationPanel);

        // loop notifications, aggregate and add it to window
        for (UserNotification un : lisOfUserNotifications) {
            VerticalLayout notificationItemLayout
                    = new VerticalLayout();
            notificationItemLayout.setWidth("100%");

            Label fromUserLabel = new Label(un.getFromUserName()
                    + " " + un.getTitle());
            fromUserLabel.addStyleName(ValoTheme.LABEL_BOLD);
            notificationItemLayout.addComponent(fromUserLabel);

            Label titleLabel = new Label(un.getDateSubmitted()
                    .format(DateTimeFormatter.ofPattern("dd-MMM hh:mm a")));
            titleLabel.addStyleName(ValoTheme.LABEL_TINY);
            notificationItemLayout.addComponent(titleLabel);

            Label messageLabel = new Label(un.getMessage()
                    + " [id: " + un.getCourseworkID() + "] "
                    + "[title: " + un.getCourseworkTitle() + "]");
            messageLabel.addStyleName(ValoTheme.LABEL_SMALL);
            messageLabel.addStyleName(ValoTheme.LABEL_LIGHT);
            notificationItemLayout.addComponent(messageLabel);

            panelContentLayout.addComponent(notificationItemLayout);
        }

        Window notificationsWindow = MainUI.get().getNotificationsWindow();

        if (notificationsWindow == null) {
            notificationsWindow = new Window();
            notificationsWindow.setWidth(300.0f, Sizeable.Unit.PIXELS);
            notificationsWindow.addStyleName("notifications-window-layout");
            notificationsWindow.setClosable(false);
            notificationsWindow.setResizable(false);
            notificationsWindow.setDraggable(false);
            notificationsWindow.setCloseShortcut(KeyboardEvent.KeyCode.ESC, null);
            notificationsWindow.setContent(popupLayout);

            MainUI.get().setNotificationsWindow(notificationsWindow);
        }

        if (!notificationsWindow.isAttached()) {
            // Pause the auto retrieve notification while
            // opening a new window
            MainUI.get().getNotificationsProvider().setPause(true);
            
            notificationsWindow.setPositionX(event.getClientX()
                    - event.getRelativeX() - 300);
            notificationsWindow.setPositionY(event.getClientY()
                    - event.getRelativeY() + 30);
            MainUI.get().addWindow(notificationsWindow);
            notificationsWindow.focus();

            // Update unread to read (database)
            AppEventBus.post(new AppEvent.UpdateNotificationToReadEvent(
                    lisOfUserNotifications));
        } else {
            notificationsWindow.close();
            MainUI.get().setNotificationsWindow(null);

            // Unpause the auto retrieve notification after closing window
            MainUI.get().getNotificationsProvider().setPause(false);
        }

    }

}
