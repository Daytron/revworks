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

import com.github.daytron.revworks.MainUI;
import com.github.daytron.revworks.ui.dashboard.lecturer.LecturerSubmitAnnouncementView;
import com.github.daytron.revworks.ui.dashboard.student.StudentCourseworkModuleView;
import com.github.daytron.revworks.ui.dashboard.student.StudentSubmitCourseworkSucessView;
import com.github.daytron.revworks.ui.dashboard.student.StudentSubmitCourseworkView;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

/**
 *
 * @author Ryan Gilera
 */
@SuppressWarnings("serial")
public class DashboardScreen extends VerticalLayout {

    private final NavigationMenu menu;

    public DashboardScreen(MainUI mainUI) {

        setMargin(true);
        HorizontalLayout headerLayout = new DashboardHeader();

        CssLayout viewContainer = new CssLayout();
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
                    FontAwesome.FOLDER);
            
            // Extra views 
            navigator.addView(StudentSubmitCourseworkSucessView.VIEW_NAME, 
                    StudentSubmitCourseworkSucessView.class);
        } else if (mainUI.getAccessControl().isUserALecturer()) {
            menu.addView(HomeView.class,
                    HomeView.VIEW_NAME,
                    HomeView.VIEW_CAPTION, FontAwesome.HOME);
            menu.addView(LecturerSubmitAnnouncementView.class,
                    LecturerSubmitAnnouncementView.VIEW_NAME,
                    LecturerSubmitAnnouncementView.VIEW_CAPTION, FontAwesome.BULLHORN);
        } else {

        }

        navigator.addViewChangeListener(viewChangeListener);

        addComponent(headerLayout);
        addComponent(menu);
        addComponent(viewContainer);
        setExpandRatio(viewContainer, 1);
        setWidth("100%");
        navigator.navigateTo(HomeView.VIEW_NAME);
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
        }

    };

}
