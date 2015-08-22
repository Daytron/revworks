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
package com.github.daytron.revworks.view.main.student;

import com.github.daytron.revworks.data.FontAwesomeIcon;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

/**
 * A view for when a student has successfully submitted a coursework.
 *
 * @author Ryan Gilera
 */
@SuppressWarnings("serial")
public class StudentSubmitCourseworkSucessView extends Panel
        implements View {

    public static final String VIEW_NAME = "SubmitCourseworkSuccessView";

    public StudentSubmitCourseworkSucessView() {
        setSizeFull();
        
        VerticalLayout wrapperLayout = new VerticalLayout();
        wrapperLayout.setWidth("100%");
        wrapperLayout.setHeightUndefined();
        
        wrapperLayout.setSpacing(true);

        // Extra top space
        Label gapLabelTop = new Label();
        gapLabelTop.setHeight(20, Unit.PIXELS);
        wrapperLayout.addComponent(gapLabelTop);
        
        // Check image Row
        final HorizontalLayout checkLayout = new HorizontalLayout();
        checkLayout.setWidth("100%");
        checkLayout.setSpacing(true);
        
        Label leftCheckSpaceLabel = new Label(" ");
        leftCheckSpaceLabel.setWidth("100%");
        checkLayout.addComponent(leftCheckSpaceLabel);
        
        Label checkLabel = new Label(FontAwesomeIcon.CHECK_CIRCLE
                .get5xSizeWithNoSpace(),
                ContentMode.HTML);
        checkLabel.addStyleName("check-image");
        checkLabel.setSizeUndefined();
        checkLayout.addComponent(checkLabel);
        
        Label rightCheckSpaceLabel = new Label(" ");
        rightCheckSpaceLabel.setWidth("100%");
        checkLayout.addComponent(rightCheckSpaceLabel);

        checkLayout.setExpandRatio(leftCheckSpaceLabel, 1);
        checkLayout.setExpandRatio(rightCheckSpaceLabel, 1);
        
        wrapperLayout.addComponent(checkLayout);
        
        // Success text row
        final HorizontalLayout successTextLayout = new HorizontalLayout();
        successTextLayout.setWidth("100%");
        successTextLayout.setSpacing(true);
        
        Label leftSuccessSpaceLabel = new Label(" ");
        leftSuccessSpaceLabel.setWidth("100%");
        successTextLayout.addComponent(leftSuccessSpaceLabel);
        
        Label successLabel = new Label("You've successfully submitted your "
                + "coursework.");
        successLabel.setSizeUndefined();
        successTextLayout.addComponent(successLabel);
        
        Label rightSuccessSpaceLabel = new Label(" ");
        rightSuccessSpaceLabel.setWidth("100%");
        successTextLayout.addComponent(rightSuccessSpaceLabel);
        
        successTextLayout.setExpandRatio(leftSuccessSpaceLabel, 1);
        successTextLayout.setExpandRatio(rightSuccessSpaceLabel, 1);
        
        wrapperLayout.addComponent(successTextLayout);
        
        // Extra bottom space
        Label gapLabelBottom = new Label();
        gapLabelBottom.setHeight(9, Unit.EM);
        wrapperLayout.addComponent(gapLabelBottom);
        
        setContent(wrapperLayout);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
    }

}
