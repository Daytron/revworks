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
package com.github.daytron.revworks.view.dashboard.lecturer;

import com.github.daytron.revworks.event.AppEvent;
import com.github.daytron.revworks.event.AppEventBus;
import com.github.daytron.revworks.model.ClassTable;
import com.github.daytron.revworks.service.CurrentUserSession;
import com.github.daytron.revworks.util.NotificationUtil;
import com.vaadin.data.Property;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
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
    
    private Label previewContent;

    public LecturerSubmitAnnouncementView() {
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        // Important!!
        // shutdown and cleanup any previous threads created
        // from coursework view and comment component if possible
        CurrentUserSession.shutdownCourseworkViewExecutorService();
        CurrentUserSession.shutdownCommentExectorService();
        
        if (!isInitialised) {
            this.listOfClassTables = CurrentUserSession.getCurrentClassTables();
            
            this.previewContent = new Label("&nbsp;");
            initView();
            isInitialised = true;
        }
    }

    private void initView() {
        setSizeFull();
        setMargin(true);
        setSpacing(true);

        Label viewTitleLabel = new Label(VIEW_TITLE);
        viewTitleLabel.setStyleName(ValoTheme.LABEL_H2);
        viewTitleLabel.addStyleName(ValoTheme.LABEL_BOLD);

        Component contentLayout = createNewAnnouncementForm();

        addComponent(viewTitleLabel);
        addComponent(contentLayout);

        setExpandRatio(contentLayout, 1);
    }

    private Component createNewAnnouncementForm() {
        HorizontalLayout horizontalLayout = 
                new HorizontalLayout();
        horizontalLayout.setWidth("100%");
        
        FormLayout leftLayout = createEditorComponent();
        horizontalLayout.addComponent(leftLayout);
        
        CssLayout rightLayout = createPreviewSection();
        horizontalLayout.addComponent(rightLayout);
        
        horizontalLayout.setExpandRatio(leftLayout, 6);
        horizontalLayout.setExpandRatio(rightLayout, 4);
        
        return horizontalLayout;
    }
    
    private CssLayout createPreviewSection() {
        final CssLayout wrapperItem = new CssLayout();
        wrapperItem.setSizeFull();
        wrapperItem.setStyleName(ValoTheme.LAYOUT_CARD);
        
        // Create Header
        final HorizontalLayout layoutHeader = new HorizontalLayout();
        layoutHeader.addStyleName("v-panel-caption");
        layoutHeader.setWidth("100%");

        Label titleLabel = new Label("Message Preview");
        titleLabel.setStyleName(ValoTheme.LABEL_BOLD);
        titleLabel.setSizeFull();
        layoutHeader.addComponent(titleLabel);
        
        wrapperItem.addComponent(layoutHeader);
             
        // Preview area
        Panel previewPanel = new Panel();
        previewPanel.setWidth("100%");
        previewPanel.setHeight(25, Unit.EM);
        
        previewContent.setContentMode(ContentMode.HTML);
        previewContent.setWidth("100%");
        previewContent.setHeightUndefined();
        previewContent.addStyleName("label-preview");
        
        previewPanel.setContent(previewContent);
        
        wrapperItem.addComponent(previewPanel);
        
        return wrapperItem;
    }
    
    private FormLayout createEditorComponent() {
        FormLayout contentLayout = new FormLayout();

        contentLayout.setSizeFull();
        contentLayout.setMargin(true);
        contentLayout.setSpacing(true);

        final ComboBox moduleComboBox = new ComboBox("Class");
        moduleComboBox.setWidth("100%");

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
        titleTextField.setWidth("100%");
        // As define in database constraints
        titleTextField.setMaxLength(45);
        contentLayout.addComponent(titleTextField);

        // Message area
        final RichTextArea richTextArea
                = new RichTextArea("Message");
        richTextArea.setWidth("100%");
        richTextArea.setHeight(20, Unit.EM);
        
        richTextArea.setImmediate(true);
        contentLayout.addComponent(richTextArea);
        
        richTextArea.addValueChangeListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                previewContent.setValue(richTextArea.getValue());
            }
        });

        // Create buttons
        HorizontalLayout buttonBar = new HorizontalLayout();
        Button submitButton = new Button("Submit");
        submitButton.setDescription("Submits the announcement");
        Button resetButton = new Button("Reset");
        resetButton.setDescription("Clears text area editor");
        Button previewButton = new Button("Preview");
        previewButton.setDescription("Shows/Updates the message preview");

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
        
        previewButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                previewContent.setValue(richTextArea.getValue());
            }
        });

        // Add extra space bewteen text area and button bar
        Label spaceLabel = new Label("&nbsp;",ContentMode.HTML);
        spaceLabel.setHeight(10, Unit.PIXELS);
        contentLayout.addComponent(spaceLabel);
        
        buttonBar.addComponents(submitButton, resetButton, previewButton);
        buttonBar.setSpacing(true);

        contentLayout.addComponent(buttonBar);

        return contentLayout;
    }

}
