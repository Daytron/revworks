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

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.themes.ValoTheme;

/**
 * The screen when a user tried to navigate to a view that does not exist.
 *
 * @author Ryan Gilera
 */
@SuppressWarnings("serial")
public class ErrorView extends Panel implements View {

    private final Label errorLabel;

    public ErrorView() {
        setSizeFull();
        
        VerticalLayout wrapperLayout = new VerticalLayout();
        wrapperLayout.setWidth("100%");
        wrapperLayout.setHeightUndefined();
        
        wrapperLayout.setMargin(true);
        wrapperLayout.setSpacing(true);

        Label headerErrorLabel = new Label("The view could not be found!");
        headerErrorLabel.addStyleName(Reindeer.LABEL_H1);
        headerErrorLabel.addStyleName(ValoTheme.LABEL_FAILURE);

        wrapperLayout.addComponent(headerErrorLabel);

        this.errorLabel = new Label();
        wrapperLayout.addComponent(this.errorLabel);
        
        setContent(wrapperLayout);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        String message = String.format(
                "You attempted to navigate to a view ('%s') "
                + "that does not exist.",
                event.getViewName());

        this.errorLabel.setValue(message);
    }

}
