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

import com.github.daytron.revworks.data.ExceptionMsg;
import com.github.daytron.revworks.data.FilePath;
import com.github.daytron.revworks.data.PreparedQueryStatement;
import com.github.daytron.revworks.exception.NoClassAttachedToLecturerException;
import com.github.daytron.revworks.exception.SQLErrorQueryException;
import com.github.daytron.revworks.exception.SQLErrorRetrievingConnectionAndPoolException;
import com.github.daytron.revworks.model.ClassTable;
import com.github.daytron.revworks.model.Coursework;
import com.github.daytron.revworks.model.StudentUser;
import com.google.common.io.Files;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.VaadinService;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Concrete implementation of {@link LecturerDataProvider}.
 *
 * @author Ryan Gilera
 */
public class LecturerDataProviderImpl extends DataProviderAbstract
        implements LecturerDataProvider {

    private LecturerDataProviderImpl() {
        super();
    }

    public static LecturerDataProviderImpl get() {
        return LecturerDataProviderHolder.INSTANCE;
    }

    @Override
    public ConcurrentHashMap<ClassTable,BeanItemContainer> extractCourseworkData() throws
            SQLErrorRetrievingConnectionAndPoolException,
            SQLErrorQueryException, NoClassAttachedToLecturerException {
        CopyOnWriteArrayList<ClassTable> listOfClassTables = 
                CurrentUserSession.getCurrentClassTables();
        if (listOfClassTables.isEmpty()) {
            return new ConcurrentHashMap<>();
        }
        
        if (reserveConnectionPool()) {

            try {
                
                // Pull courseworks for each class resulting
                // to a BeanItemContainer and save it to a List object
                // Then pass those opbjects to BeanItemContainer
                final ConcurrentHashMap<ClassTable,BeanItemContainer> 
                        listOfBeanItemContainers = new ConcurrentHashMap<>();

                for (ClassTable classTable : listOfClassTables) {
                    final PreparedStatement preparedStatementCoursework
                            = getConnection().prepareStatement(
                                    PreparedQueryStatement.LECTURER_SELECT_COURSEWORK
                                    .getQuery());

                    preparedStatementCoursework.setInt(1, classTable.getId());

                    ResultSet resultSetCoursework
                            = preparedStatementCoursework.executeQuery();

                    // If no courseworks found for a particular class
                    // add an empty BeanItemContainer object and
                    // close statement and resultset
                    if (!resultSetCoursework.next()) {
                        preparedStatementCoursework.close();
                        resultSetCoursework.close();
                        
                        listOfBeanItemContainers.put(classTable,
                                new BeanItemContainer(Coursework.class));
                        continue;
                    }

                    resultSetCoursework.beforeFirst();
                    BeanItemContainer<Coursework> beanItemContainer = 
                            new BeanItemContainer<>(Coursework.class);
                    while (resultSetCoursework.next()) {
                        // getPrincipal the bytes data from the resultset
                        byte[] pdfData = resultSetCoursework.getBytes(4);

                        // Retrieve file extension
                        String fileExtension = resultSetCoursework.getString(5);

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
                        try {
                            pdfFile.createNewFile();

                            // Begin file write from the bytes retrieved to the newly 
                            // created file with random UUID as filename
                            Files.write(pdfData, pdfFile);

                            // Add to trashbin for later file cleanup
                            CurrentUserSession.getFileTrashBin().add(pdfFile);

                        } catch (IOException ex) {
                            Logger.getLogger(LecturerDataProviderImpl.class.getName()).log(Level.SEVERE, null, ex);

                            // If unable to recreate the file,
                            // skip to next coursework
                            continue;
                        }

                        // Convert SQL DATETIME datatype to Java 8's LocalDateTime
                        Timestamp timestamp = resultSetCoursework.getTimestamp(3);
                        LocalDateTime dateSubmitted = timestamp.toLocalDateTime();

                        StudentUser studentUser = new StudentUser(
                                resultSetCoursework.getInt(6),
                                Integer.toString(resultSetCoursework.getInt(7)),
                                resultSetCoursework.getString(8),
                                resultSetCoursework.getString(9));

                        beanItemContainer.addBean(new Coursework(
                                resultSetCoursework.getInt(1),
                                resultSetCoursework.getString(2),
                                dateSubmitted,
                                pdfFile, fileExtension,
                                classTable,
                                studentUser));

                    }
                    
                    listOfBeanItemContainers.put(classTable, beanItemContainer);

                    preparedStatementCoursework.close();
                    resultSetCoursework.close();

                }

                releaseConnection();
                return listOfBeanItemContainers;

            } catch (SQLException ex) {
                Logger.getLogger(LecturerDataProviderImpl.class.getName()).log(Level.SEVERE, null, ex);
                releaseConnection();
                throw new SQLErrorQueryException(
                        ExceptionMsg.SQL_ERROR_QUERY.getMsg());
            }
        } else {
            throw new SQLErrorRetrievingConnectionAndPoolException(
                    ExceptionMsg.SQL_ERROR_CONNECTION.getMsg());
        }
    }

    private static class LecturerDataProviderHolder {

        private static final LecturerDataProviderImpl INSTANCE = new LecturerDataProviderImpl();
    }
}
