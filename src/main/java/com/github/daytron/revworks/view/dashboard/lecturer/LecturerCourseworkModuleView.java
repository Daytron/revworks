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

import com.github.daytron.revworks.MainUI;
import com.github.daytron.revworks.data.ErrorMsg;
import com.github.daytron.revworks.event.AppEvent;
import com.github.daytron.revworks.event.AppEventBus;
import com.github.daytron.revworks.exception.NoClassAttachedToLecturerException;
import com.github.daytron.revworks.exception.SQLErrorQueryException;
import com.github.daytron.revworks.exception.SQLErrorRetrievingConnectionAndPoolException;
import com.github.daytron.revworks.model.ClassTable;
import com.github.daytron.revworks.model.Coursework;
import com.github.daytron.revworks.presenter.LocalDateTimeColumnGenerator;
import com.github.daytron.revworks.presenter.StudentIdColumnGenerator;
import com.github.daytron.revworks.presenter.StudentNameColumnGenerator;
import com.github.daytron.revworks.service.CurrentUserSession;
import com.github.daytron.revworks.util.NotificationUtil;
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
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.themes.ValoTheme;
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
        // Important!!
        // shutdown and cleanup any previous threads created
        // from coursework view and comment component if possible
        CurrentUserSession.shutdownCourseworkViewExecutorService();
        CurrentUserSession.shutdownCommentExectorService();
        
        if (!isInitialised) {
            try {
                this.listOfNBeanItemContainers
                        = MainUI.get().getLecturerDataProvider().extractCourseworkData();
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
            tabSheet.setStyleName(ValoTheme.TABSHEET_FRAMED);

            for (Map.Entry<ClassTable, BeanItemContainer> entry
                    : this.listOfNBeanItemContainers.entrySet()) {

                final TabWidget tabWidget = new TabWidget(entry.getValue(),
                        entry.getKey().getModuleName());

                tabSheet.addTab(tabWidget.createNewTab(),
                        entry.getKey().getModuleId());
            }
        }

    }

    /**
     * A tab view component for each module content in the {@link TabSheet}
     * object.
     */
    final class TabWidget {

        private final BeanItemContainer beanItemContainer;
        private final String moduleNameAsTableName;
        private final Table moduleTable;
        private Coursework selectedCoursework;

        public TabWidget(BeanItemContainer beanItemContainer, String moduleNameAsTableName) {
            this.beanItemContainer = beanItemContainer;
            this.moduleNameAsTableName = moduleNameAsTableName;
            this.moduleTable = new Table();
            this.selectedCoursework = null;
        }

        public BeanItemContainer getBeanItemContainer() {
            return beanItemContainer;
        }

        public String getModuleNameAsTableName() {
            return moduleNameAsTableName;
        }

        public CssLayout createNewTab() {
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
                    footerTableLabel.setValue(listOfNBeanItemContainers.size() 
                            + " item found.");
                } else {
                    footerTableLabel.setValue(listOfNBeanItemContainers.size() 
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

            return wrapperItem;
        }

        public Table createSubmittedCourseworkTable(BeanItemContainer beanItemContainer,
                String moduleNameAsTableName) {

            this.moduleTable.setContainerDataSource(beanItemContainer);
            this.moduleTable.setCaption(moduleNameAsTableName);
            moduleTable.setEditable(false);
            moduleTable.setSelectable(true);
            moduleTable.setWidth("100%");

            // Set column data properties and names
            moduleTable.setColumnHeader("id", "ID");

            moduleTable.addGeneratedColumn("studentId",
                    new StudentIdColumnGenerator());
            moduleTable.setColumnHeader("studentId", "Student ID");

            moduleTable.addGeneratedColumn("studentName",
                    new StudentNameColumnGenerator());
            moduleTable.setColumnHeader("studentName", "Student");

            moduleTable.setColumnHeader("title", "Title");
            moduleTable.addGeneratedColumn("dateSubmitted",
                    new LocalDateTimeColumnGenerator());
            moduleTable.setColumnHeader("dateSubmitted", "Date Submitted");

            // Arrange columns order
            moduleTable.setVisibleColumns((Object[]) new String[]{"id",
                "studentId", "studentName", "title", "dateSubmitted"});
            // Set column alignment
            moduleTable.setColumnAlignments(new Table.Align[]{Table.ALIGN_LEFT,
                Table.Align.CENTER, Table.Align.CENTER, Table.Align.CENTER,
                Table.Align.CENTER});

            // Sort by date and module id
            moduleTable.setSortEnabled(true);
            moduleTable.sort(new String[]{"dateSubmitted"},
                    new boolean[]{false});

            moduleTable.setColumnWidth("id", 50);
            moduleTable.setPageLength(8);

            moduleTable.setImmediate(true);
            moduleTable.addItemClickListener(new ItemClickEvent.ItemClickListener() {

                @Override
                public void itemClick(ItemClickEvent event) {
                    // Save the selected bean
                    BeanItem<Coursework> item = (BeanItem) event.getItem();
                    Coursework selectedBean = item.getBean();
                    selectedCoursework = selectedBean;
                }
            });

            return moduleTable;

        }

        public HorizontalLayout createPanelHeader() {
            final HorizontalLayout layoutHeader = new HorizontalLayout();
            layoutHeader.addStyleName("v-panel-caption");
            layoutHeader.setWidth("100%");
            layoutHeader.setSpacing(true);
            layoutHeader.setMargin(true);

            Label titleLabel = new Label(VIEW_TITLE);
            titleLabel.setStyleName(ValoTheme.LABEL_BOLD);
            titleLabel.setSizeFull();
            layoutHeader.addComponent(titleLabel);

            Button openFileButton = new Button("View Coursework");
            openFileButton.setDisableOnClick(true);
            openFileButton.setStyleName(ValoTheme.BUTTON_SMALL);
            openFileButton.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    if (beanItemContainer.size() < 1 
                            || selectedCoursework == null) {
                        NotificationUtil.showInformation(
                                "No table item selected.", 
                                "No item found.");
                        event.getButton().setEnabled(true);
                    } else {
                        AppEventBus.post(new AppEvent
                                .LecturerViewCourseworkEvent(selectedCoursework));
                    }
                }
            });
            layoutHeader.addComponent(openFileButton);

            layoutHeader.setExpandRatio(titleLabel, 1);
            return layoutHeader;
        }

    }

}
