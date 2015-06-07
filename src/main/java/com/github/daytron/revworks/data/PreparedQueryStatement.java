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
            + "FROM Student "
            + "INNER JOIN User "
            + "ON Student.user_id = User.id "
            + "WHERE Student.student_id = ? and "
            + "? = Student.password"),
    LOGIN_USER_LECTURER("SELECT User.id, Lecturer.email, "
            + "User.first_name, User.last_name "
            + "FROM Lecturer "
            + "INNER JOIN User "
            + "ON Lecturer.user_id = User.id "
            + "WHERE Lecturer.email = ? and "
            + "? = Lecturer.password;"),
    LOGIN_USER_ADMIN("SELECT User.id, Admin.email, "
            + "User.first_name, User.last_name "
            + "FROM Admin "
            + "INNER JOIN User "
            + "ON Admin.user_id = User.id "
            + "WHERE Admin.email = ? and "
            + "? = Admin.password;"),
    SELECT_CURRENT_SEMESTER("SELECT Semester.id FROM Semester "
            + "WHERE (curdate() BETWEEN Semester.startDate "
            + "AND Semester.endDate);"),
    STUDENT_ANNOUNCEMENT_SELECT_QUERY("SELECT Announcement.id AS id, "
            + "Announcement.title AS title, "
            + "Announcement.message AS message,  "
            + "Announcement.date_submitted AS dateSubmitted, "
            + "Announcement.announcement_type_id AS announcementTypeId, "
            + "CustomTable.module_id AS moduleId, "
            + "CustomTable.module_name AS moduleName, "
            + "CustomTable.lecturer_first_name AS lecturer_first, "
            + "CustomTable.lecturer_last_name AS lecturer_last "
            + "FROM Announcement "
            + "LEFT JOIN "
            + "(SELECT Class.id AS class_id, Module.id as module_id, "
            + "Module.name AS module_name, "
            + "User.first_name AS lecturer_first_name, "
            + "User.last_name AS lecturer_last_name, "
            + "StudentClass.user_id AS user_id, "
            + "ClassWideAnnouncement.id AS csId "
            + "FROM Class "
            + "INNER JOIN ClassWideAnnouncement  "
            + "ON ClassWideAnnouncement.class_id = Class.id "
            + "INNER JOIN Module ON Module.id = Class.module_id "
            + "INNER JOIN Lecturer ON Lecturer.user_id = Class.lecturer_user_id "
            + "INNER JOIN User ON User.id = Lecturer.user_id "
            + "INNER JOIN Semester ON Semester.id = Class.semester_id "
            + "INNER JOIN StudentClass ON StudentClass.class_id = Class.id "
            + "WHERE Class.semester_id = ? "
            + "AND "
            + "StudentClass.user_id = ?) AS CustomTable "
            + "ON CustomTable.csId = Announcement.id "
            + "WHERE (Announcement.announcement_type_id = 1 "
            + "OR CustomTable.user_id = ?) AND "
            + "DATE(Announcement.date_submitted)  >= (NOW() - INTERVAL 7 DAY) "
            + "ORDER BY date_submitted DESC;"),
    LECTURER_ANNOUNCEMENT_SELECT_QUERY("SELECT Announcement.id AS id, "
            + "Announcement.title AS title, "
            + "Announcement.message as message, "
            + "Announcement.date_submitted AS dateSubmitted, "
            + "Announcement.announcement_type_id AS announcementTypeId, "
            + "CustomTable.module_id AS moduleId, "
            + "CustomTable.module_name AS moduleName "
            + "FROM Announcement "
            + "LEFT JOIN ClassWideAnnouncement "
            + "ON Announcement.id = ClassWideAnnouncement.id "
            + "LEFT JOIN "
            + "(SELECT Class.id AS class_id, Module.id as module_id, "
            + "Module.name AS module_name, Class.lecturer_user_id AS user_id "
            + "FROM Module "
            + "INNER JOIN Class ON Class.module_id = Module.id "
            + "INNER JOIN Semester ON Semester.id = Class.semester_id "
            + "WHERE Class.semester_id = ? "
            + "AND Class.lecturer_user_id = ?) AS CustomTable "
            + "ON CustomTable.class_id = ClassWideAnnouncement.class_id "
            + "WHERE (Announcement.announcement_type_id = 1 OR "
            + "CustomTable.user_id = ?) AND "
            + "DATE(Announcement.date_submitted)  >= (NOW() - INTERVAL 7 DAY) "
            + "ORDER BY date_submitted DESC;"),
    LECTURER_CLASS_SELECT_QUERY("SELECT Class.id AS id, "
            + "Class.module_id AS moduleId, "
            + "Module.name AS moduldeName "
            + "FROM Class "
            + "INNER JOIN Module ON Module.id = Class.module_id "
            + "INNER JOIN Semester ON Semester.id = Class.semester_id "
            + "WHERE Class.lecturer_user_id = ? AND "
            + "Class.semester_id = ?;"),
    STUDENT_CLASS_SELECT_QUERY("SELECT Class.id AS id, " 
            + "Class.module_id AS moduleId, " 
            + "Module.name AS moduldeName " 
            + "FROM Class " 
            + "INNER JOIN Module ON Module.id = Class.module_id " 
            + "INNER JOIN Semester ON Semester.id = Class.semester_id " 
            + "INNER JOIN StudentClass ON StudentClass.class_id = Class.id " 
            + "WHERE StudentClass.user_id = ? AND " 
            + "Class.semester_id = ?;"),
    LECTURER_INSERT_NEW_ANNOUNCEMENT("INSERT INTO Announcement "
            + "(title,message,date_submitted,announcement_type_id) "
            + "VALUES (?,?,now(),2);"),
    LECTURER_INSERT_NEW_CLASSWIDE_ANNOUNCEMENT("INSERT INTO "
            + "ClassWideAnnouncement VALUES (?,?);"),
    SELECT_LASTROW_ANNOUNCEMENT("SELECT id FROM Announcement ORDER BY id DESC LIMIT 1;"),
    STUDENT_INSERT_NEW_COURSEWORK("INSERT INTO Coursework "
            + "(title,date_submitted,file,file_extension,student_user_id,"
            + "class_id) VALUES (?,now(),?,?,?,?);"),
    STUDENT_SELECT_COURSEWORK("SELECT Coursework.id AS coursework_id, "
            + "Coursework.title AS title, "
            + "Coursework.date_submitted AS date_submitted, "
            + "Coursework.file AS file, "
            + "Coursework.file_extension AS file_extension, "
            + "Coursework.class_id AS class_id, "
            + "Class.module_id AS module_id, "
            + "Module.name AS module_name, "
            + "User.id AS lecturer_id, "
            + "Lecturer.email AS lecturer_email, "
            + "User.first_name AS lecturer_firstname, "
            + "User.last_name AS lecturer_lastname "
            + "FROM Coursework "
            + "INNER JOIN Class ON Class.id = Coursework.class_id "
            + "INNER JOIN Module ON Module.id = Class.module_id "
            + "INNER JOIN Lecturer ON Lecturer.user_id = Class.lecturer_user_id "
            + "INNER JOIN User ON User.id = Lecturer.user_id "
            + "WHERE Class.semester_id = ? AND "
            + "Coursework.student_user_id = ?;"),
    LECTURER_SELECT_COURSEWORK("SELECT Coursework.id AS id, "
            + "Coursework.title AS title, "
            + "Coursework.date_submitted AS dateSubmitted, "
            + "Coursework.file AS file, "
            + "Coursework.file_extension As fileExtension, "
            + "Coursework.student_user_id AS studentUserID, "
            + "Student.student_id AS studentID, "
            + "User.first_name AS studentFirstName, "
            + "User.last_name AS studentLastName "
            + "FROM Coursework "
            + "INNER JOIN Student ON Student.user_id = Coursework.student_user_id "
            + "INNER JOIN User ON User.id = Coursework.student_user_id "
            + "WHERE Coursework.class_id = ?;");

    private final String query;

    private PreparedQueryStatement(String script) {
        this.query = script;
    }

    public String getQuery() {
        return query;
    }

}
