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
package com.github.daytron.revworks.view.admin;

import com.github.daytron.revworks.MainUI;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

/**
 *
 * @author Ryan Gilera
 */
@SuppressWarnings("serial")
public class Dashboard extends HorizontalLayout {

    private final DashboardMenu menu;
    
    public Dashboard(MainUI mainUI) {
        
        Panel contentWrapperPanel = new Panel();
        contentWrapperPanel.setSizeFull();
        
        VerticalLayout viewContainer = new VerticalLayout();
        viewContainer.setSizeFull();
        
        final Navigator navigator = new Navigator(mainUI, viewContainer);
        navigator.setErrorView(DashboardErrorView.class);
        
        menu = new DashboardMenu(navigator);
        menu.addView(DashboardHomeView.class, 
                DashboardHomeView.VIEW_NAME, 
                DashboardHomeView.VIEW_CAPTION, 
                FontAwesome.HOME);
        menu.addView(DashboardCreateAnnouncementView.class, 
                DashboardCreateAnnouncementView.VIEW_NAME, 
                DashboardCreateAnnouncementView.VIEW_CAPTION, 
                FontAwesome.BULLHORN);
        
        contentWrapperPanel.setContent(viewContainer);
        
        navigator.addViewChangeListener(viewChangeListener);
        
        addComponent(menu);
        addComponent(contentWrapperPanel);
        
        setExpandRatio(contentWrapperPanel, 1);
        setSizeFull();
        
        navigator.navigateTo(DashboardHomeView.VIEW_NAME);
    }
    
    // notify the view menu about view changes so that it can display which view
    // is currently active
    ViewChangeListener viewChangeListener = new ViewChangeListener() {

        @Override
        public boolean beforeViewChange(ViewChangeListener.ViewChangeEvent event) {
            return true;
        }

        @Override
        public void afterViewChange(ViewChangeListener.ViewChangeEvent event) {
            menu.setActiveView(event.getViewName());
        }

    };
    
    
}
