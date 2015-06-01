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
    LOGIN_USER_STUDENT("SELECT User.id, Student.student_id, User.first_name, "
                        + "User.last_name "
                        + "FROM Student\n"
                        + "INNER JOIN User \n"
                        + "ON Student.user_id = User.id\n"
                        + "WHERE Student.student_id = ? and "
                        + " ? = Student.password"),
    LOGIN_USER_LECTURER("SELECT User.id, Lecturer.email, "
                        + "User.first_name, User.last_name "
                        + "FROM Lecturer\n"
                        + "INNER JOIN User \n"
                        + "ON Lecturer.user_id = User.id\n"
                        + "WHERE Lecturer.email = ? and "
                        + "? = Lecturer.password;"),
    LOGIN_USER_ADMIN("SELECT User.id, Admin.email, "
                        + "User.first_name, User.last_name "
                        + "FROM Admin\n"
                        + "INNER JOIN User \n"
                        + "ON Admin.user_id = User.id\n"
                        + "WHERE Admin.email = ? and "
                        + "? = Admin.password;"),
    
    ANNOUNCEMENT_STUDENT_QUERY("SELECT Announcement.id AS id, "
            + "Announcement.title AS title, Announcement.message AS message, " 
            + "Announcement.date_submitted AS dateSubmitted, "
            + "Announcement.announcement_type_id AS announcementTypeId, "
            + "Module.id AS moduleId, Module.name AS moduleName, " 
            + "User.first_name AS lecturer_first, User.last_name AS lecturer_last "
            + "FROM Announcement " 
            + "LEFT JOIN ClassWideAnnouncement "
            + "ON Announcement.id = ClassWideAnnouncement.id " 
            + "LEFT JOIN Class "
            + "ON Class.id = ClassWideAnnouncement.class_id " 
            + "LEFT JOIN Lecturer "
            + "ON Lecturer.user_id = Class.lecturer_id "
            + "LEFT JOIN User "
            + "ON User.id = Lecturer.user_id "
            + "LEFT JOIN StudentClass "
            + "ON StudentClass.class_id = Class.id "
            + "LEFT JOIN Module ON Module.id = Class.module_id " 
            + "WHERE (StudentClass.user_id = ? "
            + "OR Announcement.announcement_type_id = 1) AND "
            + "(DATE(Announcement.date_submitted)  >= (NOW() - INTERVAL 7 DAY)) " 
            + "ORDER BY date_submitted DESC;"),
    
    ANNOUNCEMENT_LECTURER_QUERY("SELECT Announcement.id AS id, "
            + "Announcement.title AS title, Announcement.message AS message, "
            + "Announcement.date_submitted AS dateSubmitted, "
            + "Announcement.announcement_type_id AS announcementTypeId, "
            + "Module.id AS moduleId, Module.name AS moduleName " 
            + "FROM Announcement " 
            + "LEFT JOIN ClassWideAnnouncement "
            + "ON Announcement.id = ClassWideAnnouncement.id " 
            + "LEFT JOIN Class ON Class.id = ClassWideAnnouncement.class_id "
            + "LEFT JOIN Module ON Module.id = Class.module_id " 
            + "WHERE (Class.lecturer_id = ? "
            + "OR Announcement.announcement_type_id = 1) AND "
            + "(DATE(Announcement.date_submitted)  >= (NOW() - INTERVAL 7 DAY)) " 
            + "ORDER BY date_submitted DESC;");
    
    private final String query;

    private PreparedQueryStatement(String script) {
        this.query = script;
    }

    public String getQuery() {
        return query;
    }
    
    
}
