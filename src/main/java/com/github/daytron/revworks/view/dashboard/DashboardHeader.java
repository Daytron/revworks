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
import com.github.daytron.revworks.event.AppEvent;
import com.github.daytron.revworks.event.AppEventBus;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Ui widget component for dashboard header.
 *
 * @author Ryan Gilera
 */
@SuppressWarnings("serial")
public class DashboardHeader extends HorizontalLayout {

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

        final Button notificatioButton = new Button();
        notificatioButton.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        notificatioButton.setStyleName(ValoTheme.BUTTON_SMALL);
        notificatioButton.setIcon(FontAwesome.BELL);

        Button logOutButton = new Button();
        logOutButton.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        logOutButton.setStyleName(ValoTheme.BUTTON_SMALL);
        logOutButton.setIcon(FontAwesome.SIGN_OUT);
        logOutButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                AppEventBus.post(new AppEvent.UserLogoutRequestEvent());
            }
        });

        HorizontalLayout toolbar = new HorizontalLayout(welcomeUserLabel,
                notificatioButton, logOutButton);
        toolbar.setSpacing(true);
        toolbar.setStyleName("dashboard-toolbar");
        toolbar.setComponentAlignment(welcomeUserLabel, Alignment.BOTTOM_LEFT);

        addComponent(toolbar);

    }
}
