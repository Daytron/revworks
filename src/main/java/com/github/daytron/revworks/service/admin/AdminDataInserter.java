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
package com.github.daytron.revworks.service.admin;

import com.github.daytron.revworks.service.*;
import com.github.daytron.revworks.data.FontAwesomeIcon;
import com.github.daytron.revworks.data.PreparedQueryStatement;
import com.github.daytron.revworks.event.AppEvent;
import com.github.daytron.revworks.util.NotificationUtil;
import com.google.common.eventbus.Subscribe;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract class for implementing {@link DataInserter} interface for common
 * data insertion and update query process.
 *
 * @author Ryan Gilera
 */
public final class AdminDataInserter extends QueryManagerAbstract {

    @Subscribe
    public void insertNewAnnouncement(final AppEvent.AdminSubmitNewAnnouncementEvent event) {
        if (reserveConnectionPool()) {
            try (PreparedStatement preparedStatement = getConnection()
                    .prepareStatement(PreparedQueryStatement
                            .ADMIN_INSERT_ANNOUNCEMENT.getQuery());) {
                preparedStatement.setString(1, event.getTitle().getValue());
                preparedStatement.setString(2, event.getRichTextArea().getValue());
                
                preparedStatement.executeUpdate();
                getConnection().commit();
                
                // Reset all form fields
                event.getTitle().setValue("");
                event.getRichTextArea().setValue("");
                
                NotificationUtil.showInformation(
                        FontAwesomeIcon.CHECK_CIRCLE_O.getLgSize(),
                        "Sent!", "Your announcement is saved.");

                // Reenable the submit button, allowing Admin to 
                // submit another one.
                event.getSubmitButton().setEnabled(true);
            } catch (SQLException ex) {
                Logger.getLogger(AdminDataInserter.class.getName())
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
