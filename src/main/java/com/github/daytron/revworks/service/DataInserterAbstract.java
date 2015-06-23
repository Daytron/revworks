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
import com.github.daytron.revworks.data.ErrorMsg;
import com.github.daytron.revworks.data.PreparedQueryStatement;
import com.github.daytron.revworks.event.AppEvent;
import com.github.daytron.revworks.util.NotificationUtil;
import com.google.common.eventbus.Subscribe;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract class for implementing {@link DataInserter} interface for common
 * data insertion and update query process.
 *
 * @author Ryan Gilera
 */
public class DataInserterAbstract extends QueryManagerAbstract implements DataInserter {

    @Override
    public void notifyDataSendError() {
        NotificationUtil.showError(
                ErrorMsg.DATA_SEND_ERROR.getText(),
                ErrorMsg.CONSULT_YOUR_ADMIN.getText());
    }
    
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
    
    @Subscribe
    @Override
    public void insertNewNote(AppEvent.SubmitNewNoteEvent event) {
        if (reserveConnectionPool()) {
            try {
                PreparedStatement preparedStatementNote
                        = getConnection().prepareStatement(
                                PreparedQueryStatement.INSERT_NOTE.getQuery(),
                                Statement.RETURN_GENERATED_KEYS);
                preparedStatementNote.setInt(1, event.getPageNumber());
                
                if (MainUI.get().getAccessControl().isUserAStudent()) {
                    preparedStatementNote.setBoolean(2, true);
                } else {
                    preparedStatementNote.setBoolean(2, false);
                }
                
                preparedStatementNote.setInt(3, event.getCourseworkId());

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
}
