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
package com.github.daytron.revworks.ui.dashboard.lecturer;

import com.github.daytron.revworks.data.ErrorMsg;
import com.github.daytron.revworks.exception.SQLErrorQueryException;
import com.github.daytron.revworks.exception.SQLErrorRetrievingConnectionAndPoolException;
import com.github.daytron.revworks.exception.SQLNoResultFoundException;
import com.github.daytron.revworks.model.Class;
import com.github.daytron.revworks.service.LecturerDataProviderImpl;
import com.github.daytron.revworks.util.NotificationUtil;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ryan Gilera
 */
public class NewAnnouncementView extends VerticalLayout implements View {

    public static final String VIEW_NAME = "NewAnnouncementView";
    public static final String VIEW_CAPTION = "New Announcement";
    public static final String VIEW_TITLE = "Create Announcement";

    private boolean initialised = false;
    private List<Class> listOfClasses;
    
    public NewAnnouncementView() {
    }
    
    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        if (!initialised) {
            try {
                this.listOfClasses = LecturerDataProviderImpl.get().extractClassData();
                initView();
                initialised = true;
            } catch (SQLErrorRetrievingConnectionAndPoolException | SQLErrorQueryException | SQLNoResultFoundException ex) {
                Logger.getLogger(NewAnnouncementView.class.getName())
                        .log(Level.SEVERE, null, ex);
                NotificationUtil.showError(
                        ErrorMsg.DATA_FETCH_ERROR.getText(),
                        ErrorMsg.CONSULT_YOUR_ADMIN.getText());
            }
        }
    }
    
    private void initView() {
        setSizeFull();
        setMargin(true);
        setSpacing(true);
        
        Label viewTitleLabel = new Label(VIEW_TITLE);
        viewTitleLabel.setStyleName(ValoTheme.LABEL_H3);
        viewTitleLabel.setStyleName(ValoTheme.LABEL_BOLD);
        
        Component contentLayout = createNewAnnouncementForm();

        addComponent(viewTitleLabel);
        addComponent(contentLayout);
        
        setExpandRatio(contentLayout, 1);
    }
    
    private Component createNewAnnouncementForm() {
        FormLayout contentLayout = new FormLayout();
        
        contentLayout.setSizeFull();
        contentLayout.setMargin(true);
        contentLayout.setSpacing(true);
        
        ComboBox moduleComboBox = new ComboBox("Select class");
        moduleComboBox.setWidth(25, UNITS_EM);
        
        for (Class classItem : this.listOfClasses) {
            moduleComboBox.addItem(classItem.getModuleName());
        }
        
        moduleComboBox.setTextInputAllowed(false);
        moduleComboBox.select(this.listOfClasses.get(0).getModuleName());
        moduleComboBox.setNullSelectionAllowed(false);
        moduleComboBox.focus();
        
        
        contentLayout.addComponent(moduleComboBox);
        
        final RichTextArea richTextArea = 
                new RichTextArea("Announcement message");
        richTextArea.setWidth(35, UNITS_EM);
        contentLayout.addComponent(richTextArea);
        
        HorizontalLayout buttonBar = new HorizontalLayout();
        Button submitButton = new Button("Submit");
        Button resetButton = new Button("Reset");
        
        resetButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                richTextArea.setValue("");
                richTextArea.focus();
            }
        });
        
        buttonBar.addComponents(submitButton,resetButton);
        buttonBar.setSpacing(true);
        
        contentLayout.addComponent(buttonBar);
        
        return  contentLayout;
        
    }
    
}
