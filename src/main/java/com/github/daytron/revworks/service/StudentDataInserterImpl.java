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
import com.github.daytron.revworks.event.AppEvent.*;
import com.github.daytron.revworks.ui.dashboard.student.StudentSubmitCourseworkSucessView;
import com.google.common.eventbus.Subscribe;
import com.google.common.io.Files;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Concrete implementation of {@link StudentDataInserter}
 *
 * @author Ryan Gilera
 */
public class StudentDataInserterImpl extends  DataInserterAbstract 
implements StudentDataInserter {

    @Subscribe
    @Override
    public void insertNewCoursework(StudentSubmitCourseworkEvent event) {
        
        if (reserveConnectionPool()) {
            try {
                PreparedStatement preparedStatement = getConnection()
                        .prepareStatement(PreparedQueryStatement.STUDENT_INSERT_NEW_COURSEWORK.getQuery());
                
                preparedStatement.setString(1, event.getTitle());
                
                String fileExtension = Files.getFileExtension(event.getCourseworkFile().getAbsolutePath());
                fileExtension = fileExtension.toLowerCase();
                preparedStatement.setString(3, fileExtension);
                
                preparedStatement.setInt(4, MainUI.get().getAccessControl().getUserId());
                preparedStatement.setInt(5, event.getClassTable().getId());
                
                // Prepare pdf file to be inserted to the database
                byte[] pdfByte = new byte[(int)event.getCourseworkFile().length()];
                DataInputStream dataInputStream = new DataInputStream((new FileInputStream(event.getCourseworkFile())));
                // Read data into byte array
                dataInputStream.read(pdfByte);
                dataInputStream.close();
                
                preparedStatement.setBytes(2, pdfByte);
                
                // Execute query
                preparedStatement.executeUpdate();
                getConnection().commit();
                
                preparedStatement.close();
                
                
                // switch view to success page
                MainUI.get().getNavigator()
                        .navigateTo(StudentSubmitCourseworkSucessView.VIEW_NAME);
                
                
            } catch (SQLException | FileNotFoundException ex) {
                Logger.getLogger(StudentDataInserterImpl.class.getName())
                        .log(Level.SEVERE, null, ex);
                notifyDataSendError();
            } catch (IOException ex) {
                Logger.getLogger(StudentDataInserterImpl.class.getName())
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
