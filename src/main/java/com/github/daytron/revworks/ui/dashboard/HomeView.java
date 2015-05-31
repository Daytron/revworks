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
package com.github.daytron.revworks.ui.dashboard;

import com.github.daytron.revworks.event.AppEvent;
import com.github.daytron.revworks.event.AppEventBus;
import com.github.daytron.revworks.service.DataProvider;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Ryan Gilera
 */
public class HomeView extends VerticalLayout implements View {

    public static final String VIEW_NAME = "Home";

    // Flags
    private boolean initialised = false;
    private boolean isDataLoaded = false;

    private static final String viewTitle = "What's New";

    private ResultSet dataContainer;

    public HomeView() {
    }

    private Component createAnnouncementPanel(
            final String title,
            final RichTextArea announcementTextArea,
            Date dateSubmitted,
            String authorSubmitted) {
        final CssLayout wrapperItem = new CssLayout();
        wrapperItem.setWidth("100%");
        wrapperItem.setStyleName(ValoTheme.LAYOUT_CARD);

        wrapperItem.addComponent(createPanelHeader(title));
        wrapperItem.addComponent(announcementTextArea);

        return wrapperItem;
    }

    private HorizontalLayout createPanelHeader(String title) {
        final HorizontalLayout layoutHeader = new HorizontalLayout();
        layoutHeader.addStyleName("v-panel-caption");
        layoutHeader.setWidth("100%");

        Label titleLabel = new Label(title);
        layoutHeader.addComponent(titleLabel);
        layoutHeader.setExpandRatio(titleLabel, 1);

        Button expandButton = new Button();
        expandButton.setIcon(FontAwesome.EXPAND);
        expandButton.addStyleName("borderless-colored");
        expandButton.addStyleName("small");
        expandButton.addStyleName("icon-only");
        layoutHeader.addComponent(expandButton);

        return layoutHeader;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

        if (!initialised) {
            AppEventBus.post(new AppEvent.LoadHomeViewDataEvent());
            this.dataContainer = DataProvider.get().getAnnouncementsContainer();

            if (dataContainer != null) {
                initialiseLayout();
                
                AppEventBus.post(new AppEvent.CloseSQLStatementAndConnectionEvent());
            }

        }
    }

    private void initialiseLayout() {
        setSizeFull();
        setMargin(true);
        setSpacing(true);

        Label whatsNewLabel = new Label(viewTitle);
        VerticalLayout contentLayout = new VerticalLayout();
        contentLayout.setSizeFull();
        
        // TODO logic for creating panel news from resultset object (dataContainer)
        
        
        addComponent(whatsNewLabel);
        addComponent(contentLayout);
        
        setExpandRatio(contentLayout, 1);
    }

}
