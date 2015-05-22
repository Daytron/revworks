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
 *
 * @author Ryan Gilera
 */
public class UserAuthentication {
    private static final long serialVersionUID = 1L;
    
    private UserAuthentication() {
    }
    
    public static UserAuthentication getInstance() {
        return UserAuthenticationServiceHolder.INSTANCE;
    }
    
    Principal authenticate(UserType userType, String userfield, 
            String password){
        try {
            JDBCConnectionPool connectionPool = new SimpleJDBCConnectionPool(
                    "com.mysql.jdbc.Driver", 
                    "jdbc:mysql://localhost/appschema", "uservalidator","sqluserpw");
            
            QueryDelegate userQueryDelegate = new FreeformQuery(
                    "select Student.id, User.first_name, User.last_name "
                            + "from Student\n" 
                            + "inner join User \n" 
                            + "on Student.person_id = User.id\n" 
                            + "where '"+ password + "' = Student.password;", 
                    connectionPool, 
                    "id");
            
            SQLContainer personContainer = new SQLContainer(userQueryDelegate);
            Item item = personContainer.getItem(personContainer.getIdByIndex(0));
            Property<Long> userID = item.getItemProperty("id");
            Property<String> firstName = item.getItemProperty("first_name");
            Property<String> lastName = item.getItemProperty("last_name");
            
            
            return new User(Long.toString(userID.getValue()), firstName.getValue(), 
                    lastName.getValue(), userType);
        } catch (SQLException ex) {
            Notification.show("SQL Exception occurred", Notification.Type.ERROR_MESSAGE);
            Logger.getLogger(UserAuthentication.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("");
           return null;
        }
        
        
        
    }
    
    private static class UserAuthenticationServiceHolder {

        private static final UserAuthentication INSTANCE = new UserAuthentication();
    }
}
