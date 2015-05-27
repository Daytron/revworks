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
package com.github.daytron.revworks.data;

/**
 * Collection of premade SQL query statements.
 * 
 * @author Ryan Gilera
 */
public enum PreparedQueryStatement {
    LOGIN_USER_STUDENT("SELECT User.id, Student.id, User.first_name, "
                        + "User.last_name "
                        + "FROM Student\n"
                        + "INNER JOIN User \n"
                        + "ON Student.person_id = User.id\n"
                        + "WHERE Student.id = ? and "
                        + " ? = Student.password"),
    LOGIN_USER_LECTURER("SELECT User.id, Lecturer.id, "
                        + "User.first_name, User.last_name "
                        + "FROM Lecturer\n"
                        + "INNER JOIN User \n"
                        + "ON Lecturer.person_id = User.id\n"
                        + "WHERE Lecturer.email = ? and "
                        + "? = Lecturer.password;"),
    LOGIN_USER_ADMIN("SELECT User.id, Admin.id, "
                        + "User.first_name, User.last_name "
                        + "FROM Admin\n"
                        + "INNER JOIN User \n"
                        + "ON Admin.person_id = User.id\n"
                        + "WHERE Admin.email = ? and "
                        + "? = Admin.password;");
    
    private final String query;

    private PreparedQueryStatement(String script) {
        this.query = script;
    }

    public String getQuery() {
        return query;
    }
    
    
}
