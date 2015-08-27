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
import com.github.daytron.revworks.data.PreparedQueryStatement;
import com.github.daytron.revworks.event.AppEvent;
import com.github.daytron.revworks.event.AppEventBus;
import com.github.daytron.revworks.model.Coursework;
import com.github.daytron.revworks.service.DataProviderAbstract;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Shared GUI view component for lecturers and students for displaying comments 
 * in the coursework view.
 * 
 * @author Ryan Gilera
 */
@SuppressWarnings("serial")
public class CommentComponent extends VerticalLayout {

    private final ScheduledExecutorService scheduler
            = Executors.newScheduledThreadPool(1);
    private final Runnable runnableTask;
    private final ScheduledFuture scheduledFuture;

    private final Coursework coursework;
    private final Label commentLabel;
    private final TextArea writerArea;
    private boolean isFirstComment;
    private int noteId;
    private final int page;
    private final CourseworkView courseworkView;
    private final Panel commentContainerPanel;

    /**
     * This constructor is used when creating a comment and a note at the same 
     * time. It does not take note id as an argument as a result. By default it 
     * is set to zero. Note id starts at 1.
     * 
     * @param coursework the associated coursework
     * @param isFirstComment boolean value for initial comment creation
     * @param page the associated page
     * @param courseworkView The associated view object
     */
    public CommentComponent(Coursework coursework,
            boolean isFirstComment, int page, CourseworkView courseworkView) {
        this(coursework, isFirstComment, page, 0, courseworkView);
    }

    /**
     * This constructor is used when creating a comment in an existing note. It 
     * launches an executor service for update and retrieval of comments from 
     * the database. 
     * 
     * @param coursework the associated coursework
     * @param isFirstComment boolean value for initial comment creation
     * @param page the associated page
     * @param noteId the note id where the comments are associated with
     * @param courseworkView The associated view object
     */
    public CommentComponent(Coursework coursework,
            boolean isFirstComment, int page, int noteId,
            CourseworkView courseworkView) {
        this.coursework = coursework;
        this.isFirstComment = isFirstComment;
        this.page = page;
        this.noteId = noteId;
        this.courseworkView = courseworkView;
        commentContainerPanel = new Panel();

        setSizeFull();
        setStyleName(ValoTheme.LAYOUT_CARD);
        addStyleName("coursework-panel-wrapper");
        addStyleName("coursework-panel-border");

        this.commentLabel = new Label("", ContentMode.HTML);
        this.writerArea = new TextArea();
        HorizontalLayout headerLayout = createHeader();
        addComponent(headerLayout);

        VerticalLayout contentLayout = createContent();
        addComponent(contentLayout);

        setExpandRatio(contentLayout, 1);

        // Only focus to writerArea if creating a comment for the first time
        if (isFirstComment) {
            writerArea.focus();
        }

        runnableTask = new CommentsExtractorRunnable(MainUI.get()
                .getAccessControl().isUserAStudent());
        scheduledFuture = scheduler.scheduleWithFixedDelay(runnableTask,
                0, 500, TimeUnit.MILLISECONDS);
    }

    /**
     * Creates a header bar on top of this component with label and a close 
     * button. The close button allows this component to be hidden in plain 
     * sight. When a new comment component replaces this object, this object is 
     * then destroyed.
     * 
     * @return HorizontalLayout object
     */
    private HorizontalLayout createHeader() {
        final HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.addStyleName("v-panel-caption");
        headerLayout.addStyleName("coursework-panel-header");
        headerLayout.setWidth("100%");
        headerLayout.setSpacing(true);

        Label titleLabel = new Label("Comments");
        titleLabel.setStyleName(ValoTheme.LABEL_BOLD);
        titleLabel.setSizeFull();
        headerLayout.addComponent(titleLabel);

        Button closeLayoutButton = new Button("Close");
        closeLayoutButton.setIcon(FontAwesome.TIMES);
        closeLayoutButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        closeLayoutButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        closeLayoutButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                CommentComponent.this.setVisible(false);

                // remove all clicked style on note buttons
                synchronized (this) {
                    for (Map.Entry<Integer, Button> entry
                            : courseworkView.getListOfNoteButtons().entrySet()) {
                        entry.getValue().removeStyleName("note-clicked");
                    }
                }

                CommentComponent.this.shutdownCommentExecutor();
            }
        });
        
        headerLayout.addComponent(closeLayoutButton);
        headerLayout.setExpandRatio(titleLabel, 1);

        return headerLayout;
    }

    /**
     * Creates the content area consists of comment area, textArea and a submit 
     * button.
     * 
     * @return 
     */
    private VerticalLayout createContent() {
        final VerticalLayout contentLayout = new VerticalLayout();
        contentLayout.setSizeFull();

        // Comment Viewer
        commentLabel.setWidth("100%");
        commentLabel.setHeightUndefined();
        commentLabel.addStyleName("comment-label");

        commentContainerPanel.setContent(commentLabel);
        commentContainerPanel.setSizeFull();
        commentContainerPanel.addStyleName("coursework-panel-border");

        // TODO Fille with old contents for commentLabel
        // if (isFirstCommentt == false ) {}
        contentLayout.addComponent(commentContainerPanel);

        // Comment Writer
        HorizontalLayout writerLayout = new HorizontalLayout();
        writerLayout.setWidth("100%");
        writerLayout.addStyleName("coursework-panel-border");

        writerArea.setRows(3);
        writerArea.setWidth("100%");
        writerArea.addStyleName("coursework-panel-border");
        writerLayout.addComponent(writerArea);

        Button sendButton = new Button("Send");
        sendButton.addStyleName("coursework-panel-border");
        sendButton.addStyleName("send");
        sendButton.setDescription("Send comment");
        sendButton.setHeight("100%");
        sendButton.setWidthUndefined();
        sendButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        sendButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (!writerArea.isEmpty()) {
                    String messageString = writerArea.getValue();
                    writerArea.setValue("");

                    if (isFirstComment && noteId == 0) {
                        AppEventBus.post(new AppEvent.SubmitNewNoteEvent(
                                coursework, page, messageString,
                                courseworkView));
                    } else {
                        AppEventBus.post(new AppEvent.SubmitNewCommentEvent(
                                coursework, noteId, messageString));
                    }

                    isFirstComment = false;
                }
            }
        });
        writerLayout.addComponent(sendButton);

        writerLayout.setExpandRatio(writerArea, 1);
        contentLayout.addComponent(writerLayout);

        contentLayout.setExpandRatio(commentContainerPanel, 1);
        return contentLayout;
    }

    /**
     * Access the comment label.
     * 
     * @return label object
     */
    public Label getCommentLabel() {
        return commentLabel;
    }

    /**
     * Access the text area.
     * 
     * @return TextArea object
     */
    public TextArea getWriterArea() {
        return writerArea;
    }

    /**
     * Sets the noteId.
     * 
     * @param noteId integer value
     */
    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    /**
     * Access the note id.
     * 
     * @return integer value
     */
    public int getNoteId() {
        return noteId;
    }

    /**
     * shuts down the executor service for retrieving and updating comments 
     * in real time.
     */
    public void shutdownCommentExecutor() {
        scheduledFuture.cancel(true);
        scheduler.shutdownNow();
    }

    /**
     * A Runnable object for Executor service that updates and retrieves comments 
     * continuously. 
     */
    final class CommentsExtractorRunnable extends DataProviderAbstract 
    implements Runnable {

        private int previousCommentCount = 0;
        private final boolean isStudentUser;

        public CommentsExtractorRunnable(boolean isStudentUser) {
            this.isStudentUser = isStudentUser;
        }

        /**
         * Updates and retrieves comments continuously from the database. If 
         * the associated comment component only just created its first comment, 
         * no need to retrieve non-existing previous comments.
         */
        @Override
        public void run() {
            if (!isFirstComment && noteId > 0) {
                if (reserveConnectionPool()) {
                    try {
                        ResultSet resultSet;
                        StringBuilder stringBuilder;
                        try (PreparedStatement preparedStatement = getConnection()
                                .prepareStatement(PreparedQueryStatement.SELECT_COMMENT.getQuery())) {
                            preparedStatement.setInt(1, noteId);
                            resultSet = preparedStatement.executeQuery();
                            
                            if (!resultSet.next()) {
                                preparedStatement.close();
                                resultSet.close();
                                releaseConnection();
                                return;
                            }
                            // Calculate the current resulting row count
                            int numberOfResultedRows = 0;
                            resultSet.beforeFirst();
                            
                            while (resultSet.next()) {
                                numberOfResultedRows += 1;
                            }

                            // Skips repainting comments on Label if the 
                            // result returns the same number of comments.
                            // Note comments cannot be deleted so comparing 
                            // them by size is valid
                            if (numberOfResultedRows == 0
                                    || (numberOfResultedRows == previousCommentCount)) {

                                preparedStatement.close();
                                resultSet.close();
                                releaseConnection();
                                return;
                            }

                            // Save it for the next round
                            previousCommentCount = numberOfResultedRows;

                            stringBuilder = new StringBuilder();
                            resultSet.beforeFirst();
                            DateTimeFormatter formatter
                                    = DateTimeFormatter.ofPattern("dd-MMM hh:mm a");

                            int counter = 0;
                            
                            while (resultSet.next()) {
                                counter += 1;
                                String message = resultSet.getString(1);

                                Timestamp timestamp = resultSet.getTimestamp(2);
                                LocalDateTime dateSubmitted = timestamp.toLocalDateTime();
                                String dateString = dateSubmitted.format(formatter);

                                boolean isStudentToLecturer = resultSet.getBoolean(3);

                                // Only add hr line after 1st comment
                                if (counter > 1) {
                                    stringBuilder.append("<hr>");
                                }

                                if (isStudentToLecturer) {
                                    if (isStudentUser) {
                                        stringBuilder.append("<div class=\"right-align-comment\"><small><b>")
                                                .append("Me" + "&nbsp;&nbsp;")
                                                .append(dateString)
                                                .append("</b></small>")
                                                .append("<br>")
                                                .append(message)
                                                .append("</div>");
                                    } else {
                                        stringBuilder.append("<div><small><b>")
                                                .append(coursework.getStudentUser()
                                                        .getFirstName())
                                                .append("&nbsp;&nbsp;")
                                                .append(dateString)
                                                .append("</b></small>")
                                                .append("<br>").append(message)
                                                .append("</div>");
                                    }
                                } else {
                                    if (isStudentUser) {
                                        stringBuilder.append("<div><small><b>")
                                                .append(coursework.getClassTable()
                                                        .getLecturerUser()
                                                        .getFirstName())
                                                .append("&nbsp;&nbsp;")
                                                .append(dateString)
                                                .append("</b></small>")
                                                .append("<br>").append(message)
                                                .append("</div>");
                                    } else {
                                        stringBuilder.append("<div class=\"right-align-comment\"><small><b>")
                                                .append("Me" + "&nbsp;&nbsp;")
                                                .append(dateString)
                                                .append("</b></small>")
                                                .append("<br>")
                                                .append(message)
                                                .append("</div>");
                                    }
                                }
                            }
                        }
                        resultSet.close();

                        MainUI.get().access(new Runnable() {
                            @Override
                            public void run() {
                                commentLabel.setValue(stringBuilder.toString());
                                commentContainerPanel.setScrollTop(Short.MAX_VALUE);
                            }
                        });

                    } catch (Exception ex) {
                        Logger.getLogger(CommentsExtractorRunnable.class.getName())
                                .log(Level.SEVERE, null, ex);
                    } finally {
                        releaseConnection();
                    }
                }
            }
        }
    }
    
}
