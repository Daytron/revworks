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
import com.github.daytron.revworks.data.ExceptionMsg;
import com.github.daytron.revworks.data.FilePath;
import com.github.daytron.revworks.data.PreparedQueryStatement;
import com.github.daytron.revworks.event.AppEvent.*;
import com.github.daytron.revworks.exception.SQLErrorQueryException;
import com.github.daytron.revworks.exception.SQLErrorRetrievingConnectionAndPoolException;
import com.github.daytron.revworks.exception.SQLErrorUpdateException;
import com.github.daytron.revworks.model.ClassTable;
import com.github.daytron.revworks.model.Coursework;
import com.github.daytron.revworks.model.StudentUser;
import com.github.daytron.revworks.model.User;
import com.github.daytron.revworks.util.NotificationUtil;
import com.github.daytron.revworks.view.main.CourseworkView;
import com.google.common.eventbus.Subscribe;
import com.google.common.io.Files;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.VaadinService;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Concrete implementation of {@link StudentDataProvider}.
 *
 * @author Ryan Gilera
 */
public class StudentDataProviderImpl extends DataProviderAbstract
        implements StudentDataProvider {

    public StudentDataProviderImpl() {
        super();
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public BeanItemContainer<Coursework> extractCourseworkData() throws SQLErrorRetrievingConnectionAndPoolException, SQLErrorQueryException,
            FileNotFoundException, IOException {
        CopyOnWriteArrayList<ClassTable> listOfClassTables
                = CurrentUserSession.getCurrentClassTables();

        if (listOfClassTables.isEmpty()) {
            return new BeanItemContainer<>(Coursework.class);
        }

        if (reserveConnectionPool()) {
            try {
                final List<Coursework> listOfCourseworks = new ArrayList<>();

                for (ClassTable classTable : listOfClassTables) {
                    final PreparedStatement preparedStatement
                            = getConnection().prepareStatement(
                                    PreparedQueryStatement.STUDENT_SELECT_COURSEWORK
                                    .getQuery());

                    preparedStatement.setInt(1, classTable.getId());
                    preparedStatement.setInt(2,
                            ((User) CurrentUserSession.getPrincipal()).getId());

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        // Skip to the next class if there are no coursework
                        // submitted for this class
                        if (!resultSet.next()) {
                            preparedStatement.close();
                            resultSet.close();

                            continue;
                        }

                        resultSet.beforeFirst();

                        // Cycle through the SQL query result
                        while (resultSet.next()) {

                            // Extract the coursework id
                            int courseworkID = resultSet.getInt(1);

                            // Then before extracting the remaining details
                            // Check first if the corresponding notes are read 
                            // or unread then update the coursework is_read field
                            PreparedStatement preparedStatementNote
                                    = getConnection().prepareStatement(
                                            PreparedQueryStatement.SELECT_NOTE.getQuery());
                            preparedStatementNote.setInt(1, courseworkID);

                            boolean isReadStudentCoursework = resultSet.getBoolean(6);
                            
                            // Default true 
                            // if all notes are true then this should be true
                            // otherwise this is updated to false further 
                            // down the code
                            boolean isReadStudentNote = true;
                            
                            ResultSet resultSetNote = preparedStatementNote
                                    .executeQuery();
                            if (resultSetNote.next()) {
                                resultSetNote.beforeFirst();

                                while (resultSetNote.next()) {
                                    isReadStudentNote
                                            = resultSetNote.getBoolean(5);

                                    // If at least one of the note is not
                                    // read yet, break
                                    if (isReadStudentNote == false) {
                                        break;
                                    }
                                }

                                // Close first 
                                preparedStatementNote.close();
                                resultSetNote.close();
                                
                                // Then update coursework student is_read field
                                // If isReadStudentCoursework is not up to date,
                                // update it with isReadStudentNote
                                if (isReadStudentNote != isReadStudentCoursework) {
                                    // Save it first
                                    isReadStudentCoursework = isReadStudentNote;
                                    
                                    // Then update
                                    try (PreparedStatement preparedStatementUpdate
                                            = getConnection().prepareStatement(
                                                    PreparedQueryStatement.STUDENT_UPDATE_COURSEWORK_IS_READ
                                                    .getQuery())) {
                                                preparedStatementUpdate.setBoolean(1,
                                                        isReadStudentCoursework);
                                                preparedStatementUpdate.setInt(2, courseworkID);

                                                preparedStatementUpdate.executeUpdate();
                                                getConnection().commit();
                                            }
                                }
                            }

                            // Get the bytes data (pdf) from the resultset
                            byte[] pdfData = resultSet.getBytes(4);
                            
                            // Retrieve file extension
                            String fileExtension = resultSet.getString(5);

                            // Prepare destination path of the pdf file
                            String basePath = VaadinService.getCurrent()
                                    .getBaseDirectory().getAbsolutePath()
                                    + FilePath.TEMP_FILE_HOLDER.getPath();

                            // Generate random filename
                            // so it won't overwrite by the next retrieve file
                            String randomStringForFilename = UUID.randomUUID().toString();

                            String pdfFilename = basePath
                                    + FilePath.FILE_OUTPUT_NAME.getPath()
                                    + "/" + randomStringForFilename + "."
                                    + fileExtension;

                            File pdfFile = new File(pdfFilename);
                            pdfFile.getParentFile().mkdirs();
                            pdfFile.createNewFile();

                            // Begin file write from the bytes retrieved to the newly
                            // created file with random UUID as filename
                            Files.write(pdfData, pdfFile);

                            // Add to trashbin for later file cleanup
                            CurrentUserSession.getFileTrashBin().add(pdfFile);

                            // Convert SQL DATETIME datatype to Java 8's LocalDateTime
                            Timestamp timestamp = resultSet.getTimestamp(3);
                            LocalDateTime dateSubmitted = timestamp.toLocalDateTime();

                            listOfCourseworks.add(new Coursework(courseworkID,
                                    resultSet.getString(2),
                                    dateSubmitted,
                                    pdfFile,
                                    fileExtension,
                                    classTable,
                                    (StudentUser) CurrentUserSession.getPrincipal(),
                                    isReadStudentCoursework,
                                    resultSet.getBoolean(7)));
                        }

                        preparedStatement.close();
                    }
                }

                BeanItemContainer<Coursework> courseworksContainer
                        = new BeanItemContainer<>(Coursework.class);
                courseworksContainer.addAll(listOfCourseworks);

                return courseworksContainer;
            } catch (SQLException ex) {
                Logger.getLogger(StudentDataProviderImpl.class.getName())
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

    /**
     * Saves the coursework data from coursework module view (table view) for 
     * later used in the coursework view. The coursework read status is updated 
     * to true in the database. Then the user is rerouted to the coursework 
     * view if successfully updated its status, otherwise page transition is 
     * skipped entirely and displays an error notification to the user.
     * 
     * @param event a custom event object defined in {@link com.github.daytron.revworks.event.AppEvent} class
     */
    @Subscribe
    public void receiveCourseworkDataFromTable(final StudentViewCourseworkEvent event) {
        setReceivedCoursework(event.getCoursework());

        /*
         Before switching to coursework view
         Set is_read field to true.
        
         This turn to true by default regardless of note is_read field
         whenever user open this coursework because some courseworks
         may not have notes in it.
        
         The courseworks is-read fields are re-evaluated or updated properly 
         when user view goes (back) to coursework table view.
        
         Non-fatal SIDE EFFECT: coursework is_read field is not properly
         updated until user open coursework table view. No issues for users since 
         it is updated when they open the table view.
        
         Reason for this decision: it is expensive to implement real-time 
         coursework update while there are already real-time updates on 
         notes (both updating is_read field and retrieving data) 
         (spawning new threads) and real-time updates on notifications in placed.
        
         Until the server is upgraded to support huge real-time thread 
         operations, the current feature is suffice. 
         */
        if (reserveConnectionPool()) {
            try (PreparedStatement preparedStatementUpdate
                    = getConnection().prepareStatement(
                            PreparedQueryStatement.STUDENT_UPDATE_COURSEWORK_IS_READ
                            .getQuery())) {
                        preparedStatementUpdate.setBoolean(1, true);
                        preparedStatementUpdate.setInt(2,
                                event.getCoursework().getId());

                        preparedStatementUpdate.executeUpdate();
                        getConnection().commit();
                    } catch (SQLException ex) {
                        Logger.getLogger(StudentDataProviderImpl.class
                                .getName()).log(Level.SEVERE, null, ex);

                        NotificationUtil.showError(
                                ErrorMsg.DATA_UPDATE_ERROR.getText(),
                                ErrorMsg.CONSULT_YOUR_ADMIN.getText());
                        releaseConnection();

                        return;
                    } finally {
                        releaseConnection();
                    }
        } else {
            SQLErrorUpdateException ex = new SQLErrorUpdateException(
                    ExceptionMsg.SQL_ERROR_CONNECTION.getMsg());
            Logger.getLogger(StudentDataProviderImpl.class.getName())
                    .log(Level.SEVERE, null, ex);

            NotificationUtil.showError(
                    ErrorMsg.DATA_UPDATE_ERROR.getText(),
                    ErrorMsg.CONSULT_YOUR_ADMIN.getText());

            return;
        }

        MainUI.get().getNavigator().navigateTo(CourseworkView.VIEW_NAME);
    }

}
