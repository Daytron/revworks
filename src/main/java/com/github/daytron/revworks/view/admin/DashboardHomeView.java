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

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 *
 * @author Ryan Gilera
 */
public final class DashboardHomeView extends VerticalLayout implements View {
    
    public final static String VIEW_NAME = "DASHBOARD";
    public final static String VIEW_CAPTION = "Home";
    public final static String VIEW_TITLE = "Dashboard";
    
    private boolean isInitialised = false;
    
    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        if (!isInitialised) {
            initView();
            
            isInitialised = true;
        }
    }
    
    private void initView() {
        setSizeFull();
        setMargin(true);
        
        Label title = new Label(VIEW_TITLE);
        title.addStyleName(ValoTheme.LABEL_H1);
        addComponent(title);
    }
    
}
