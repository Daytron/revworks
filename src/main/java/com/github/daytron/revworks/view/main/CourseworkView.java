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
package com.github.daytron.revworks.view.main;

import com.github.daytron.revworks.MainUI;
import com.github.daytron.revworks.data.ErrorMsg;
import com.github.daytron.revworks.data.PreparedQueryStatement;
import com.github.daytron.revworks.event.AppEvent;
import com.github.daytron.revworks.event.AppEventBus;
import com.github.daytron.revworks.model.Coursework;
import com.github.daytron.revworks.presenter.NoteButtonListener;
import com.github.daytron.revworks.service.CurrentUserSession;
import com.github.daytron.revworks.service.DataProviderAbstract;
import com.github.daytron.revworks.util.NotificationUtil;
import com.github.daytron.revworks.util.PdfRenderer;
import com.vaadin.data.Property;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ryan Gilera
 */
@SuppressWarnings("serial")
public class CourseworkView extends VerticalLayout implements View {

    public static final String VIEW_NAME = "CourseworkView";
    public static final String VIEW_TITLE = "Coursework Display View";

    private boolean isInitialised = false;
    private Coursework coursework;
    private List<File> listOfPdfPages;
    private Panel courseworkPagePanel;
    private int currentPage;
    private final TextField pageField;
    private VerticalLayout scrollNoteLayout;
    private CommentComponent commentLayout;
    private HorizontalLayout coreContentLayout;

    private Map<Integer, Button> listOfNoteButtons;

    private ScheduledExecutorService noteScheduler
            = Executors.newScheduledThreadPool(1);
    private Runnable noteRunnableTask;
    private ScheduledFuture noteScheduledFuture;

    public CourseworkView() {
        this.pageField = new TextField();
        coursework = null;
        setSizeFull();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        if (!isInitialised) {

            listOfNoteButtons = new ConcurrentHashMap<>();

            if (MainUI.get().getAccessControl().isUserAStudent()) {
                coursework = MainUI.get().getStudentDataProvider()
                        .getReceivedCoursework();
            } else {
                coursework = MainUI.get().getLecturerDataProvider()
                        .getReceivedCoursework();
            }

            if (coursework == null) {
                NotificationUtil.showError(
                        ErrorMsg.DATA_FETCH_ERROR.getText(),
                        ErrorMsg.CONSULT_YOUR_ADMIN.getText());
                return;
            }

            try {
                listOfPdfPages = new ArrayList<>();
                initView();

                noteRunnableTask = new CourseworkView.NotesExtractorRunnable(
                        MainUI.get().getAccessControl().isUserAStudent(),
                        this);
                noteScheduledFuture = noteScheduler.scheduleWithFixedDelay(
                        noteRunnableTask, 0, 500, TimeUnit.MILLISECONDS);

                // Store coursework view to this session
                CurrentUserSession.setCurrentCourseworkView(this);
            } catch (Exception ex) {
                Logger.getLogger(CourseworkView.class.getName())
                        .log(Level.SEVERE, null, ex);
                NotificationUtil.showError(
                        ErrorMsg.DATA_FETCH_ERROR.getText(),
                        ErrorMsg.CONSULT_YOUR_ADMIN.getText());
                return;
            }
            isInitialised = true;

        }
    }

    private void initView() throws Exception {
        final VerticalLayout expanderWraperLayout = new VerticalLayout();
        expanderWraperLayout.setSizeFull();
        expanderWraperLayout.addStyleName("wrapper-layout");

        VerticalLayout contentLayout = new VerticalLayout();
        contentLayout.setSizeFull();
        contentLayout.addStyleName(ValoTheme.LAYOUT_CARD);
        contentLayout.addStyleName("coursework-view-wrapper-content-layout");

        
        HorizontalLayout headerLayout = createHeaderView(expanderWraperLayout);
        contentLayout.addComponent(headerLayout);
        VerticalLayout subContentLayout = createContentLayout();
        contentLayout.addComponent(subContentLayout);
        contentLayout.setExpandRatio(subContentLayout, 1);
        
        expanderWraperLayout.addComponent(contentLayout);

        addComponent(expanderWraperLayout);

    }

    private HorizontalLayout createHeaderView(final VerticalLayout expanderLayout) {
        HorizontalLayout toolbarLayout = new HorizontalLayout();
        toolbarLayout.addStyleName("content-toolbar");
        toolbarLayout.setWidth("100%");

        Label contentLabel = new Label(VIEW_TITLE);
        contentLabel.addStyleName(ValoTheme.LABEL_H4);
        contentLabel.setSizeFull();
        toolbarLayout.addComponent(contentLabel);

        final Button expanderButton = new Button();
        expanderButton.setDescription("Maximise view");
        expanderButton.setIcon(FontAwesome.EXPAND);
        expanderButton.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        expanderButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        expanderButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (!expanderLayout.getStyleName().contains("max")) {
                    event.getButton().setIcon(FontAwesome.COMPRESS);
                    AppEventBus.post(new AppEvent.ToggleCourseworkViewEvent(
                            expanderLayout, true));
                    expanderButton.setDescription("Minimise view");
                } else {
                    expanderLayout.removeStyleName("max");
                    event.getButton().setIcon(FontAwesome.EXPAND);
                    AppEventBus.post(new AppEvent.ToggleCourseworkViewEvent(
                            expanderLayout, false));
                    expanderButton.setDescription("Maximise view");
                }
            }
        });
        toolbarLayout.addComponent(expanderButton);

        toolbarLayout.setExpandRatio(contentLabel, 1);
        return toolbarLayout;
    }

    private VerticalLayout createContentLayout() throws IOException, Exception {
        final VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();

        coreContentLayout = new HorizontalLayout();
        coreContentLayout.setSizeFull();
        coreContentLayout.addStyleName("panels-horizontal-content-wrapper");
        coreContentLayout.setSpacing(true);

        // Coursework display viewer
        VerticalLayout viewerLayout = createCourseworkViewer();

        // Scroll Layout
        VerticalLayout scrollLayout = createNoteComponent();

        // Comment Layout
        // By default it is a placeholder hidden with noteId 0
        commentLayout
                = new CommentComponent(coursework, true, currentPage,
                        this);

        // Store new comment component to this session
        CurrentUserSession.setCurrentCommentComponent(commentLayout);
        commentLayout.setVisible(false);

        coreContentLayout.addComponent(viewerLayout);
        coreContentLayout.addComponent(scrollLayout);
        coreContentLayout.addComponent(commentLayout);
        coreContentLayout.setExpandRatio(viewerLayout, 7);
        coreContentLayout.setExpandRatio(scrollLayout, 1);
        coreContentLayout.setExpandRatio(commentLayout, 2);

        verticalLayout.addComponent(coreContentLayout);

        return verticalLayout;

    }

    private VerticalLayout createCourseworkViewer() throws IOException, Exception {
        final VerticalLayout viewerLayout = new VerticalLayout();
        viewerLayout.setSizeFull();
        viewerLayout.setStyleName(ValoTheme.LAYOUT_CARD);
        viewerLayout.addStyleName("coursework-panel-wrapper");
        viewerLayout.addStyleName("coursework-panel-border");

        // Header
        final HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.addStyleName("v-panel-caption");
        headerLayout.addStyleName("coursework-panel-header");
        headerLayout.setWidth("100%");
        headerLayout.setSpacing(true);

        Label titleLabel = new Label("Coursework Viewer");
        titleLabel.setStyleName(ValoTheme.LABEL_BOLD);
        titleLabel.setSizeFull();
        headerLayout.addComponent(titleLabel);

        pageField.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        pageField.setWidth(3, Unit.EM);
        pageField.setValue("1");
        pageField.setImmediate(true);
        pageField.addValueChangeListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                try {
                    int page = Integer.parseInt(pageField.getValue());

                    if (page == currentPage) {
                        return;
                    }

                    if (page > listOfPdfPages.size() || 
                            page < 1) {
                        NotificationUtil.showWarning("Page out of bounds!", 
                                "No such page exist.");
                        pageField.setValue(String.valueOf(currentPage));
                    } else {
                        try {
                        FileResource fileResource
                                = new FileResource(listOfPdfPages.get(page - 1));
                        Image image = new Image(null, fileResource);
                        image.setWidth("100%");
                        image.setHeightUndefined();
                                
                        courseworkPagePanel.setContent(image);
                        currentPage = page;
                    } catch (Exception e) {
                        NotificationUtil.showError(
                                ErrorMsg.DATA_FETCH_ERROR.getText(),
                                ErrorMsg.CONSULT_YOUR_ADMIN.getText());
                    }
                    }
                    
                    
                } catch (NumberFormatException e) {
                }
            }
        });

        Button leftButton = new Button();
        leftButton.setDescription("Flip to the previous page");
        leftButton.setIcon(FontAwesome.ARROW_LEFT);
        leftButton.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        leftButton.addStyleName(ValoTheme.BUTTON_SMALL);
        leftButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        leftButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (currentPage > 1) {
                    try {
                        FileResource fileResource
                                = new FileResource(listOfPdfPages.get(currentPage - 2));
                        Image image = new Image(null, fileResource);
                        image.setWidth("100%");
                        image.setHeightUndefined();

                        courseworkPagePanel.setContent(image);
                        currentPage = currentPage - 1;
                        pageField.setValue("" + currentPage);
                    } catch (Exception e) {
                        NotificationUtil.showError(
                                ErrorMsg.DATA_FETCH_ERROR.getText(),
                                ErrorMsg.CONSULT_YOUR_ADMIN.getText());
                    }
                }
            }
        });

        Button rightButton = new Button();
        rightButton.setDescription("Flip to the next page");
        rightButton.setIcon(FontAwesome.ARROW_RIGHT);
        rightButton.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        rightButton.addStyleName(ValoTheme.BUTTON_SMALL);
        rightButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        rightButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (currentPage < listOfPdfPages.size()) {
                    try {
                        FileResource fileResource
                                = new FileResource(listOfPdfPages.get(currentPage));
                        Image image = new Image(null, fileResource);
                        image.setWidth("100%");
                        image.setHeightUndefined();

                        courseworkPagePanel.setContent(image);
                        currentPage = currentPage + 1;
                        pageField.setValue("" + currentPage);
                    } catch (Exception e) {
                        NotificationUtil.showError(
                                ErrorMsg.DATA_FETCH_ERROR.getText(),
                                ErrorMsg.CONSULT_YOUR_ADMIN.getText());
                    }
                }
            }
        });

        // Content
        courseworkPagePanel = new Panel();
        courseworkPagePanel.setSizeFull();
        courseworkPagePanel.addStyleName("coursework-panel-border");

        listOfPdfPages = PdfRenderer.extractPages(coursework);

        // Label for total of pages next to the arrows in header
        Label totalPagesLabel = new Label("/ " + listOfPdfPages.size());
        totalPagesLabel.setSizeUndefined();

        Image image = new Image(null, new FileResource(listOfPdfPages.get(0)));
        image.setWidth("100%");
        image.setHeightUndefined();
        courseworkPagePanel.setContent(image);
        currentPage = 1;

        headerLayout.addComponent(leftButton);
        headerLayout.addComponent(pageField);
        headerLayout.addComponent(totalPagesLabel);
        headerLayout.addComponent(rightButton);

        headerLayout.setExpandRatio(titleLabel, 1);

        viewerLayout.addComponent(headerLayout);
        viewerLayout.addComponent(courseworkPagePanel);
        
        viewerLayout.setExpandRatio(courseworkPagePanel, 1);

        return viewerLayout;
    }

    private VerticalLayout createNoteComponent() {
        final VerticalLayout noteLayout = new VerticalLayout();
        noteLayout.setSizeFull();
        noteLayout.setStyleName(ValoTheme.LAYOUT_CARD);
        noteLayout.addStyleName("coursework-panel-wrapper");
        noteLayout.addStyleName("coursework-panel-border");

        // Header
        final HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.addStyleName("v-panel-caption");
        headerLayout.addStyleName("coursework-panel-header");
        headerLayout.setWidth("100%");

        Label titleLabel = new Label("Notes");
        titleLabel.setStyleName(ValoTheme.LABEL_BOLD);
        titleLabel.setSizeFull();
        headerLayout.addComponent(titleLabel);

        Button addNoteButton = new Button("Add");
        addNoteButton.setDescription("Create a new note. This opens "
                + "a new comment section.");
        addNoteButton.setIcon(FontAwesome.PLUS_CIRCLE);
        addNoteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        addNoteButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        addNoteButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                commentLayout.setVisible(true);
                commentLayout.shutdownCommentExecutor();

                CommentComponent lcc
                        = new CommentComponent(coursework,
                                true, currentPage, CourseworkView.this);

                coreContentLayout.replaceComponent(commentLayout, lcc);
                commentLayout = lcc;

                // Store new comment component to this session
                CurrentUserSession.setCurrentCommentComponent(lcc);
            }
        });
        headerLayout.addComponent(addNoteButton);

        headerLayout.setExpandRatio(titleLabel, 1);
        noteLayout.addComponent(headerLayout);

        // Content
        Panel notesPanel = new Panel();
        notesPanel.addStyleName("coursework-panel-border");
        notesPanel.setSizeFull();
        scrollNoteLayout = new VerticalLayout();
        scrollNoteLayout.setWidth("100%");
        scrollNoteLayout.setHeightUndefined();

        notesPanel.setContent(scrollNoteLayout);
        noteLayout.addComponent(notesPanel);
        
        noteLayout.setExpandRatio(notesPanel, 1);

        return noteLayout;

    }

    public VerticalLayout getScrollNoteLayout() {
        return scrollNoteLayout;
    }

    public CommentComponent getCommentLayout() {
        return commentLayout;
    }

    public Map<Integer, Button> getListOfNoteButtons() {
        return listOfNoteButtons;
    }

    public void shutdownNoteExecutor() {
        noteScheduledFuture.cancel(true);
        noteScheduler.shutdownNow();
    }

    public HorizontalLayout getCoreContentLayout() {
        return coreContentLayout;
    }

    public Coursework getCoursework() {
        return coursework;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCommentLayout(CommentComponent commentLayout) {
        this.commentLayout = commentLayout;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public void flipToPage(int pageNumber) {
        FileResource fileResource
                = new FileResource(listOfPdfPages.get(pageNumber - 1));
        Image image = new Image(null, fileResource);
        image.setWidth("100%");

        courseworkPagePanel.setContent(image);
        currentPage = pageNumber;
        pageField.setValue("" + currentPage);
    }

    final class NotesExtractorRunnable extends DataProviderAbstract implements Runnable {

        private int previousNoteCount = 0;
        private final boolean isStudentUser;
        private final CourseworkView courseworkView;
        private boolean isFirstRun;
        private boolean isNewNoteAdded;

        public NotesExtractorRunnable(boolean isStudentUser,
                CourseworkView courseworkView) {
            this.isStudentUser = isStudentUser;
            this.courseworkView = courseworkView;
            this.isFirstRun = true;
            isNewNoteAdded = false;
        }

        @Override
        public void run() {
            if (reserveConnectionPool()) {
                final ResultSet resultSet;

                try (PreparedStatement preparedStatement = getConnection()
                        .prepareStatement(PreparedQueryStatement.SELECT_NOTE.getQuery())) {
                    preparedStatement.setInt(1, coursework.getId());
                    resultSet = preparedStatement.executeQuery();

                    if (!resultSet.next()) {
                        preparedStatement.close();
                        resultSet.close();
                        releaseConnection();
                        
                        isFirstRun = false;
                        return;
                    }

                    // Calculate the current resulting row count
                    int numberOfResultedRows = 0;
                    resultSet.beforeFirst();
                    while (resultSet.next()) {
                        numberOfResultedRows += 1;
                    }

                    // detect if there is a new note added
                    // this feature is used for detecting a note added
                    // by this user to apply clicked css style later on
                    if (numberOfResultedRows == 0
                            || (numberOfResultedRows == previousNoteCount)) {

                        isNewNoteAdded = false;
                    } else {
                        isNewNoteAdded = true;
                        // Save it for the next round
                        previousNoteCount = numberOfResultedRows;
                    }

                    // Refresh scrollNoteLayout
//                    MainUI.get().access(new Runnable() {
//                        @Override
//                        public void run() {
//                            // Clear previous buttons
//                            scrollNoteLayout.removeAllComponents();
//                            listOfNoteButtons = new HashMap<>();
//                        }
//                    });
                    int lastNoteIdForActivatingClickedStyle = -1;
                    boolean lastIsStudentToLecturer = false;

                    resultSet.beforeFirst();
                    while (resultSet.next()) {
                        final int noteId = resultSet.getInt(1);
                        lastNoteIdForActivatingClickedStyle = noteId;

                        final int pageNum = resultSet.getInt(2);

                        final boolean isStudentToLecturer = resultSet.getBoolean(4);
                        lastIsStudentToLecturer = isStudentToLecturer;

                        final boolean isReadStudent = resultSet.getBoolean(5);
                        final boolean isReadLecturer = resultSet.getBoolean(6);

                        MainUI.get().access(new Runnable() {
                            @Override
                            public void run() {
                                boolean alreadyInTheListButton = false;
                                for (Map.Entry<Integer, Button> entry
                                        : courseworkView.getListOfNoteButtons()
                                        .entrySet()) {
                                    if (entry.getKey() == noteId) {
                                        // Detect if the note is not currently open
                                        if (!(commentLayout.getNoteId() == noteId
                                                && commentLayout.isVisible())) {
                                            if (isStudentUser) {
                                                if (!isReadStudent) {
                                                    entry.getValue()
                                                            .addStyleName("note-unread");
                                                }
                                            } else {
                                                if (!isReadLecturer) {
                                                    entry.getValue()
                                                            .addStyleName("note-unread");
                                                }
                                            }
                                        }

                                        alreadyInTheListButton = true;
                                        break;
                                    }
                                }

                                // Otherwise add a new note button
                                if (!alreadyInTheListButton) {

                                    String identifier = "";
                                    if (isStudentToLecturer) {
                                        if (isStudentUser) {
                                            identifier += "Me";
                                        } else {
                                            identifier += coursework
                                                    .getStudentUser()
                                                    .getFirstName();
                                        }
                                    } else {
                                        if (isStudentUser) {
                                            identifier += coursework
                                                    .getClassTable()
                                                    .getLecturerUser()
                                                    .getFirstName();
                                        } else {
                                            identifier += "Me";
                                        }
                                    }

                                    boolean isRead;
                                    if (isStudentUser) {
                                        isRead = isReadStudent;
                                    } else {
                                        isRead = isReadLecturer;
                                    }

                                    Button noteButton = new Button(
                                            identifier + " [p" + pageNum + "]");
                                    noteButton.setWidth("100%");
                                    noteButton.addStyleName("coursework-panel-border");

                                    // Apply style
                                    if (isRead) {
                                        noteButton.addStyleName("note-read");
                                    } else {
                                        noteButton.addStyleName("note-unread");
                                    }

                                    listOfNoteButtons.put(noteId, noteButton);
                                    scrollNoteLayout.addComponent(noteButton);

                                    noteButton.addClickListener(new NoteButtonListener(
                                            courseworkView, pageNum));
                                }

                            }
                        });
                    }

                    // Only triggered if there is a new note added and this method 
                    // run after the first one and there is a last note id
                    if (isNewNoteAdded && !isFirstRun
                            && lastNoteIdForActivatingClickedStyle > 0) {
                        // Logically, the last note button added is 
                        // a newly created opened comment, since it is only updated
                        // if there is a new note added to list
                        // So it is safe to say that this note must also have clicked
                        // style added to it

                        // Make sure first that the newly added note button
                        // is created by the corresponding user
                        // It is possible to add a new button from other user
                        if ((isStudentUser && lastIsStudentToLecturer)
                                || (!isStudentUser && !lastIsStudentToLecturer)) {

                            final int copyOfLastNoteId = lastNoteIdForActivatingClickedStyle;
                            MainUI.get().access(new Runnable() {

                                @Override
                                public void run() {

                                    for (Map.Entry<Integer, Button> entry
                                            : courseworkView.getListOfNoteButtons()
                                            .entrySet()) {
                                        if (entry.getKey() == copyOfLastNoteId) {
                                            entry.getValue().removeStyleName("note-unread");
                                            entry.getValue().addStyleName("note-clicked");
                                            break;
                                        }

                                    }
                                }
                            });
                        } else {
                            // Otherwise retain current clicked style to the 
                            // currently opened comment layout
                            // Possible scenarion when this user is reading a note
                            // then suddenly the other user sent a new note
                            MainUI.get().access(new Runnable() {

                                @Override
                                public void run() {
                                    int currentOpenPageNoteId = commentLayout.getNoteId();

                                    if (commentLayout.isVisible()) {
                                        for (Map.Entry<Integer, Button> entry
                                                : courseworkView.getListOfNoteButtons()
                                                .entrySet()) {
                                            if (currentOpenPageNoteId == entry.getKey()) {
                                                // remove any unread style
                                                entry.getValue().removeStyleName("note-unread");

                                                entry.getValue().addStyleName("note-clicked");
                                                break;
                                            }
                                        }
                                    }
                                }
                            });
                        }

                    }

                    isFirstRun = false;

                    preparedStatement.close();
                    resultSet.close();

                } catch (SQLException ex) {
                    Logger.getLogger(CourseworkView.class.getName())
                            .log(Level.SEVERE, null, ex);
                } finally {
                    releaseConnection();
                }
            }
        }
    }
}
