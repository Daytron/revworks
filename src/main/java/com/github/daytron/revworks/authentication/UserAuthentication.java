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

import com.github.daytron.revworks.ui.constants.ExceptionMsg;
import com.github.daytron.revworks.ui.constants.UserType;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.data.util.sqlcontainer.query.QueryDelegate;
import com.vaadin.ui.Notification;
import java.security.Principal;
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
public class UserAuthentication {

    private static final long serialVersionUID = 1L;

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
     * @param userType
     * @param userfield
     * @param password
     * @return The Principal object (the user) if the connection and query are
     * successful, otherwise returns null.
     */
    Principal authenticate(UserType userType, String userfield,
            String password) throws AuthenticationException {
        // Try to connect and make query to the database
        
        JDBCConnectionPool connectionPool = null;
        
        try {
            connectionPool = new SimpleJDBCConnectionPool(
                    "com.mysql.jdbc.Driver",
                    "jdbc:mysql://localhost/appschema", 
                    "uservalidator", "sqluserpw",2,10);

            
            QueryDelegate userQueryDelegate;

            if (userType == UserType.STUDENT) {
                userQueryDelegate = new FreeformQuery(
                        "select Student.id, User.first_name, User.last_name "
                        + "from Student\n"
                        + "inner join User \n"
                        + "on Student.person_id = User.id\n"
                        + "where Student.id = " + userfield + " and "
                        + "'" + password + "' = Student.password;",
                        connectionPool,
                        "id");
            } else {
                userQueryDelegate = new FreeformQuery(
                        "select Lecturer.id, Lecturer.email, "
                        + "User.first_name, User.last_name "
                        + "from Lecturer\n"
                        + "inner join User \n"
                        + "on Lecturer.person_id = User.id\n"
                        + "where Lecturer.email = '" + userfield + "' and "
                        + "'" + password + "' = Lecturer.password;",
                        connectionPool,
                        "id");
            }

            // Add the query delegate to the SQL container
            SQLContainer personContainer = new SQLContainer(userQueryDelegate);

            if (personContainer.size() < 1) {
                throw new AuthenticationException(
                        ExceptionMsg.INVALID_USER_CREDENTIAL.getMsg());
            }

            // Retrieve the user item
            Item item = personContainer.getItem(personContainer.getIdByIndex(0));

            // Retrieve the user's information 
            Property<Long> userID = item.getItemProperty("id");
            Property<String> firstName = item.getItemProperty("first_name");
            Property<String> lastName = item.getItemProperty("last_name");

            Principal user;
            // Get the String equivalent
            String userIDStr = Long.toString(userID.getValue());
            String firstNameStr = firstName.getValue();
            String lastNameStr = lastName.getValue();
            
            // Create appropriate user
            if (userType == UserType.STUDENT) {
                user = new StudentUser(userIDStr, 
                        userfield, firstNameStr, lastNameStr, userType);
            } else {
                user = new LecturerUser(userIDStr, 
                        userfield, firstNameStr, lastNameStr, userType);
            }
            
            return user;
        } catch (SQLException ex) {
            Logger.getLogger(UserAuthentication.class.getName()).log(Level.SEVERE, null, ex);

            throw new AuthenticationException(ex);
        } finally {
            // Close connection after authentication process is done to minimise
            // idle connections
            if (connectionPool != null) {
                connectionPool.destroy();
            }
        }

    }

    /**
     * Private inner class to hold the single object of this singleton class
     */
    private static class UserAuthenticationServiceHolder {

        private static final UserAuthentication INSTANCE = new UserAuthentication();
    }
}
