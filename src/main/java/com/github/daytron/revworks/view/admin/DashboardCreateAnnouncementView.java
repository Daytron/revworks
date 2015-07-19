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

import com.github.daytron.revworks.data.FontAwesomeIcon;
import com.github.daytron.revworks.util.NotificationUtil;
import com.vaadin.data.Property;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
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

/**
 *
 * @author Ryan Gilera
 */
public final class DashboardCreateAnnouncementView extends VerticalLayout implements View {

    public final static String VIEW_NAME = "CREATE_ANNOUNCEMENT";
    public final static String VIEW_CAPTION = "Announce";
    public final static String VIEW_TITLE = "Create Announcement";

    private boolean isInitialised = false;
    
    private Label previewContent;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        if (!isInitialised) {
            this.previewContent = new Label("&nbsp;");
            
            initView();
            
            isInitialised = true;
        }
    }

    private void initView() {
        setWidth("100%");
        setHeightUndefined();
        setMargin(true);
        setSpacing(true);

        Label title = new Label(VIEW_TITLE);
        title.addStyleName(ValoTheme.LABEL_H1);
        addComponent(title);

        Component contentComponent = createContent();
        addComponent(contentComponent);

        setExpandRatio(contentComponent, 1);

    }

    private Component createContent() {
        HorizontalLayout horizontalLayout
                = new HorizontalLayout();
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
        wrapperItem.addStyleName("message-preview-wrapper");

        // Create Header
        final HorizontalLayout layoutHeader = new HorizontalLayout();
        layoutHeader.addStyleName("v-panel-caption");
        layoutHeader.addStyleName("message-preview-header");
        layoutHeader.setWidth("100%");

        Label titleLabel = new Label(
                FontAwesomeIcon.DESKTOP.getLgSize()
                + "Message Preview");
        titleLabel.setContentMode(ContentMode.HTML);
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

        submitButton.setDisableOnClick(true);
        submitButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                Button button = event.getButton();

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

//                AppEventBus.post(
//                        new AppEvent.LecturerSubmitNewAnnouncementEvent(
//                                selectedClass, titleTextField,
//                                richTextArea,button));
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
