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

import com.github.daytron.revworks.MainUI;
import com.github.daytron.revworks.data.ErrorMsg;
import com.github.daytron.revworks.exception.SQLErrorQueryException;
import com.github.daytron.revworks.exception.SQLErrorRetrievingConnectionAndPoolException;
import com.github.daytron.revworks.exception.SQLNoResultFoundException;
import com.github.daytron.revworks.model.Coursework;
import com.github.daytron.revworks.presenter.LecturerNameColumnGenerator;
import com.github.daytron.revworks.presenter.LocalDateTimeColumnGenerator;
import com.github.daytron.revworks.presenter.ModuleIDColumnGenerator;
import com.github.daytron.revworks.presenter.ModuleNameColumnGenerator;
import com.github.daytron.revworks.service.StudentDataProviderImpl;
import com.github.daytron.revworks.util.NotificationUtil;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.themes.ValoTheme;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ryan Gilera
 */
@SuppressWarnings("serial")
public class StudentCourseworkModuleView extends VerticalLayout implements View {

    public static final String VIEW_NAME = "CourseworkModuleView";
    public static final String VIEW_CAPTION = "My Courseworks";
    public static final String VIEW_TITLE = "My Submitted Courseworks";

    private boolean isInitialised = false;
    private BeanItemContainer<Coursework> courseworksContainer;

    public StudentCourseworkModuleView() {
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        if (!isInitialised) {
            try {
                this.courseworksContainer = StudentDataProviderImpl.get()
                        .extractCourseworkData();
                initView();
                isInitialised = true;
            } catch (SQLErrorRetrievingConnectionAndPoolException | SQLErrorQueryException | SQLNoResultFoundException | IOException ex) {
                Logger.getLogger(StudentCourseworkModuleView.class.getName()).log(Level.SEVERE, null, ex);
                NotificationUtil.showError(
                        ErrorMsg.DATA_FETCH_ERROR.getText(),
                        ErrorMsg.CONSULT_YOUR_ADMIN.getText());
            }
        }
    }

    private void initView() {
        setWidth("100%");
        setMargin(true);
        setSpacing(true);
        
        final CssLayout wrapperItem = new CssLayout();
        wrapperItem.setWidth("100%");
        wrapperItem.setStyleName(ValoTheme.LAYOUT_CARD);
        wrapperItem.addComponent(createPanelHeader());
        
        VerticalLayout contentLayout = new VerticalLayout();
        contentLayout.setSizeFull();
        contentLayout.setMargin(true);
        contentLayout.setSpacing(true);
        
        Table assignmentTable = createSubmittedCourseworkTable();
        contentLayout.addComponent(assignmentTable);
        Label footerTableLabel = new Label();
        
        int items = courseworksContainer.size();
        if (items < 1) {
            footerTableLabel.setValue("No coursework submitted yet.");
        } else {
            if (items == 1) {
                footerTableLabel.setValue(courseworksContainer.size() + " item found.");
            } else {
                footerTableLabel.setValue(courseworksContainer.size() + " items found.");
            }
        }
        
        contentLayout.addComponent(footerTableLabel);
        contentLayout.setExpandRatio(assignmentTable, 1);
        wrapperItem.addComponent(contentLayout);
        
        addComponent(wrapperItem);
        
        // Set default table item selection to the first row if not empty
        assignmentTable.select(assignmentTable.firstItemId());
        
    }
    
    public Table createSubmittedCourseworkTable() {
        final Table courseworksTable = new Table("",courseworksContainer);
        courseworksTable.setEditable(false);
        courseworksTable.setSelectable(true);
        courseworksTable.setWidth("100%");
        
        // Set column data properties and names
        courseworksTable.setColumnHeader("id", "ID");
        courseworksTable.setColumnHeader("title", "Title");
        courseworksTable.addGeneratedColumn("dateSubmitted", 
                new LocalDateTimeColumnGenerator());
        courseworksTable.setColumnHeader("dateSubmitted", "Date Submitted");
        
        courseworksTable.addGeneratedColumn("moduleId", 
                new ModuleIDColumnGenerator());
        courseworksTable.setColumnHeader("moduleId", "Module ID");
        
        courseworksTable.addGeneratedColumn("moduleName", 
                new ModuleNameColumnGenerator());
        courseworksTable.setColumnHeader("moduleName", "Module");
        
        courseworksTable.addGeneratedColumn("lecturer", 
                new LecturerNameColumnGenerator());
        courseworksTable.setColumnHeader("lecturer", "Lecturer");
        
        // Arrange columns order
        courseworksTable.setVisibleColumns((Object[]) new String[]{"id", 
            "title","dateSubmitted","moduleId", "moduleName","lecturer"});
        // Set column alignment
        courseworksTable.setColumnAlignments(new Table.Align[] {Table.ALIGN_LEFT, 
            Table.Align.CENTER, Table.Align.CENTER, Table.Align.CENTER, 
            Table.Align.CENTER, Table.Align.CENTER});
        
        // Sort by date and module id
        courseworksTable.setSortEnabled(true);
        courseworksTable.sort(new String[]{"dateSubmitted","moduleId"},
                new boolean[]{true,true});
        
        courseworksTable.setColumnWidth("id", 50);
        courseworksTable.setPageLength(8);
      
        return courseworksTable;
        
    }
    

    public HorizontalLayout createPanelHeader() {
        final HorizontalLayout layoutHeader = new HorizontalLayout();
        layoutHeader.addStyleName("v-panel-caption");
        layoutHeader.setWidth("100%");
        layoutHeader.setSpacing(true);

        Label titleLabel = new Label(VIEW_TITLE);
        titleLabel.setStyleName(ValoTheme.LABEL_BOLD);
        titleLabel.setSizeFull();
        layoutHeader.addComponent(titleLabel);

        Button openFileButton = new Button("View Coursework");
        openFileButton.setStyleName(ValoTheme.BUTTON_SMALL);
        layoutHeader.addComponent(openFileButton);

        Button submitButton = new Button("Submit Coursework");
        submitButton.setStyleName(ValoTheme.BUTTON_SMALL);
        submitButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                MainUI.get().getNavigator().navigateTo(
                    StudentSubmitCourseworkView.VIEW_NAME);
            }
        });
        layoutHeader.addComponent(submitButton);

        layoutHeader.setExpandRatio(titleLabel, 1);
        return layoutHeader;
    }

}
