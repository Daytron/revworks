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

import com.github.daytron.revworks.model.LecturerUser;
import com.github.daytron.revworks.model.StudentUser;
import com.github.daytron.revworks.model.AdminUser;
import com.github.daytron.revworks.service.SQLConnectionManager;
import com.github.daytron.revworks.data.ExceptionMsg;
import com.github.daytron.revworks.data.PreparedQueryStatement;
import com.github.daytron.revworks.data.UserType;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import java.security.Principal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Responsible for verifying the user login credentials through the MySQL
 * database. It is a singleton class to prevent multiple connections to the
 * database.
 *
 * @author Ryan Gilera
 */
@SuppressWarnings("serial")
public class UserAuthentication {


    private UserAuthentication() {
    }

    /**
     * Returns the one and only one instance of this class.
     *
     * @return The UserAuthentication object
     */
    public static UserAuthentication getInstance() {
        return UserAuthenticationServiceHolder.INSTANCE;
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
            String password) throws AuthenticationException {
        // Try to connect and make query to the database

        JDBCConnectionPool connectionPool;

        try {
            // Create a pool of SQL connections
            connectionPool = SQLConnectionManager.getInstance().connect();
            // Reserve a new connection
            Connection connection = connectionPool.reserveConnection();

            PreparedStatement preparedStatement;

            if (userType == UserType.STUDENT) {
                preparedStatement = connection.prepareStatement(
                        PreparedQueryStatement.LOGIN_USER_STUDENT.getQuery());

            } else if (userType == UserType.LECTURER) {
                preparedStatement = connection.prepareStatement(
                        PreparedQueryStatement.LOGIN_USER_LECTURER.getQuery());
            } else {
                preparedStatement = connection.prepareStatement(
                        PreparedQueryStatement.LOGIN_USER_ADMIN.getQuery());
            }

            // Apply username and password to the query statement
            preparedStatement.setString(1, userfield);
            preparedStatement.setString(2, password);

            // Execute statement and save it to ResultSet object
            ResultSet resultSet = preparedStatement.executeQuery();

            // If there is no user retrieved with the given username and password,
            // throw new AuthenticationException
            if (!resultSet.next()) {
                throw new AuthenticationException(
                        ExceptionMsg.AUTHENTICATION_EXCEPTION_NO_USER.getMsg());
            }

            // Move "cursor" to the first row of resulting query table
            resultSet.first();

            // Retrieve user data from the result row table
            String userID = Integer.toString(resultSet.getInt(1));
            String firstName = resultSet.getString(3);
            String lastName = resultSet.getString(4);

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
            preparedStatement.close();
            // Then release the connection to free idle SQL connection
            connectionPool.releaseConnection(connection);

            return user;
        } catch (SQLException ex) {
            Logger.getLogger(UserAuthentication.class.getName()).log(Level.SEVERE, null, ex);

            throw new AuthenticationException(
                    ExceptionMsg.AUTHENTICATION_EXCEPTION_SYS_ERROR.getMsg());
        }

    }

    /**
     * Private inner class to hold the single object of this singleton class
     */
    private static class UserAuthenticationServiceHolder {

        private static final UserAuthentication INSTANCE = new UserAuthentication();
    }
}
