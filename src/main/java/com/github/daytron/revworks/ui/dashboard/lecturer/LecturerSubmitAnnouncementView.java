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

import com.github.daytron.revworks.event.AppEvent;
import com.github.daytron.revworks.event.AppEventBus;
import com.github.daytron.revworks.model.ClassTable;
import com.github.daytron.revworks.service.CurrentUserSession;
import com.github.daytron.revworks.util.NotificationUtil;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The view class for creating new announcement for lecturers.
 *
 * @author Ryan Gilera
 */
@SuppressWarnings("serial")
public class LecturerSubmitAnnouncementView extends VerticalLayout implements View {

    public static final String VIEW_NAME = "NewAnnouncementView";
    public static final String VIEW_CAPTION = "New Announcement";
    public static final String VIEW_TITLE = "Create Announcement";

    private boolean isInitialised = false;
    private CopyOnWriteArrayList<ClassTable> listOfClassTables;

    public LecturerSubmitAnnouncementView() {
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        if (!isInitialised) {
            this.listOfClassTables = CurrentUserSession.getCurrentClassTables();
            initView();
            isInitialised = true;
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

        final ComboBox moduleComboBox = new ComboBox("Select class");
        moduleComboBox.setWidth(25, UNITS_EM);

        for (ClassTable classItem : this.listOfClassTables) {
            moduleComboBox.addItem(classItem.getModuleName());
        }

        // Module selection
        moduleComboBox.setTextInputAllowed(false);

        moduleComboBox.setNullSelectionAllowed(false);
        moduleComboBox.focus();

        contentLayout.addComponent(moduleComboBox);

        if (!this.listOfClassTables.isEmpty()) {
            moduleComboBox.select(this.listOfClassTables.get(0).getModuleName());
        } else {
            Label warningNoClassLabel = new Label("No class found.");
            warningNoClassLabel.addStyleName(ValoTheme.LABEL_FAILURE);
            contentLayout.addComponent(warningNoClassLabel);
        }

        // Title area
        final TextField titleTextField = new TextField("Title");
        titleTextField.setWidth(25, UNITS_EM);
        // As define in database constraints
        titleTextField.setMaxLength(45);
        contentLayout.addComponent(titleTextField);

        // Message area
        final RichTextArea richTextArea
                = new RichTextArea("Announcement message");
        richTextArea.setWidth(35, UNITS_EM);
        contentLayout.addComponent(richTextArea);

        HorizontalLayout buttonBar = new HorizontalLayout();
        Button submitButton = new Button("Submit");
        Button resetButton = new Button("Reset");

        final List<ClassTable> copyOfClassTables = this.listOfClassTables;
        submitButton.setDisableOnClick(true);
        submitButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                Button button = event.getButton();
                if (moduleComboBox.getValue() == null) {
                    NotificationUtil.showError("No class selected");
                    button.setEnabled(true);
                    return;
                }

                if (titleTextField.getValue() == null
                        || titleTextField.getValue().isEmpty()) {
                    NotificationUtil.showError("Empty title field.");
                    button.setEnabled(true);
                    return;
                }

                if (richTextArea.getValue() == null
                        || richTextArea.getValue().isEmpty()) {
                    NotificationUtil.showError("Empty message field.");
                    button.setEnabled(true);
                    return;
                }

                ClassTable selectedClass = null;
                for (ClassTable classTable : copyOfClassTables) {
                    if (classTable.getModuleName().equalsIgnoreCase(
                            (String) moduleComboBox.getValue())) {
                        selectedClass = classTable;
                    }
                }

                if (selectedClass == null) {
                    NotificationUtil.showError("Something went wrong.",
                            "Could not find matching class.");
                    button.setEnabled(true);
                    return;
                }

                AppEventBus.post(
                        new AppEvent.LecturerSubmitNewAnnouncementEvent(
                                selectedClass, titleTextField,
                                richTextArea,button));
            }
        });

        resetButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                richTextArea.setValue("");
                richTextArea.focus();
            }
        });

        buttonBar.addComponents(submitButton, resetButton);
        buttonBar.setSpacing(true);

        contentLayout.addComponent(buttonBar);

        return contentLayout;

    }

}
