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
import com.github.daytron.revworks.event.AppEvent.*;
import com.github.daytron.revworks.service.CurrentUserSession;
import com.github.daytron.revworks.view.main.lecturer.LecturerCourseworkModuleView;
import com.github.daytron.revworks.view.main.lecturer.LecturerSubmitAnnouncementView;
import com.github.daytron.revworks.view.main.student.StudentCourseworkModuleView;
import com.github.daytron.revworks.view.main.student.StudentSubmitCourseworkSucessView;
import com.github.daytron.revworks.view.main.student.StudentSubmitCourseworkView;
import com.google.common.eventbus.Subscribe;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

/**
 * The main view page for the student and lecturer users. Menu, content and footer  
 * components are built and combined in this class.
 * 
 * @author Ryan Gilera
 */
@SuppressWarnings("serial")
public class MainView extends VerticalLayout {

    private final HeaderComponent headerLayout;
    private final NavigationMenu menu;
    private final VerticalLayout viewContainer;
    private final FooterComponent dashboardFooter;
    private final Panel scrollableContentPanel;
    
    /**
     * Builds the main components as object is created.
     * 
     * @param mainUI The one and only derived class of UI passed as an argument
     */
    public MainView(MainUI mainUI) {
        setSizeFull();
        addStyleName("dashscreen-main-layout");
        
        headerLayout = new HeaderComponent();
        // Save it in the session 
        // to be used for shutting down the executor service
        // when user logout
        CurrentUserSession.setDashboardHeader(headerLayout);

        scrollableContentPanel = new Panel();
        scrollableContentPanel.setSizeFull();
        
        viewContainer = new VerticalLayout();
        // viewContainer.setWidth("100%");
        // viewContainer.setHeightUndefined();
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
            menu.addView(HelpView.class, 
                    HelpView.VIEW_NAME, 
                    HelpView.VIEW_CAPTION, 
                    FontAwesome.LIFE_SAVER);
            
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
            menu.addView(HelpView.class, 
                    HelpView.VIEW_NAME, 
                    HelpView.VIEW_CAPTION, 
                    FontAwesome.LIFE_SAVER);
            
            // inner views
            navigator.addView(CourseworkView.VIEW_NAME, 
                    CourseworkView.class);
        } else {
            // Admin
        }
        
        scrollableContentPanel.setContent(viewContainer);

        addComponent(headerLayout);
        addComponent(menu);
        addComponent(scrollableContentPanel);
        
        dashboardFooter = new FooterComponent();
        addComponent(dashboardFooter);
        setExpandRatio(scrollableContentPanel, 1);
        setWidth("100%");
        
        navigator.navigateTo(HomeView.VIEW_NAME);
    }
    
    /**
     * Toggles the occurrence of maximising or minimising the coursework view.
     * 
     * @param event a custom event object defined in {@link com.github.daytron.revworks.event.AppEvent} class
     */
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
