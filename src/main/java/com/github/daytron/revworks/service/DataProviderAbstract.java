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

import com.github.daytron.revworks.exception.SQLErrorRetrievingConnectionAndPoolException;
import com.github.daytron.revworks.exception.SQLErrorQueryException;
import com.github.daytron.revworks.MainUI;
import com.github.daytron.revworks.data.AnnouncementType;
import com.github.daytron.revworks.data.ExceptionMsg;
import com.github.daytron.revworks.data.PreparedQueryStatement;
import com.github.daytron.revworks.data.UserType;
import com.github.daytron.revworks.model.Announcement;
import com.github.daytron.revworks.model.ClassTable;
import com.github.daytron.revworks.model.Coursework;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract class for implementing {@link DataProvider} interface for common
 * data retrieval query process.
 *
 * @author Ryan Gilera
 */
public abstract class DataProviderAbstract extends QueryManagerAbstract
        implements DataProvider {

    private static final String GSM_LONDON = "GSM London";

    private List<Announcement> listOfAnnouncements;
    private Coursework receivedCoursework;

    public DataProviderAbstract() {
        // init empty
        receivedCoursework = null;
    }

    @Override
    public List<Announcement> populateHomeViewWithData() throws
            SQLErrorQueryException, 
            SQLErrorRetrievingConnectionAndPoolException {

        if (reserveConnectionPool()) {
            try {
                
                // Extract the last 3 announcemnts and additional announcements
                // that falls within the last 7 days
                // Max 15 announcements
                PreparedStatement preparedStatement;
                if (MainUI.get().getAccessControl().isUserAStudent()) {
                    preparedStatement = getConnection().prepareStatement(
                            PreparedQueryStatement
                            .STUDENT_SELECT_ANNOUNCEMENTS
                            .getQuery());

                } else {
                    // Otherwise it's a lecturer
                    preparedStatement = getConnection().prepareStatement(
                            PreparedQueryStatement
                            .LECTURER_SELECT_ANNOUNCEMENTS
                            .getQuery());
                }

                preparedStatement.setInt(1,
                        MainUI.get().getAccessControl().getUserId());

                ResultSet resultSet = preparedStatement.executeQuery();

                // Return empty list if no result
                if (!resultSet.next()) {
                    preparedStatement.close();
                    resultSet.close();
                    releaseConnection();
                    
                    return new ArrayList<>();
                }

                resultSet.beforeFirst();

                this.listOfAnnouncements = new ArrayList<>();

                int counter = 0;
                while (resultSet.next()) {
                    counter += 1;
                    
                    Timestamp timestamp = resultSet.getTimestamp(4);
                    LocalDateTime dateSubmitted = timestamp.toLocalDateTime();
                    
                    if (counter > 3) {
                        LocalDateTime now = LocalDateTime.now();
                        int days = (int)ChronoUnit.DAYS.between(dateSubmitted, now);
                        
                        if (days > 7) {
                            break;
                        }
                    }

                    AnnouncementType announcementType;
                    String announcementSource;
                    Announcement announcement;

                    CopyOnWriteArrayList<ClassTable> listOfClassTables 
                            = CurrentUserSession.getCurrentClassTables();
                    
                    if (resultSet.getInt(5) == 2) {
                        int classId = resultSet.getInt(6);
                        
                        ClassTable selectedClassTable = null;
                        for (ClassTable classTable : listOfClassTables) {
                            if (classTable.getId() == classId) {
                                selectedClassTable = classTable;
                                break;
                            }
                        }
                        
                        String moduleId = selectedClassTable.getModuleId();
                        String moduleName = selectedClassTable.getModuleName();

                        announcementType = AnnouncementType.CLASS_WIDE;

                        if (MainUI.get().getAccessControl().getUserType()
                                == UserType.STUDENT) {
                            announcementSource = selectedClassTable.getLecturerUser()
                                    .getFirstName()
                                    + " " + selectedClassTable.getLecturerUser()
                                    .getLastName();
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
                resultSet.close();
  

                return listOfAnnouncements;

            } catch (SQLException ex) {
                Logger.getLogger(DataProviderAbstract.class.getName())
                        .log(Level.SEVERE, null, ex);
                throw new SQLErrorQueryException(
                        ExceptionMsg.SQL_ERROR_QUERY.getMsg());
            } finally {
                releaseConnection();
            }
        } else {
            throw new SQLErrorRetrievingConnectionAndPoolException(
                    ExceptionMsg.SQL_ERROR_CONNECTION.getMsg());
        }

    }
    

    public void setReceivedCoursework(Coursework receivedCoursework) {
        this.receivedCoursework = receivedCoursework;
    }
    
    public Coursework getReceivedCoursework() {
        return receivedCoursework;
    }

}
