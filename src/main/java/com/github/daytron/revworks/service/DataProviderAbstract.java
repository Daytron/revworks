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

import com.github.daytron.revworks.exception.SQLNoResultFoundException;
import com.github.daytron.revworks.exception.SQLErrorRetrievingConnectionAndPoolException;
import com.github.daytron.revworks.exception.SQLErrorQueryException;
import com.github.daytron.revworks.MainUI;
import com.github.daytron.revworks.data.AnnouncementType;
import com.github.daytron.revworks.data.ExceptionMsg;
import com.github.daytron.revworks.data.PreparedQueryStatement;
import com.github.daytron.revworks.data.UserType;
import com.github.daytron.revworks.model.Announcement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ryan Gilera
 */
public abstract class DataProviderAbstract extends QueryManagerAbstract
        implements DataProvider {

    private static final String GSM_LONDON = "GSM London";

    private List<Announcement> listOfAnnouncements;

    public DataProviderAbstract() {
    }

    @Override
    public List<Announcement> populateHomeViewWithData() throws
            SQLErrorQueryException, SQLNoResultFoundException,
            SQLErrorRetrievingConnectionAndPoolException {

        if (reserveConnectionPool()) {
            try {
                PreparedStatement preparedStatement;
                if (MainUI.get().getAccessControl().isUserAStudent()) {
                    preparedStatement = getConnection().prepareStatement(
                            PreparedQueryStatement.STUDENT_ANNOUNCEMENT_SELECT_QUERY
                            .getQuery());

                } else {
                    // Otherwise it's a lecturer
                    preparedStatement = getConnection().prepareStatement(
                            PreparedQueryStatement.LECTURER_ANNOUNCEMENT_SELECT_QUERY
                            .getQuery());
                }

                preparedStatement.setInt(1,
                        MainUI.get().getAccessControl().getUserId());
                preparedStatement.setInt(2,
                        MainUI.get().getAccessControl().getUserId());

                ResultSet resultSet = preparedStatement.executeQuery();

                if (!resultSet.next()) {
                    Throwable throwable
                            = new SQLNoResultFoundException(
                                    ExceptionMsg.EMPTY_SQL_RESULT.getMsg());
                    Logger.getLogger(DataProviderAbstract.class.getName())
                            .log(Level.SEVERE, null, throwable);
                    throw new SQLNoResultFoundException(
                            ExceptionMsg.SQL_NO_RESULT_FOUND.getMsg());
                }

                resultSet.beforeFirst();

                this.listOfAnnouncements = new ArrayList<>();

                while (resultSet.next()) {
                    Timestamp timestamp = resultSet.getTimestamp(4);
                    LocalDateTime dateSubmitted = timestamp.toLocalDateTime();

                    AnnouncementType announcementType;
                    String announcementSource;
                    Announcement announcement;

                    if (resultSet.getInt(5) == 2) {
                        String moduleId = resultSet.getString(6);
                        String moduleName = resultSet.getString(7);

                        announcementType = AnnouncementType.CLASS_WIDE;

                        if (MainUI.get().getAccessControl().getUserType()
                                == UserType.STUDENT) {
                            announcementSource = resultSet.getString(8)
                                    + " " + resultSet.getString(9);
                        } else {
                            announcementSource
                                    = MainUI.get().getAccessControl().getFullName();
                        }

                        announcement = new Announcement(
                                resultSet.getInt(1),
                                resultSet.getString(2),
                                resultSet.getString(3),
                                dateSubmitted,
                                announcementType,
                                announcementSource,
                                moduleId,
                                moduleName);
                    } else {
                        announcementType = AnnouncementType.SYSTEM_WIDE;
                        announcementSource = GSM_LONDON;

                        announcement = new Announcement(
                                resultSet.getInt(1),
                                resultSet.getString(2),
                                resultSet.getString(3),
                                dateSubmitted,
                                announcementType,
                                announcementSource);
                    }

                    this.listOfAnnouncements.add(announcement);
                }

                preparedStatement.close();
                releaseConnection();

                return listOfAnnouncements;

            } catch (SQLException ex) {
                Logger.getLogger(DataProviderAbstract.class.getName())
                        .log(Level.SEVERE, null, ex);
                throw new SQLErrorQueryException(
                        ExceptionMsg.SQL_ERROR_QUERY.getMsg());
            }
        } else {
            throw new SQLErrorRetrievingConnectionAndPoolException(
                    ExceptionMsg.SQL_ERROR_CONNECTION.getMsg());
        }

    }

}
