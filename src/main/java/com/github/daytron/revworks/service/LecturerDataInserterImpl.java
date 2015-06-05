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
import com.github.daytron.revworks.event.AppEvent;
import com.github.daytron.revworks.util.NotificationUtil;
import com.google.common.eventbus.Subscribe;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    public void insertNewAnnouncement(final AppEvent.LecturerSubmitNewAnnouncementEvent event) {

        if (reserveConnectionPool()) {
            try {
                int newId;
                // Insert new Announcement item
                PreparedStatement preparedStatement1 = getConnection()
                        .prepareStatement(
                                PreparedQueryStatement.LECTURER_INSERT_NEW_ANNOUNCEMENT.getQuery());
                preparedStatement1.setString(1, event.getTitle().getValue());
                preparedStatement1.setString(2, event.getRichTextArea().getValue());
                System.out.println("Prepared 1: " + preparedStatement1.toString());
                preparedStatement1.executeUpdate();
                getConnection().commit();

                // Retrive last id of the last row of Announcement, that is 
                // the announcement just added
                PreparedStatement preparedStatement2 = getConnection()
                        .prepareStatement(
                                PreparedQueryStatement.SELECT_LASTROW_ANNOUNCEMENT.getQuery());
                ResultSet idResultSet = preparedStatement2.executeQuery();
                idResultSet.first();

                newId = idResultSet.getInt(1);
                System.out.println("id: " + newId);

                // Insert new class wide announcement item
                PreparedStatement preparedStatement3 = getConnection()
                        .prepareStatement(
                                PreparedQueryStatement.LECTURER_INSERT_NEW_CLASSWIDE_ANNOUNCEMENT.getQuery());

                preparedStatement3.setInt(1, newId);
                preparedStatement3.setInt(2, event.getSelectedClass().getId());
                System.out.println("prepared 3: " + preparedStatement3.toString());
                preparedStatement3.executeUpdate();
                getConnection().commit();

                // Close all statements and connection
                preparedStatement1.close();
                preparedStatement2.close();
                preparedStatement3.close();
                releaseConnection();

                // Reset all form fields
                event.getTitle().setValue("");
                event.getRichTextArea().setValue("");

                NotificationUtil.showInformation(
                        FontAwesomeIcon.CHECK_CIRCLE_O.getLgSize(),
                        "Sent!", "Your announcement is saved.");
            } catch (SQLException ex) {
                Logger.getLogger(LecturerDataInserterImpl.class.getName())
                        .log(Level.SEVERE, null, ex);
                notifyDataSendError();
            }
        } else {
            notifyDataSendError();
        }
    }

}
