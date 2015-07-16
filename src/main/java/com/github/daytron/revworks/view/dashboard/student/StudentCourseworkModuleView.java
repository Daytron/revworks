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
package com.github.daytron.revworks.view.dashboard.student;

import com.github.daytron.revworks.MainUI;
import com.github.daytron.revworks.data.ErrorMsg;
import com.github.daytron.revworks.event.AppEvent;
import com.github.daytron.revworks.event.AppEventBus;
import com.github.daytron.revworks.exception.SQLErrorQueryException;
import com.github.daytron.revworks.exception.SQLErrorRetrievingConnectionAndPoolException;
import com.github.daytron.revworks.exception.SQLNoResultFoundException;
import com.github.daytron.revworks.model.Coursework;
import com.github.daytron.revworks.presenter.LecturerNameColumnGenerator;
import com.github.daytron.revworks.presenter.LocalDateTimeColumnGenerator;
import com.github.daytron.revworks.presenter.ModuleIDColumnGenerator;
import com.github.daytron.revworks.presenter.ModuleNameColumnGenerator;
import com.github.daytron.revworks.service.CurrentUserSession;
import com.github.daytron.revworks.util.NotificationUtil;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
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
    private Coursework selectedCoursework;

    public StudentCourseworkModuleView() {
        // init empty by default
        this.selectedCoursework = null;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        // Important!!
        // shutdown and cleanup any previous threads created
        // from coursework view and comment component if possible
        CurrentUserSession.shutdownCourseworkViewExecutorService();
        CurrentUserSession.shutdownCommentExectorService();
        
        if (!isInitialised) {
            try {
                this.courseworksContainer = MainUI.get().getStudentDataProvider()
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
        wrapperItem.addStyleName("announcement-wrapper-custom");
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
                footerTableLabel.setValue(courseworksContainer.size()
                        + " item found.");
            } else {
                footerTableLabel.setValue(courseworksContainer.size()
                        + " items found.");
            }

            // Set default table item selection to the first row if not empty
            assignmentTable.select(assignmentTable.firstItemId());
            // Extract the bean (Coursework) and save it
            BeanItem<Coursework> item1 = (BeanItem) assignmentTable
                    .getItem(assignmentTable.getValue());
            Coursework selectedBean = item1.getBean();
            selectedCoursework = selectedBean;
        }

        contentLayout.addComponent(footerTableLabel);
        contentLayout.setExpandRatio(assignmentTable, 1);
        wrapperItem.addComponent(contentLayout);

        addComponent(wrapperItem);

    }

    public Table createSubmittedCourseworkTable() {
        final Table courseworksTable = new Table("", courseworksContainer);
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
        
        courseworksTable.setColumnHeader("readStudent", "Read");

        // Arrange columns order
        courseworksTable.setVisibleColumns((Object[]) new String[]{"id",
            "title", "dateSubmitted", "moduleId", "moduleName", "lecturer"});
        // Set column alignment
        courseworksTable.setColumnAlignments(new Table.Align[]{Table.ALIGN_LEFT,
            Table.Align.CENTER, Table.Align.CENTER, Table.Align.CENTER,
            Table.Align.CENTER, Table.Align.CENTER});
        
        courseworksTable.setCellStyleGenerator(new Table.CellStyleGenerator() {

            @Override
            public String getStyle(Table source, Object itemId, Object propertyId) {
                if (propertyId == null) {
                    Item item = source.getItem(itemId);
                    
                    Property<Boolean> isRead = item.getItemProperty("readStudent");
                    boolean isReadStudent = isRead.getValue();
                    
                    // If it is unread then make text bolder
                    if (!isReadStudent) {
                        return "unread";
                    } else {
                        return "read";
                    }
                } else {
                    return null;
                }
            }
        });

        // Sort by date and module id
        courseworksTable.setSortEnabled(true);
        courseworksTable.setSortContainerPropertyId("dateSubmitted");
        courseworksTable.setSortAscending(false);

        courseworksTable.setColumnWidth("id", 50);
        courseworksTable.setPageLength(8);

        courseworksTable.setImmediate(true);
        courseworksTable.addItemClickListener(new ItemClickEvent.ItemClickListener() {

            @Override
            public void itemClick(ItemClickEvent event) {
                // Save the selected bean
                BeanItem<Coursework> item = (BeanItem) event.getItem();
                Coursework selectedBean = item.getBean();
                selectedCoursework = selectedBean;
            }
        });

        return courseworksTable;

    }

    public HorizontalLayout createPanelHeader() {
        final HorizontalLayout layoutHeader = new HorizontalLayout();
        layoutHeader.addStyleName("v-panel-caption");
        layoutHeader.setWidth("100%");
        layoutHeader.setSpacing(true);
        layoutHeader.setMargin(true);
        layoutHeader.addStyleName("announcement-header");

        Label titleLabel = new Label(VIEW_TITLE);
        titleLabel.setStyleName(ValoTheme.LABEL_BOLD);
        titleLabel.setSizeFull();
        layoutHeader.addComponent(titleLabel);

        Button openFileButton = new Button("View Coursework");
        openFileButton.setDescription("Opens the selected coursework");
        openFileButton.setStyleName(ValoTheme.BUTTON_SMALL);
        openFileButton.setDisableOnClick(true);
        openFileButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (courseworksContainer.size() < 1 
                            || selectedCoursework == null) {
                        NotificationUtil.showInformation(
                                "No table item selected.", 
                                "No item found.");
                        event.getButton().setEnabled(true);
                    } else {
                        AppEventBus.post(new AppEvent
                                .StudentViewCourseworkEvent(selectedCoursework));
                    }
            }
        });
        layoutHeader.addComponent(openFileButton);

        Button submitButton = new Button("Submit New Coursework");
        submitButton.setDescription("Submit a new coursework. Opens a new page.");
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
