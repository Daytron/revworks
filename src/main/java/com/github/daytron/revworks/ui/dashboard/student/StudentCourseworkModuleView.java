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
package com.github.daytron.revworks.ui.dashboard.student;

import com.vaadin.ui.VerticalLayout;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;

/**
 *
 * @author Ryan Gilera
 */
public class StudentCourseworkModuleView extends VerticalLayout implements View {

    public static final String VIEW_NAME = "CourseworkModuleView";
    public static final String VIEW_CAPTION = "My Courseworks";

    public StudentCourseworkModuleView() {
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }

}
