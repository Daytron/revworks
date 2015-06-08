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
    STUDENT_SELECT_ANNOUNCEMENT("SELECT Announcement.id AS id, "
            + "Announcement.title AS title, "
            + "Announcement.message AS message,  "
            + "Announcement.date_submitted AS dateSubmitted, "
            + "Announcement.announcement_type_id AS announcementTypeId, "
            + "ClassWideAnnouncement.class_id AS classId "
            + "FROM Announcement "
            + "LEFT JOIN ClassWideAnnouncement "
            + "ON ClassWideAnnouncement.id = Announcement.id "
            + "LEFT JOIN Class "
            + "ON Class.id = ClassWideAnnouncement.class_id "
            + "LEFT JOIN StudentClass "
            + "ON StudentClass.class_id = Class.id "
            + "WHERE (Announcement.announcement_type_id = 1 OR "
            + "StudentClass.user_id = ?) AND "
            + "DATE(Announcement.date_submitted)  >= (NOW() - INTERVAL 7 DAY) "
            + "ORDER BY date_submitted DESC;"),
    LECTURER_SELECT_ANNOUNCEMENT("SELECT Announcement.id AS id, "
            + "Announcement.title AS title, "
            + "Announcement.message as message, "
            + "Announcement.date_submitted AS dateSubmitted, "
            + "Announcement.announcement_type_id AS announcementTypeId, "
            + "ClassWideAnnouncement.class_id AS classId "
            + "FROM Announcement "
            + "LEFT JOIN ClassWideAnnouncement "
            + "ON Announcement.id = ClassWideAnnouncement.id  "
            + "LEFT JOIN Class "
            + "ON Class.id = ClassWideAnnouncement.class_id "
            + "WHERE (Announcement.announcement_type_id = 1 OR "
            + "Class.lecturer_user_id = ?) AND "
            + "DATE(Announcement.date_submitted)  >= (NOW() - INTERVAL 7 DAY) "
            + "ORDER BY date_submitted DESC;"),
    LECTURER_SELECT_CLASS("SELECT Class.id AS id, "
            + "Class.module_id AS moduleId, "
            + "Module.name AS moduldeName "
            + "FROM Class "
            + "INNER JOIN Module ON Module.id = Class.module_id "
            + "INNER JOIN Semester ON Semester.id = Class.semester_id "
            + "WHERE Class.lecturer_user_id = ? AND "
            + "Class.semester_id = ?;"),
    STUDENT_SELECT_CLASS("SELECT Class.id, Module.id, Module.name, "
            + "User.id, Lecturer.email, User.first_name, User.last_name "
            + "FROM Class "
            + "INNER JOIN StudentClass ON StudentClass.class_id = Class.id "
            + "INNER JOIN Student ON Student.user_id = StudentClass.user_id "
            + "INNER JOIN Module ON Module.id = Class.module_id "
            + "INNER JOIN Lecturer ON Lecturer.user_id = Class.lecturer_user_id "
            + "INNER JOIN User ON User.id = Lecturer.user_id "
            + "WHERE Student.user_id = ? AND "
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
            + "Coursework.file_extension AS file_extension "
            + "FROM Coursework "
            + "INNER JOIN Class ON Class.id = Coursework.class_id "
            + "INNER JOIN Module ON Module.id = Class.module_id "
            + "INNER JOIN Lecturer ON Lecturer.user_id = Class.lecturer_user_id "
            + "INNER JOIN User ON User.id = Lecturer.user_id "
            + "WHERE Coursework.class_id = ?;"),
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
