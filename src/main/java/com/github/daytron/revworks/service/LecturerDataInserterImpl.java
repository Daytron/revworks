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

import com.github.daytron.revworks.data.FontAwesomeIcon;
import com.github.daytron.revworks.data.PreparedQueryStatement;
import com.github.daytron.revworks.event.AppEvent.*;
import com.github.daytron.revworks.util.NotificationUtil;
import com.google.common.eventbus.Subscribe;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Concrete implementation of {@link LecturerDataInserter}.
 *
 * @author Ryan Gilera
 */
public class LecturerDataInserterImpl extends DataInserterAbstract implements
        LecturerDataInserter {

    @Subscribe
    @Override
    public void insertNewAnnouncement(final LecturerSubmitNewAnnouncementEvent event) {

        if (reserveConnectionPool()) {
            try {
                // Insert new Announcement item
                PreparedStatement preparedStatement1 = getConnection()
                        .prepareStatement(
                                PreparedQueryStatement.LECTURER_INSERT_NEW_ANNOUNCEMENT
                                .getQuery(), Statement.RETURN_GENERATED_KEYS);
                preparedStatement1.setString(1, event.getTitle().getValue());
                preparedStatement1.setString(2, event.getRichTextArea().getValue());

                preparedStatement1.executeUpdate();
                getConnection().commit();

                ResultSet resultSet = preparedStatement1.getGeneratedKeys();
                int newId;

                if (resultSet.next()) {
                    newId = resultSet.getInt(1);
                } else {
                    preparedStatement1.close();
                    resultSet.close();
                    releaseConnection();

                    notifyDataSendError();
                    return;
                }

                PreparedStatement preparedStatement2 = getConnection()
                        .prepareStatement(
                                PreparedQueryStatement
                                        .LECTURER_INSERT_NEW_CLASSWIDE_ANNOUNCEMENT
                                        .getQuery());

                preparedStatement2.setInt(1, newId);
                preparedStatement2.setInt(2, event.getSelectedClass().getId());
                preparedStatement2.executeUpdate();
                getConnection().commit();

                // Close all statements and connection
                preparedStatement1.close();
                resultSet.close();
                preparedStatement2.close();

                // Reset all form fields
                event.getTitle().setValue("");
                event.getRichTextArea().setValue("");

                NotificationUtil.showInformation(
                        FontAwesomeIcon.CHECK_CIRCLE_O.getLgSize(),
                        "Sent!", "Your announcement is saved.");
                
                // Reenable the submit button, allowing lecturer to 
                // submit another one.
                event.getSubmitButton().setEnabled(true);
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
