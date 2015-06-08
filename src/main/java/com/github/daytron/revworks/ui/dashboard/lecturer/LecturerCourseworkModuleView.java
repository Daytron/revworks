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

import com.github.daytron.revworks.ui.dashboard.student.*;
import com.github.daytron.revworks.MainUI;
import com.github.daytron.revworks.data.ErrorMsg;
import com.github.daytron.revworks.exception.NoClassAttachedToLecturerException;
import com.github.daytron.revworks.exception.SQLErrorQueryException;
import com.github.daytron.revworks.exception.SQLErrorRetrievingConnectionAndPoolException;
import com.github.daytron.revworks.model.ClassTable;
import com.github.daytron.revworks.model.Coursework;
import com.github.daytron.revworks.presenter.LecturerNameColumnGenerator;
import com.github.daytron.revworks.presenter.LocalDateTimeColumnGenerator;
import com.github.daytron.revworks.presenter.StudentIdColumnGenerator;
import com.github.daytron.revworks.presenter.StudentNameColumnGenerator;
import com.github.daytron.revworks.service.LecturerDataProviderImpl;
import com.github.daytron.revworks.util.NotificationUtil;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.themes.ValoTheme;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ryan Gilera
 */
@SuppressWarnings("serial")
public class LecturerCourseworkModuleView extends VerticalLayout implements View {

    public static final String VIEW_NAME = "CourseworkModuleView";
    public static final String VIEW_CAPTION = "View Courseworks";
    public static final String VIEW_TITLE = "Students Submitted Courseworks";

    private boolean isInitialised = false;
    private ConcurrentHashMap<ClassTable, BeanItemContainer> listOfNBeanItemContainers;
    private final TabSheet tabSheet;

    public LecturerCourseworkModuleView() {
        this.tabSheet = new TabSheet();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        if (!isInitialised) {
            try {
                this.listOfNBeanItemContainers
                        = LecturerDataProviderImpl.get().extractCourseworkData();
                initView();
                isInitialised = true;

            } catch (NoClassAttachedToLecturerException ex) {
                Logger.getLogger(LecturerCourseworkModuleView.class.getName()).log(Level.SEVERE, null, ex);
                NotificationUtil.showError(
                        ErrorMsg.LECTURER_NO_CLASS_FOUND.getText(),
                        ErrorMsg.CONSULT_YOUR_ADMIN.getText());
            } catch (SQLErrorRetrievingConnectionAndPoolException | SQLErrorQueryException ex) {
                Logger.getLogger(LecturerCourseworkModuleView.class.getName()).log(Level.SEVERE, null, ex);
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

        if (this.listOfNBeanItemContainers.isEmpty()) {
            Label emptyNoticeLabel = new Label("No modules registered found.\n"
                    + "Please seek the administrator for more information.");
            emptyNoticeLabel.setStyleName(ValoTheme.LABEL_FAILURE);
            addComponent(emptyNoticeLabel);
        } else {
            addComponent(tabSheet);

            for (Map.Entry<ClassTable, BeanItemContainer> entry : 
                    this.listOfNBeanItemContainers.entrySet()) {

                tabSheet.addTab(createNewTab(entry.getValue(),
                        entry.getKey().getModuleName()),
                        entry.getKey().getModuleId());
            }
        }

    }

    public CssLayout createNewTab(BeanItemContainer beanItemContainer, String 
            moduleNameAsTableName) {
        final CssLayout wrapperItem = new CssLayout();
        wrapperItem.setWidth("100%");
        wrapperItem.setStyleName(ValoTheme.LAYOUT_CARD);
        wrapperItem.addComponent(createPanelHeader());

        VerticalLayout contentLayout = new VerticalLayout();
        contentLayout.setSizeFull();
        contentLayout.setMargin(true);
        contentLayout.setSpacing(true);

        Table assignmentTable = createSubmittedCourseworkTable(beanItemContainer, 
                moduleNameAsTableName);
        contentLayout.addComponent(assignmentTable);
        Label footerTableLabel = new Label();

        int items = beanItemContainer.size();
        if (items < 1) {
            footerTableLabel.setValue("No coursework submitted yet.");
        } else {
            if (items == 1) {
                footerTableLabel.setValue(listOfNBeanItemContainers.size() + " item found.");
            } else {
                footerTableLabel.setValue(listOfNBeanItemContainers.size() + " items found.");
            }
        }

        // Set default table item selection to the first row if not empty
        assignmentTable.select(assignmentTable.firstItemId());

        contentLayout.addComponent(footerTableLabel);
        contentLayout.setExpandRatio(assignmentTable, 1);
        wrapperItem.addComponent(contentLayout);

        return wrapperItem;
    }

    public Table createSubmittedCourseworkTable(BeanItemContainer beanItemContainer, 
            String moduleNameAsTableName) {

        final Table courseworksTable
                = new Table(moduleNameAsTableName, beanItemContainer);
        courseworksTable.setEditable(false);
        courseworksTable.setSelectable(true);
        courseworksTable.setWidth("100%");

        // Set column data properties and names
        courseworksTable.setColumnHeader("id", "ID");

        courseworksTable.addGeneratedColumn("studentId",
                new StudentIdColumnGenerator());
        courseworksTable.setColumnHeader("studentId", "Student ID");

        courseworksTable.addGeneratedColumn("studentName",
                new StudentNameColumnGenerator());
        courseworksTable.setColumnHeader("studentName", "Student");

        courseworksTable.setColumnHeader("title", "Title");
        courseworksTable.addGeneratedColumn("dateSubmitted",
                new LocalDateTimeColumnGenerator());
        courseworksTable.setColumnHeader("dateSubmitted", "Date Submitted");

        // Arrange columns order
        courseworksTable.setVisibleColumns((Object[]) new String[]{"id",
            "studentId", "studentName", "title", "dateSubmitted"});
        // Set column alignment
        courseworksTable.setColumnAlignments(new Table.Align[]{Table.ALIGN_LEFT,
            Table.Align.CENTER, Table.Align.CENTER, Table.Align.CENTER,
            Table.Align.CENTER});

        // Sort by date and module id
        courseworksTable.setSortEnabled(true);
        courseworksTable.sort(new String[]{"dateSubmitted"},
                new boolean[]{true});

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

        layoutHeader.setExpandRatio(titleLabel, 1);
        return layoutHeader;
    }

}
