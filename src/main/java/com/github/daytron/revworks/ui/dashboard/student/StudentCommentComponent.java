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
import com.github.daytron.revworks.data.PreparedQueryStatement;
import com.github.daytron.revworks.event.AppEvent;
import com.github.daytron.revworks.event.AppEventBus;
import com.github.daytron.revworks.model.Coursework;
import com.github.daytron.revworks.service.CurrentUserSession;
import com.github.daytron.revworks.service.DataProviderAbstract;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
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
public class StudentCommentComponent extends CssLayout {

    private final ScheduledExecutorService scheduler
            = Executors.newScheduledThreadPool(1);
    private final Runnable runnableTask;
    private final ScheduledFuture scheduledFuture;

    private final Coursework coursework;
    private final Label commentLabel;
    private final TextArea writerArea;
    private int noteId;

    public StudentCommentComponent(Coursework coursework, int noteId) {
        this.coursework = coursework;
        this.noteId = noteId;

        setWidth("100%");
        setStyleName(ValoTheme.LAYOUT_CARD);

        this.commentLabel = new Label("", ContentMode.HTML);
        this.writerArea = new TextArea();
        addComponent(createHeader());
        addComponent(createContent());

        writerArea.focus();
        
        runnableTask = new CommentsExtractorRunnable();
        scheduledFuture = scheduler.scheduleWithFixedDelay(runnableTask,
                0, 1, TimeUnit.SECONDS);

        CurrentUserSession.setCurrentExecutorService(scheduler);
    }

    private HorizontalLayout createHeader() {
        final HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.addStyleName("v-panel-caption");
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
                StudentCommentComponent.this.shutdownScheduler();
                StudentCommentComponent.this.setVisible(false);
            }
        });
        headerLayout.addComponent(closeLayoutButton);
        headerLayout.setExpandRatio(titleLabel, 1);

        return headerLayout;
    }

    private VerticalLayout createContent() {
        final VerticalLayout contentLayout = new VerticalLayout();
        contentLayout.setSizeFull();

        // Comment Viewer
        Panel commentContainerPanel = new Panel();

        commentLabel.setSizeFull();
        commentLabel.addStyleName("comment-label");
        commentContainerPanel.setContent(commentLabel);
        commentContainerPanel.setHeight("493px");
        commentContainerPanel.setWidth("100%");

        // TODO Fille with old contents for commentLabel
        // if (isFirstCommentt == false ) {}
        contentLayout.addComponent(commentContainerPanel);

        // Comment Writer
        HorizontalLayout writerLayout = new HorizontalLayout();
        writerLayout.setWidth("100%");

        writerArea.setRows(3);
        writerArea.setWidth("100%");
        writerLayout.addComponent(writerArea);

        Button sendButton = new Button("Send");
        sendButton.setHeight("100%");
        sendButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        sendButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (!writerArea.isEmpty() && noteId > 0) {
                    String messageString = writerArea.getValue();
                    writerArea.setValue("");

                    AppEventBus.post(new AppEvent.SubmitACommentEvent(
                            noteId, messageString, true));
                }
            }
        });
        writerLayout.addComponent(sendButton);

        writerLayout.setExpandRatio(writerArea, 1);
        contentLayout.addComponent(writerLayout);

        contentLayout.setExpandRatio(commentContainerPanel, 1);
        return contentLayout;
    }

    public int getNoteId() {
        return noteId;
    }

    public void shutdownScheduler() {
        scheduledFuture.cancel(true);
        scheduler.shutdownNow();
    }

    final class CommentsExtractorRunnable extends DataProviderAbstract implements Runnable {

        private int previousCommentCount = 0;

        @Override
        public void run() {
            if (noteId > 0) {
                if (reserveConnectionPool()) {
                    try {
                        ResultSet resultSet;
                        StringBuilder stringBuilder;
                        try (PreparedStatement preparedStatement = getConnection()
                                .prepareStatement(PreparedQueryStatement
                                        .SELECT_COMMENT_ASC.getQuery())) {
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
                            while (resultSet.next()) {
                                String message = resultSet.getString(1);

                                Timestamp timestamp = resultSet.getTimestamp(2);
                                LocalDateTime dateSubmitted = timestamp.toLocalDateTime();
                                String dateString = dateSubmitted.format(formatter);

                                boolean isStudentToLecturer = resultSet.getBoolean(3);

                                if (isStudentToLecturer) {
                                    stringBuilder.append("<small><b>")
                                            .append("Me")
                                            .append("&nbsp;&nbsp;")
                                            .append(dateString)
                                            .append("</b></small>")
                                            .append("<br>").append(message)
                                            .append("<br>");
                                } else {
                                    stringBuilder.append("<small><b>")
                                            .append(coursework.getClassTable()
                                            .getLecturerUser().getFirstName())
                                            .append("&nbsp;&nbsp;")
                                            .append(dateString)
                                            .append("</b></small>")
                                            .append("<br>").append(message)
                                            .append("<br>");
                                }
                            }
                        }
                        resultSet.close();

                        MainUI.get().access(new Runnable() {
                            @Override
                            public void run() {
                                commentLabel.setValue(stringBuilder.toString());
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
