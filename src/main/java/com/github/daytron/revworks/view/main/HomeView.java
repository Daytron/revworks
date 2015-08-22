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
package com.github.daytron.revworks.view.main;

import com.github.daytron.revworks.MainUI;
import com.github.daytron.revworks.data.ErrorMsg;
import com.github.daytron.revworks.data.FontAwesomeIcon;
import com.github.daytron.revworks.model.Announcement;
import com.github.daytron.revworks.exception.SQLErrorQueryException;
import com.github.daytron.revworks.exception.SQLErrorRetrievingConnectionAndPoolException;
import com.github.daytron.revworks.service.CurrentUserSession;
import com.github.daytron.revworks.util.NotificationUtil;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * The default view of the navigator and dashboard screen. Displays all the
 * relevant announcements in the past 7 days.
 *
 * @author Ryan Gilera
 */
@SuppressWarnings("serial")
public class HomeView extends Panel implements View {

    public static final String VIEW_NAME = "HomeView";
    public static final String VIEW_CAPTION = "Home";
    private static final String VIEW_TITLE = "What's New";

    // Flag
    private boolean initialised = false;

    private List<Announcement> listOfAnnouncements;

    public HomeView() {
    }

    private Component createAnnouncementPanel(final Announcement announcement) {
        final CssLayout wrapperItem = new CssLayout();
        wrapperItem.setWidth("100%");
        wrapperItem.setStyleName(ValoTheme.LAYOUT_CARD);
        wrapperItem.addStyleName("panel-wrapper-custom");
        
        wrapperItem.addComponent(createPanelHeader(announcement.getTitle(),
                announcement.getDateTimeSubmitted()));

        VerticalLayout contentLayout = new VerticalLayout();
        contentLayout.setSizeFull();

        HorizontalLayout contentMetaDataBar = new HorizontalLayout();
        contentMetaDataBar.addStyleName("content-sub-header");
        contentMetaDataBar.setWidth("100%");
        contentMetaDataBar.setSpacing(true);

        Label sourceAuthor = new Label(announcement.getAnnouncementSource());
        sourceAuthor.setSizeFull();
        sourceAuthor.setStyleName(ValoTheme.LABEL_SMALL);

        contentMetaDataBar.addComponent(sourceAuthor);

        if (announcement.isClassWideAnnouncement()) {

            Label moduleIdLabel = new Label(announcement.getModuleId());
            moduleIdLabel.setStyleName(ValoTheme.LABEL_SMALL);
            moduleIdLabel.setSizeUndefined();
            contentMetaDataBar.addComponent(moduleIdLabel);

            Label moduleNameLabel = new Label(announcement.getModulename());
            moduleNameLabel.setStyleName(ValoTheme.LABEL_SMALL);
            moduleNameLabel.setSizeUndefined();
            contentMetaDataBar.addComponent(moduleNameLabel);
        }

        contentMetaDataBar.setExpandRatio(sourceAuthor, 1);

        contentLayout.addComponent(contentMetaDataBar);

        VerticalLayout messageLayout = new VerticalLayout();
        messageLayout.setWidth("100%");
        messageLayout.addStyleName("content-message");
        
        Label message = new Label(announcement.getMessage(), ContentMode.HTML);
        messageLayout.addComponent(message);
        contentLayout.addComponent(messageLayout);

        contentLayout.setExpandRatio(messageLayout, 1);

        wrapperItem.addComponent(contentLayout);

        return wrapperItem;
    }

    private HorizontalLayout createPanelHeader(String title,
            LocalDateTime dateString) {
        final HorizontalLayout layoutHeader = new HorizontalLayout();
        layoutHeader.addStyleName("v-panel-caption");
        layoutHeader.addStyleName("panel-header");
        layoutHeader.setWidth("100%");

        Label titleLabel = new Label(title);
        titleLabel.setStyleName(ValoTheme.LABEL_BOLD);
        titleLabel.setSizeFull();
        layoutHeader.addComponent(titleLabel);

        DateTimeFormatter dateTimeFormatter
                = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

        Label dateLabel = new Label(
                FontAwesomeIcon.CALENDAR.getLgSize() + "&nbsp;&nbsp;" +
                        dateString.format(dateTimeFormatter),
                ContentMode.HTML);
        dateLabel.setSizeUndefined();
        dateLabel.setStyleName(ValoTheme.LABEL_SMALL);
        layoutHeader.addComponent(dateLabel);

        layoutHeader.setExpandRatio(titleLabel, 1);
        return layoutHeader;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        // Important!!
        // shutdown and cleanup any previous threads created
        // from coursework view and comment component if possible
        CurrentUserSession.shutdownCourseworkViewExecutorService();
        CurrentUserSession.shutdownCommentExectorService();
        
        if (!initialised) {
            try {

                // Does not matter if lecturer or student's data provider
                // They share the common method from DataProviderAbstract
                this.listOfAnnouncements
                        = MainUI.get().getStudentDataProvider()
                                .populateHomeViewWithData();

                initView();
                initialised = true;

            } catch (SQLErrorQueryException |
                    SQLErrorRetrievingConnectionAndPoolException ex) {
                NotificationUtil.showError(
                        ErrorMsg.DATA_FETCH_ERROR.getText(),
                        ErrorMsg.CONSULT_YOUR_ADMIN.getText());
            }
        }
    }

    private void initView() {

        setSizeFull();
        VerticalLayout wrapperLayout = new VerticalLayout();
        wrapperLayout.setWidth("100%");
        wrapperLayout.setHeightUndefined();
        
        wrapperLayout.setMargin(true);
        wrapperLayout.setSpacing(true);

        Label whatsNewLabel;
        
        if (this.listOfAnnouncements.isEmpty()) {
            whatsNewLabel = new Label("No announcement for this week.");
        } else {
            whatsNewLabel = new Label(VIEW_TITLE);
        }
        
        whatsNewLabel.setStyleName(ValoTheme.LABEL_H2);
        whatsNewLabel.addStyleName(ValoTheme.LABEL_BOLD);
        VerticalLayout contentLayout = new VerticalLayout();
        contentLayout.setSpacing(true);

        wrapperLayout.addComponent(whatsNewLabel);

        for (Announcement announcement : this.listOfAnnouncements) {
            Component announcementComponent
                    = createAnnouncementPanel(announcement);
            contentLayout.addComponent(announcementComponent);
        }

        wrapperLayout.addComponent(contentLayout);
        wrapperLayout.setExpandRatio(contentLayout, 1);
        
        setContent(wrapperLayout);
        initialised = true;
    }

}
