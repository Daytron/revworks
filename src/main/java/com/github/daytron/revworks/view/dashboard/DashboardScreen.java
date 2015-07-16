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
import com.github.daytron.revworks.event.AppEvent.*;
import com.github.daytron.revworks.event.AppEventBus;
import com.github.daytron.revworks.service.CurrentUserSession;
import com.github.daytron.revworks.view.dashboard.lecturer.LecturerCourseworkModuleView;
import com.github.daytron.revworks.view.dashboard.lecturer.LecturerSubmitAnnouncementView;
import com.github.daytron.revworks.view.dashboard.student.StudentCourseworkModuleView;
import com.github.daytron.revworks.view.dashboard.student.StudentSubmitCourseworkSucessView;
import com.github.daytron.revworks.view.dashboard.student.StudentSubmitCourseworkView;
import com.google.common.eventbus.Subscribe;
import com.vaadin.event.LayoutEvents;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.VerticalLayout;

/**
 *
 * @author Ryan Gilera
 */
@SuppressWarnings("serial")
public class DashboardScreen extends VerticalLayout {

    private final DashboardHeader headerLayout;
    private final NavigationMenu menu;
    private final CssLayout viewContainer;
    private final DashboardFooter dashboardFooter;
    
    public DashboardScreen(MainUI mainUI) {

        setMargin(true);
        headerLayout = new DashboardHeader();
        // Save it in the session 
        // to be used for shutting down the executor service
        // when user logout
        CurrentUserSession.setDashboardHeader(headerLayout);

        viewContainer = new CssLayout();
        viewContainer.addStyleName("valo-content");
        viewContainer.setSizeFull();

        final Navigator navigator = new Navigator(mainUI, viewContainer);
        navigator.setErrorView(ErrorView.class);
        
        menu = new NavigationMenu(navigator);
        
        if (mainUI.getAccessControl().isUserAStudent()) {
            menu.addView(HomeView.class,
                    HomeView.VIEW_NAME,
                    HomeView.VIEW_CAPTION, FontAwesome.HOME);
            menu.addView(StudentSubmitCourseworkView.class, 
                    StudentSubmitCourseworkView.VIEW_NAME, 
                    StudentSubmitCourseworkView.VIEW_CAPTION, 
                    FontAwesome.CLOUD_UPLOAD);
            menu.addView(StudentCourseworkModuleView.class,
                    StudentCourseworkModuleView.VIEW_NAME,
                    StudentCourseworkModuleView.VIEW_CAPTION, 
                    FontAwesome.FOLDER_OPEN);
            
            // inner views 
            navigator.addView(StudentSubmitCourseworkSucessView.VIEW_NAME, 
                    StudentSubmitCourseworkSucessView.class);
            navigator.addView(CourseworkView.VIEW_NAME, 
                    CourseworkView.class);
        } else if (mainUI.getAccessControl().isUserALecturer()) {
            menu.addView(HomeView.class,
                    HomeView.VIEW_NAME,
                    HomeView.VIEW_CAPTION, FontAwesome.HOME);
            menu.addView(LecturerCourseworkModuleView.class, 
                    LecturerCourseworkModuleView.VIEW_NAME, 
                    LecturerCourseworkModuleView.VIEW_CAPTION, 
                    FontAwesome.FOLDER_OPEN);
            menu.addView(LecturerSubmitAnnouncementView.class,
                    LecturerSubmitAnnouncementView.VIEW_NAME,
                    LecturerSubmitAnnouncementView.VIEW_CAPTION, FontAwesome.BULLHORN);
            
            // inner views
            navigator.addView(CourseworkView.VIEW_NAME, 
                    CourseworkView.class);
        } else {
            // Admin
        }
        

        addComponent(headerLayout);
        addComponent(menu);
        addComponent(viewContainer);
        
        dashboardFooter = new DashboardFooter();
        addComponent(dashboardFooter);
        setExpandRatio(viewContainer, 1);
        setWidth("100%");
        
        navigator.navigateTo(HomeView.VIEW_NAME);
        
        addLayoutClickListener(new LayoutEvents.LayoutClickListener() {

            @Override
            public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                AppEventBus.post(new CloseAllWindowsEvent());
            }
        });
    }
    
    @Subscribe
    public void toggleMaxViewForCourseworkView(final ToggleCourseworkViewEvent event) {
        viewContainer.setVisible(true);
        headerLayout.setVisible(!event.isToggled());
        menu.setVisible(!event.isToggled());
        dashboardFooter.setVisible(!event.isToggled());
        
        if (event.isToggled()) {
            event.getContentLayout().setVisible(true);
            event.getContentLayout().addStyleName("max");
        } else {
            event.getContentLayout().removeStyleName("max");
        }
    }

}
