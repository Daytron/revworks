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
package com.github.daytron.revworks.service;

import com.github.daytron.revworks.MainUI;
import com.github.daytron.revworks.data.PreparedQueryStatement;
import com.github.daytron.revworks.data.UserNotificationType;
import com.github.daytron.revworks.event.AppEvent;
import com.github.daytron.revworks.event.AppEventBus;
import com.github.daytron.revworks.model.Coursework;
import com.google.common.eventbus.Subscribe;
import com.vaadin.ui.Button;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract class for implementing {@link DataInserter} interface for common
 * data insertion and update query process.
 *
 * @author Ryan Gilera
 */
public class DataInserterAbstract extends QueryManagerAbstract implements DataInserter {

    /**
     * {@inheritDoc }
     */
    @Subscribe
    @Override
    public void insertNewComment(final AppEvent.SubmitNewCommentEvent event) {
        if (reserveConnectionPool()) {
            try (PreparedStatement preparedStatementComment = getConnection()
                    .prepareStatement(
                            PreparedQueryStatement.INSERT_COMMENT.getQuery())) {
                        preparedStatementComment.setString(1, event.getMessage());

                        if (MainUI.get().getAccessControl().isUserAStudent()) {
                            preparedStatementComment.setBoolean(2, true);
                        } else {
                            preparedStatementComment.setBoolean(2, false);
                        }

                        preparedStatementComment.setInt(3, event.getNoteId());

                        preparedStatementComment.executeUpdate();
                        getConnection().commit();

                        preparedStatementComment.close();

                        // Update note of its is_read fields
                        PreparedStatement preparedStatementNote
                                = getConnection().prepareStatement(
                                        PreparedQueryStatement.UPDATE_NOTE.getQuery());

                        if (MainUI.get().getAccessControl().isUserAStudent()) {
                            preparedStatementNote.setBoolean(1, true);
                            preparedStatementNote.setBoolean(2, false);
                        } else {
                            preparedStatementNote.setBoolean(1, false);
                            preparedStatementNote.setBoolean(2, true);
                        }

                        preparedStatementNote.setInt(3, event.getNoteId());
                        preparedStatementNote.executeUpdate();
                        getConnection().commit();

                        preparedStatementNote.close();

                        // Create new user notification for the corresponding user
                        final Coursework coursework = event.getCoursework();
                        
                        if (MainUI.get().getAccessControl().isUserAStudent()) {
                            AppEventBus.post(new AppEvent.InsertNotificationEvent(
                                    " has sent you a comment",
                                    "on ", UserNotificationType.COMMENT,
                                    coursework.getClassTable().getLecturerUser().getId(),
                                    coursework.getId()));
                        } else {
                            AppEventBus.post(new AppEvent.InsertNotificationEvent(
                                    " has sent you a comment",
                                    "on ", UserNotificationType.COMMENT,
                                    coursework.getStudentUser().getId(),
                                    coursework.getId()));
                        }

                    } catch (SQLException ex) {
                        Logger.getLogger(LecturerDataInserterImpl.class.getName())
                                .log(Level.SEVERE, null, ex);
                        notifyDataSendError();
                    } finally {
                        releaseConnection();
                    }
        } else {
            notifyDataSendError();
        }
    }

    /**
     * {@inheritDoc }
     */
    @Subscribe
    @Override
    public void insertNewNote(AppEvent.SubmitNewNoteEvent event) {
        if (reserveConnectionPool()) {
            // Must clear previous style clicked on note buttons
            // because that style must now fall to the new button
            for (Map.Entry<Integer, Button> entry
                    : event.getCourseworkView().getListOfNoteButtons().entrySet()) {
                entry.getValue().removeStyleName("note-clicked");
            }

            try {
                PreparedStatement preparedStatementNote
                        = getConnection().prepareStatement(
                                PreparedQueryStatement.INSERT_NOTE.getQuery(),
                                Statement.RETURN_GENERATED_KEYS);
                preparedStatementNote.setInt(1, event.getPageNumber());

                if (MainUI.get().getAccessControl().isUserAStudent()) {
                    // is_student_to_lecturer
                    preparedStatementNote.setBoolean(2, true);
                    // is_read_student
                    preparedStatementNote.setBoolean(3, true);
                    // is_read_lecturer
                    preparedStatementNote.setBoolean(4, false);
                } else {
                    // is_student_to_lecturer
                    preparedStatementNote.setBoolean(2, false);
                    // is_read_student
                    preparedStatementNote.setBoolean(3, false);
                    // is_read_lecturer
                    preparedStatementNote.setBoolean(4, true);
                }

                preparedStatementNote.setInt(5, event.getCoursework().getId());

                preparedStatementNote.executeUpdate();
                getConnection().commit();

                ResultSet resultSet = preparedStatementNote.getGeneratedKeys();
                int generatedNoteId;

                if (resultSet.next()) {
                    generatedNoteId = resultSet.getInt(1);
                } else {
                    preparedStatementNote.close();
                    resultSet.close();
                    releaseConnection();

                    notifyDataSendError();
                    return;
                }

                PreparedStatement preparedStatementComment
                        = getConnection().prepareStatement(
                                PreparedQueryStatement.INSERT_COMMENT.getQuery());
                preparedStatementComment.setString(1, event.getMessage());

                if (MainUI.get().getAccessControl().isUserAStudent()) {
                    preparedStatementComment.setBoolean(2, true);
                } else {
                    preparedStatementComment.setBoolean(2, false);
                }

                preparedStatementComment.setInt(3, generatedNoteId);

                preparedStatementComment.executeUpdate();
                getConnection().commit();

                preparedStatementNote.close();
                resultSet.close();
                preparedStatementComment.close();

                event.getCourseworkView().getCommentLayout()
                        .setNoteId(generatedNoteId);

                // Create new user notification for the corresponding user
                final Coursework coursework = event.getCoursework();
                
                if (MainUI.get().getAccessControl().isUserAStudent()) {
                    AppEventBus.post(new AppEvent.InsertNotificationEvent(
                            " has created a note",
                            "on ", UserNotificationType.NOTE,
                            coursework.getClassTable().getLecturerUser().getId(),
                            coursework.getId()));
                } else {
                    AppEventBus.post(new AppEvent.InsertNotificationEvent(
                            " has created a note",
                            "on ", UserNotificationType.NOTE,
                            coursework.getStudentUser().getId(),
                            coursework.getId()));
                }

            } catch (SQLException ex) {
                Logger.getLogger(LecturerDataInserterImpl.class.getName())
                        .log(Level.SEVERE, null, ex);
                notifyDataSendError();
            } finally {
                releaseConnection();
            }
        } else {
            notifyDataSendError();
        }
    }

    /**
     * {@inheritDoc }
     */
    @Subscribe
    @Override
    public void updateNoteIsReadWhenClicked(AppEvent.UpdateNoteIsReadWhenClickEvent event) {
        final Button button = event.getButton();

        if (reserveConnectionPool()) {
            try {
                PreparedStatement preparedStatementNote;
                
                if (MainUI.get().getAccessControl().isUserAStudent()) {
                    preparedStatementNote
                            = getConnection().prepareStatement(
                                    PreparedQueryStatement.STUDENT_UPDATE_NOTE
                                    .getQuery());

                    preparedStatementNote.setBoolean(1, true);
                    preparedStatementNote.setInt(2, event.getNoteId());
                } else {
                    preparedStatementNote
                            = getConnection().prepareStatement(
                                    PreparedQueryStatement.LECTURER_UPDATE_NOTE
                                    .getQuery());

                    preparedStatementNote.setBoolean(1, true);
                    preparedStatementNote.setInt(2, event.getNoteId());
                }

                preparedStatementNote.executeUpdate();
                getConnection().commit();

                preparedStatementNote.close();
                
                button.removeStyleName("note-unread");
                button.addStyleName("note-read");
                button.addStyleName("note-clicked");
            } catch (SQLException ex) {
                Logger.getLogger(DataInserterAbstract.class.getName())
                        .log(Level.SEVERE, null, ex);
                notifyDataSendError();
            } finally {
                releaseConnection();
            }
        } else {
            notifyDataSendError();
        }
    }
    
}
