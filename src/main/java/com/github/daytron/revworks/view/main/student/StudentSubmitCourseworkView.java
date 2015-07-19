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

import com.github.daytron.revworks.data.ErrorMsg;
import com.github.daytron.revworks.data.FontAwesomeIcon;
import com.github.daytron.revworks.event.AppEvent;
import com.github.daytron.revworks.event.AppEventBus;
import com.github.daytron.revworks.model.ClassTable;
import com.github.daytron.revworks.service.CurrentUserSession;
import com.github.daytron.revworks.service.FileUploadReceiver;
import com.github.daytron.revworks.util.NotificationUtil;
import com.github.daytron.revworks.util.StringUtil;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import static com.vaadin.server.Sizeable.UNITS_EM;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A view for submitting coursework for students.
 *
 * @author Ryan Gilera
 */
@SuppressWarnings("serial")
public class StudentSubmitCourseworkView extends Panel
        implements View {

    public static final String VIEW_NAME = "SubmitCourseworkView";
    public static final String VIEW_CAPTION = "Submit Coursework";
    public static final String VIEW_TITLE = "Submit Your Coursework";

    private boolean isInitialised = false;
    private CopyOnWriteArrayList<ClassTable> listOfClasses;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        // Important!!
        // shutdown and cleanup any previous threads created
        // from coursework view and comment component if possible
        CurrentUserSession.shutdownCourseworkViewExecutorService();
        CurrentUserSession.shutdownCommentExectorService();

        if (!isInitialised) {
            this.listOfClasses = CurrentUserSession.getCurrentClassTables();
            initView();
            isInitialised = true;
        }
    }

    private void initView() {
        setSizeFull();
        
        VerticalLayout wrapperLayout = new VerticalLayout();
        wrapperLayout.setWidth("100%");
        wrapperLayout.setHeightUndefined();
        wrapperLayout.setMargin(true);

        Label viewTitleLabel = new Label(VIEW_TITLE);
        viewTitleLabel.setStyleName(ValoTheme.LABEL_H2);
        viewTitleLabel.addStyleName(ValoTheme.LABEL_BOLD);

        wrapperLayout.addComponent(viewTitleLabel);
        
        // Content
        HorizontalLayout wrapperInnerLayout = new HorizontalLayout();
        wrapperInnerLayout.setWidth("100%");
        wrapperInnerLayout.setHeightUndefined();
        
        wrapperInnerLayout.setSpacing(true);
        
        Component contentleftLayout = createSubmitCourseworkForm();
        wrapperInnerLayout.addComponent(contentleftLayout);
        
        Component contentRightLayout = createRightContentSideNote();
        wrapperInnerLayout.addComponent(contentRightLayout);
        
        wrapperInnerLayout.setExpandRatio(contentleftLayout, 7);
        wrapperInnerLayout.setExpandRatio(contentRightLayout, 3);

        
        wrapperLayout.addComponent(wrapperInnerLayout);
        wrapperLayout.setExpandRatio(wrapperInnerLayout, 1);
        
        setContent(wrapperLayout);
    }
    
    private Component createRightContentSideNote() {
        VerticalLayout sideNoteLayout = new VerticalLayout();
        setSizeFull();
        
        sideNoteLayout.setStyleName(ValoTheme.LAYOUT_CARD);
        sideNoteLayout.addStyleName("student-submit-coursework-side-note-wrapper");
        
        // Create Header
        final HorizontalLayout layoutHeader = new HorizontalLayout();
        layoutHeader.addStyleName("v-panel-caption");
        layoutHeader.addStyleName("student-submit-coursework-side-note-header");
        layoutHeader.setWidth("100%");

        Label titleLabel = new Label(
                FontAwesomeIcon.EXCLAMATION_TRIANGLE.getLgSize() + 
                        "Important Note");
        titleLabel.setContentMode(ContentMode.HTML);
        titleLabel.setStyleName(ValoTheme.LABEL_BOLD);
        titleLabel.setSizeFull();
        layoutHeader.addComponent(titleLabel);
        
        sideNoteLayout.addComponent(layoutHeader);
        
        // Content
        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setSpacing(true);
        content.setMargin(true);
        content.addStyleName("student-submit-coursework-side-note-content");
        
        Label note1 = new Label();
        note1.setValue("The only allowed file format is PDF. The PDF file format "
                + "is the defacto document standard "
                + "that is widely compatible to all operating system platforms "
                + "and web browsers.");
        content.addComponent(note1);
        
        Label note2 = new Label();
        note2.setContentMode(ContentMode.HTML);
        note2.setValue("There are multiple ways to convert your "
                + "document to PDF file format. If you're using Microsoft's "
                + "Office Word or LibreOffice Writer, you can easily convert "
                + "the document by saving it "
                + "as PDF (SAVE AS > PDF). Alternatively, there are lots of "
                + "online document PDF converter out there, you may start your "
                + "search <a href=\"http://lmgtfy.com/?q=convert+to+pdf+online\">here"
                + "</a>.");
        content.addComponent(note2);
        
        content.setExpandRatio(note2, 1);
        
        sideNoteLayout.addComponent(content);
        
        sideNoteLayout.setExpandRatio(content, 1);
        
        return sideNoteLayout;
    }

    private Component createSubmitCourseworkForm() {
        VerticalLayout layout = new VerticalLayout();

        FormLayout contentFormLayout = new FormLayout();

        contentFormLayout.setSizeFull();
        contentFormLayout.setMargin(true);
        contentFormLayout.setSpacing(true);

        final ComboBox moduleComboBox = new ComboBox("Class");
        moduleComboBox.setWidth(25, UNITS_EM);

        for (ClassTable classItem : this.listOfClasses) {
            moduleComboBox.addItem(classItem.getModuleName());
        }

        // Module selection
        moduleComboBox.setTextInputAllowed(false);
        moduleComboBox.select(this.listOfClasses.get(0).getModuleName());
        moduleComboBox.setNullSelectionAllowed(false);
        moduleComboBox.focus();

        contentFormLayout.addComponent(moduleComboBox);

        // Title area
        final TextField titleTextField = new TextField("Title");
        titleTextField.setWidth(25, Unit.EM);
        // As define in database constraints
        titleTextField.setMaxLength(150);
        contentFormLayout.addComponent(titleTextField);

        Label gapLabel = new Label("&nbsp;", ContentMode.HTML);
        gapLabel.setHeight(10, Unit.PIXELS);
        contentFormLayout.addComponent(gapLabel);

        // Info label
//        Label uploadInformationLabel = new Label();
//        uploadInformationLabel.setValue("Upload your coursework PDF file here.");
//        contentFormLayout.addComponent(uploadInformationLabel);
        // Upload area
        ProgressBar progressBar = new ProgressBar(0.0f);
        progressBar.setWidth("50%");
        progressBar.setVisible(false);
        final FileUploadReceiver fileUploadReceiver
                = new FileUploadReceiver(progressBar);
        Upload fileUploader = new Upload("PDF file to upload", fileUploadReceiver);
        fileUploadReceiver.setUploader(fileUploader);
        fileUploader.addProgressListener(fileUploadReceiver);
        fileUploader.addFinishedListener(fileUploadReceiver);
        fileUploader.addStartedListener(fileUploadReceiver);
        fileUploader.addSucceededListener(fileUploadReceiver);
        fileUploader.addFailedListener(fileUploadReceiver);
        contentFormLayout.addComponent(fileUploader);
        contentFormLayout.addComponent(progressBar);

        Label gapLabel2 = new Label("&nbsp;", ContentMode.HTML);
        gapLabel2.setHeight(10, Unit.PIXELS);
        contentFormLayout.addComponent(gapLabel2);

        Label termsLabel = new Label("By ticking the checkbox below, you agree "
                + "to the terms and conditions and privacy policy enforced on this "
                + "site.");
        contentFormLayout.addComponent(termsLabel);

        final CheckBox agreeBox
                = new CheckBox("I agree to the terms and conditions.");
        contentFormLayout.addComponent(agreeBox);

        Label gapLabel3 = new Label("&nbsp;", ContentMode.HTML);
        gapLabel3.setHeight(5, Unit.PIXELS);
        contentFormLayout.addComponent(gapLabel3);

        final List<ClassTable> copyOfClassTables = this.listOfClasses;
        Button submitButton = new Button("Submit");
        submitButton.setDescription("Submit the coursework.");
        submitButton.setDisableOnClick(true);
        submitButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                String newFormattedTitle = null;

                if (titleTextField.getValue().isEmpty()
                        || titleTextField.getValue() == null) {
                    NotificationUtil.showError(
                            ErrorMsg.EMPTY_TITLE_FIELD.getText());
                    event.getButton().setEnabled(true);
                    return;
                } else {
                    newFormattedTitle = StringUtil
                            .capitalizeFirstLetterEachWord(
                                    titleTextField.getValue());
                }

                if (!fileUploadReceiver.isUploaded()) {
                    NotificationUtil.showError(
                            ErrorMsg.FILE_IS_NOT_YET_UPLOADED.getText());
                    event.getButton().setEnabled(true);
                    return;
                }

                if (!agreeBox.getValue()) {
                    NotificationUtil.showError(
                            ErrorMsg.TERMS_NOT_ACCEPTED.getText());
                    event.getButton().setEnabled(true);
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
                    event.getButton().setEnabled(true);
                    return;
                }

                AppEventBus.post(new AppEvent.StudentSubmitCourseworkEvent(
                        fileUploadReceiver.getFileUploaded(),
                        selectedClass, newFormattedTitle));

            }
        });
        contentFormLayout.addComponent(submitButton);

        layout.addComponent(contentFormLayout);
        layout.setWidth("100%");
        layout.setSpacing(true);

        return layout;

    }
}
