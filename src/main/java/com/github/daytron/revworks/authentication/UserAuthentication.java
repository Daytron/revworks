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
package com.github.daytron.revworks.authentication;

import com.github.daytron.revworks.exception.AuthenticationException;
import com.github.daytron.revworks.model.LecturerUser;
import com.github.daytron.revworks.model.StudentUser;
import com.github.daytron.revworks.model.AdminUser;
import com.github.daytron.revworks.data.ExceptionMsg;
import com.github.daytron.revworks.data.PreparedQueryStatement;
import com.github.daytron.revworks.data.UserType;
import com.github.daytron.revworks.exception.SQLErrorQueryException;
import com.github.daytron.revworks.exception.SQLErrorRetrievingConnectionAndPoolException;
import com.github.daytron.revworks.model.ClassTable;
import com.github.daytron.revworks.model.User;
import com.github.daytron.revworks.service.QueryManagerAbstract;
import java.security.Principal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Responsible for verifying the user login credentials through the MySQL
 * database. It also extracts class table and verify semester of the signed user.
 *
 * @author Ryan Gilera
 */
@SuppressWarnings("serial")
public class UserAuthentication extends QueryManagerAbstract {

    public UserAuthentication() {
    }

    /**
     * Connects to the database and retrieve user from it.
     *
     * @param userType A UserType object
     * @param userfield String for username field
     * @param password String for password
     * @return The Principal object (the user) if the connection and query are
     * successful, otherwise returns null.
     */
    Principal authenticate(UserType userType, String userfield,
            String password) throws AuthenticationException, SQLErrorRetrievingConnectionAndPoolException {

        if (reserveConnectionPool()) {

            try {
                // Begin user credentials verification
                PreparedStatement preparedStatementUser;

                if (userType == UserType.STUDENT) {
                    preparedStatementUser = getConnection().prepareStatement(
                            PreparedQueryStatement.LOGIN_USER_STUDENT.getQuery());

                } else if (userType == UserType.LECTURER) {
                    preparedStatementUser = getConnection().prepareStatement(
                            PreparedQueryStatement.LOGIN_USER_LECTURER.getQuery());
                } else {
                    preparedStatementUser = getConnection().prepareStatement(
                            PreparedQueryStatement.LOGIN_USER_ADMIN.getQuery());
                }

                // Apply username and password to the query statement
                preparedStatementUser.setString(1, userfield);
                preparedStatementUser.setString(2, password);

                // Execute statement and save it to ResultSet object
                ResultSet resultSetUser = preparedStatementUser.executeQuery();

                // If there is no user retrieved with the given username and password,
                // throw new AuthenticationException
                if (!resultSetUser.next()) {
                    preparedStatementUser.close();
                    resultSetUser.close();
                    releaseConnection();

                    throw new AuthenticationException(
                            ExceptionMsg.AUTHENTICATION_EXCEPTION_NO_USER.getMsg());
                }

                // Move "cursor" to the first row of resulting query table
                resultSetUser.first();

                // Retrieve user data from the result row table
                int userID = resultSetUser.getInt(1);
                String firstName = resultSetUser.getString(3);
                String lastName = resultSetUser.getString(4);

                // Then select the classes associated with it
                // Create appropriate user
                Principal user;
                if (userType == UserType.STUDENT) {
                    user = new StudentUser(userID, userfield, firstName, lastName);

                } else if (userType == UserType.LECTURER) {
                    user = new LecturerUser(userID, userfield,
                            firstName, lastName);
                } else {
                    user = new AdminUser(userID, userfield, firstName,
                            lastName);
                }

                // Close the statement after using it, to free up memory
                preparedStatementUser.close();
                resultSetUser.close();
                // Then release the connection to free idle SQL connection
                releaseConnection();

                return user;
            } catch (SQLException ex) {
                Logger.getLogger(UserAuthentication.class.getName()).log(Level.SEVERE, null, ex);
                releaseConnection();
                throw new AuthenticationException(
                        ExceptionMsg.AUTHENTICATION_EXCEPTION_SYS_ERROR.getMsg());
            }

        } else {
            throw new SQLErrorRetrievingConnectionAndPoolException(
                    ExceptionMsg.SQL_ERROR_CONNECTION.getMsg());
        }

    }

    /**
     * Retrieve Retrieve current classes associated with the current user within
     * the current semester. Returns empty list of no class found.
     *
     * @param userType A UserType object
     * @param user Principal object
     * @param semesterID current semesterID
     * @return
     * @throws SQLErrorRetrievingConnectionAndPoolException
     * @throws SQLErrorQueryException
     */
    CopyOnWriteArrayList<ClassTable> extractClassTables(UserType userType, Principal user, String semesterID) throws SQLErrorRetrievingConnectionAndPoolException, SQLErrorQueryException {
        if (userType == UserType.ADMIN) {
            return new CopyOnWriteArrayList<>();
        }

        if (semesterID.isEmpty()) {
            return new CopyOnWriteArrayList<>();
        }

        if (reserveConnectionPool()) {
            try {

                if (userType == UserType.STUDENT) {
                    PreparedStatement preparedStatementClass
                            = getConnection().prepareStatement(
                                    PreparedQueryStatement.STUDENT_SELECT_CLASS.getQuery());
                    User studentUser = (User) user;
                    System.out.println("Student user ID" + studentUser.getId());

                    preparedStatementClass.setInt(1, studentUser.getId());
                    preparedStatementClass.setString(2, semesterID);

                    ResultSet resultSetStudentClass = preparedStatementClass.executeQuery();

                    // If no registered class give empty list of classes
                    if (!resultSetStudentClass.next()) {
                        preparedStatementClass.close();
                        resultSetStudentClass.close();
                        releaseConnection();

                        return new CopyOnWriteArrayList<>();
                    }

                    CopyOnWriteArrayList<ClassTable> listOfClassTables
                            = new CopyOnWriteArrayList<>();
                    resultSetStudentClass.beforeFirst();

                    while (resultSetStudentClass.next()) {
                        listOfClassTables.add(
                                new ClassTable(
                                        resultSetStudentClass.getInt(1),
                                        resultSetStudentClass.getString(2),
                                        resultSetStudentClass.getString(3),
                                        new LecturerUser(
                                                resultSetStudentClass.getInt(4),
                                                resultSetStudentClass.getString(5),
                                                resultSetStudentClass.getString(6),
                                                resultSetStudentClass.getString(7))));
                    }

                    preparedStatementClass.close();
                    resultSetStudentClass.close();
                    releaseConnection();

                    return listOfClassTables;

                    // Otherwise it's a lecturer user
                    // Note: admin user are filtered on top
                } else {
                    PreparedStatement preparedStatementLecturerClass
                            = getConnection().prepareStatement(
                                    PreparedQueryStatement.LECTURER_SELECT_CLASS.getQuery());
                    LecturerUser lecturerUser = (LecturerUser) user;
                    User userUser = (User) user;
                    preparedStatementLecturerClass
                            .setInt(1, userUser.getId());
                    preparedStatementLecturerClass.setString(2, semesterID);

                    ResultSet resultSetLecturerClass
                            = preparedStatementLecturerClass.executeQuery();

                    if (!resultSetLecturerClass.next()) {
                        preparedStatementLecturerClass.close();
                        resultSetLecturerClass.close();
                        releaseConnection();

                        return new CopyOnWriteArrayList<>();
                    }

                    CopyOnWriteArrayList<ClassTable> listOfClassTables
                            = new CopyOnWriteArrayList<>();
                    resultSetLecturerClass.beforeFirst();

                    while (resultSetLecturerClass.next()) {
                        listOfClassTables.add(
                                new ClassTable(resultSetLecturerClass.getInt(1),
                                        resultSetLecturerClass.getString(2),
                                        resultSetLecturerClass.getString(3),
                                        lecturerUser));
                    }

                    preparedStatementLecturerClass.close();
                    resultSetLecturerClass.close();
                    releaseConnection();

                    return listOfClassTables;

                }

            } catch (SQLException ex) {
                Logger.getLogger(UserAuthentication.class.getName())
                        .log(Level.SEVERE, null, ex);
                releaseConnection();
                throw new SQLErrorQueryException(
                        ExceptionMsg.SQL_ERROR_QUERY.getMsg());
            }

        } else {
            throw new SQLErrorRetrievingConnectionAndPoolException(
                    ExceptionMsg.SQL_ERROR_CONNECTION.getMsg());
        }
    }

    /**
     * Retrieve the current semester id based on date it was accessed. Returns
     * an empty String if accessed outside the semester date range.
     *
     * @return The semester id
     * @throws AuthenticationException If the connection to database fails
     */
    String verifyCurrentDateWithinASemester() throws AuthenticationException, SQLErrorRetrievingConnectionAndPoolException {

        if (reserveConnectionPool()) {
            try {

                // Select the current semester firsl
                PreparedStatement preparedStatementSemester = getConnection()
                        .prepareStatement(
                                PreparedQueryStatement.SELECT_CURRENT_SEMESTER
                                .getQuery());

                ResultSet resultSetSemester = preparedStatementSemester.executeQuery();

                if (!resultSetSemester.next()) {
                    preparedStatementSemester.close();
                    resultSetSemester.close();
                    releaseConnection();

                    return "";
                } else {
                    resultSetSemester.first();
                    String semesterID = resultSetSemester.getString(1).toUpperCase();

                    preparedStatementSemester.close();
                    resultSetSemester.close();
                    releaseConnection();

                    return semesterID;
                }

            } catch (SQLException ex) {
                Logger.getLogger(UserAuthentication.class.getName())
                        .log(Level.SEVERE, null, ex);
                releaseConnection();
                throw new AuthenticationException(
                        ExceptionMsg.AUTHENTICATION_EXCEPTION_SYS_ERROR.getMsg());
            }
        } else {
            throw new SQLErrorRetrievingConnectionAndPoolException(
                    ExceptionMsg.SQL_ERROR_CONNECTION.getMsg());
        }

    }

}
