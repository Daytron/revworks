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
import com.github.daytron.revworks.data.ExceptionMsg;
import com.github.daytron.revworks.data.FilePath;
import com.github.daytron.revworks.data.PreparedQueryStatement;
import com.github.daytron.revworks.exception.SQLErrorQueryException;
import com.github.daytron.revworks.exception.SQLErrorRetrievingConnectionAndPoolException;
import com.github.daytron.revworks.exception.SQLNoResultFoundException;
import com.github.daytron.revworks.model.ClassTable;
import com.github.daytron.revworks.model.Coursework;
import com.github.daytron.revworks.model.LecturerUser;
import com.google.common.io.Files;
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Concrete implementation of {@link StudentDataProvider}.
 *
 * @author Ryan Gilera
 */
public class StudentDataProviderImpl extends DataProviderAbstract
        implements StudentDataProvider {

    private List<ClassTable> listOfClasses;

    public StudentDataProviderImpl() {
        super();
    }

    public static StudentDataProviderImpl get() {
        return StudentDataProviderHolder.INSTANCE;
    }

    @Override
    public List<ClassTable> extractClassData() throws SQLErrorRetrievingConnectionAndPoolException, SQLErrorQueryException, SQLNoResultFoundException {
        if (reserveConnectionPool()) {
            try {
                final PreparedStatement preparedStatement = getConnection()
                        .prepareStatement(
                                PreparedQueryStatement.STUDENT_CLASS_SELECT_QUERY.getQuery());

                preparedStatement.setInt(1,
                        MainUI.get().getAccessControl().getUserId());

                ResultSet resultSet = preparedStatement.executeQuery();

                if (!resultSet.next()) {
                    Throwable throwable
                            = new SQLNoResultFoundException(
                                    ExceptionMsg.EMPTY_SQL_RESULT.getMsg());
                    Logger.getLogger(StudentDataProviderImpl.class.getName())
                            .log(Level.SEVERE, null, throwable);
                    throw new SQLNoResultFoundException(
                            ExceptionMsg.SQL_NO_RESULT_FOUND.getMsg());
                }

                resultSet.beforeFirst();
                this.listOfClasses = new ArrayList<>();

                while (resultSet.next()) {
                    this.listOfClasses.add(new ClassTable(resultSet.getInt(1),
                            resultSet.getString(2),
                            resultSet.getString(3)));
                }

                preparedStatement.close();
                getConnectionPool().releaseConnection(getConnection());

                return this.listOfClasses;

            } catch (SQLException ex) {
                Logger.getLogger(StudentDataProviderImpl.class.getName())
                        .log(Level.SEVERE, null, ex);
                throw new SQLErrorQueryException(
                        ExceptionMsg.SQL_ERROR_QUERY.getMsg());
            }
        } else {
            throw new SQLErrorRetrievingConnectionAndPoolException(
                    ExceptionMsg.SQL_ERROR_CONNECTION.getMsg());
        }
    }

    @Override
    public List<Coursework> extractCourseworkData() throws SQLErrorRetrievingConnectionAndPoolException, SQLErrorQueryException, SQLNoResultFoundException, FileNotFoundException, IOException {
        if (reserveConnectionPool()) {
            try {

                final PreparedStatement preparedStatement
                        = getConnection().prepareStatement(
                                PreparedQueryStatement.STUDENT_SELECT_COURSEWORK
                                .getQuery());

                preparedStatement.setString(1,
                        CurrentUserSession.getCurrentSemester());
                preparedStatement.setInt(2,
                        MainUI.get().getAccessControl().getUserId());

                ResultSet resultSet = preparedStatement.executeQuery();

                // Return an empty list if no courseworks found
                if (!resultSet.next()) {
                    return new ArrayList<>();
                }

                resultSet.beforeFirst();
                final List<Coursework> listOfCourseworks = new ArrayList<>();

                // Cycle through the SQL query result
                while (resultSet.next()) {

                    // get the bytes data from the resultset
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
                            + FilePath.PDF_OUTPUT_NAME.getPath()
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

                    LecturerUser lecturer
                            = new LecturerUser(
                                    resultSet.getInt(9),
                                    resultSet.getString(10),
                                    resultSet.getString(11),
                                    resultSet.getString(12));

                    listOfCourseworks.add(new Coursework(resultSet.getInt(1),
                            resultSet.getString(2),
                            dateSubmitted,
                            pdfFile,
                            fileExtension,
                            resultSet.getInt(6),
                            resultSet.getString(7),
                            resultSet.getString(8),
                            lecturer));
                }

                preparedStatement.close();
                getConnectionPool().releaseConnection(getConnection());

                return listOfCourseworks;

            } catch (SQLException ex) {
                Logger.getLogger(StudentDataProviderImpl.class.getName()).log(Level.SEVERE, null, ex);
                throw new SQLErrorQueryException(
                        ExceptionMsg.SQL_ERROR_QUERY.getMsg());
            } catch (FileNotFoundException ex) {
                Logger.getLogger(StudentDataProviderImpl.class.getName()).log(Level.SEVERE, null, ex);
                throw ex;
            } catch (IOException ex) {
                Logger.getLogger(StudentDataProviderImpl.class.getName()).log(Level.SEVERE, null, ex);
                throw ex;
            }
        } else {
            throw new SQLErrorRetrievingConnectionAndPoolException(
                    ExceptionMsg.SQL_ERROR_CONNECTION.getMsg());
        }
    }

    private static class StudentDataProviderHolder {

        private static final StudentDataProviderImpl INSTANCE = new StudentDataProviderImpl();
    }

}
