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
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.themes.ValoTheme;

/**
 * The screen for when an admin tried to navigate to a view that does not exist.
 *
 * @author Ryan Gilera
 */
@SuppressWarnings("serial")
public class DashboardErrorView extends VerticalLayout implements View {

    private final Label errorLabel;

    /**
     * A class constructor that builds its UI components upon object creation.
     */
    public DashboardErrorView() {
        setWidth("100%");
        setHeightUndefined();
        
        setMargin(true);
        setSpacing(true);

        Label headerErrorLabel = new Label("The view could not be found!");
        headerErrorLabel.addStyleName(Reindeer.LABEL_H1);
        headerErrorLabel.addStyleName(ValoTheme.LABEL_FAILURE);

        addComponent(headerErrorLabel);

        this.errorLabel = new Label();
        addComponent(this.errorLabel);
    }

    /**
     * The entry point for all derived classes of View. Initialise the error 
     * message.
     * 
     * @param event ViewChangeEvent object
     */
    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        String message = String.format(
                "You attempted to navigate to a view ('%s') "
                + "that does not exist.",
                event.getViewName());

        this.errorLabel.setValue(message);
    }

}
