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
    STUDENT_SELECT_ANNOUNCEMENTS("SELECT Announcement.id AS id, "
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
            + "WHERE Announcement.announcement_type_id = 1 OR "
            + "StudentClass.user_id = ? "
            + "ORDER BY date_submitted DESC "
            + "LIMIT 15;"),
    LECTURER_SELECT_ANNOUNCEMENTS("SELECT Announcement.id AS id, "
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
            + "WHERE Announcement.announcement_type_id = 1 OR "
            + "Class.lecturer_user_id = ? "
            + "ORDER BY date_submitted DESC "
            + "LIMIT 15;"),
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
    STUDENT_INSERT_NEW_COURSEWORK("INSERT INTO Coursework "
            + "(title,date_submitted,file,file_extension,"
            + "is_read_student,is_read_lecturer,student_user_id,"
            + "class_id) VALUES (?,now(),?,?,?,?,?,?);"),
    STUDENT_SELECT_COURSEWORK("SELECT Coursework.id AS coursework_id, "
            + "Coursework.title AS title, "
            + "Coursework.date_submitted AS date_submitted, "
            + "Coursework.file AS file, "
            + "Coursework.file_extension AS file_extension, "
            + "Coursework.is_read_student AS is_read_s, "
            + "Coursework.is_read_lecturer AS is_read_l "
            + "FROM Coursework "
            + "INNER JOIN Class ON Class.id = Coursework.class_id "
            + "INNER JOIN Module ON Module.id = Class.module_id "
            + "INNER JOIN Lecturer ON Lecturer.user_id = Class.lecturer_user_id "
            + "INNER JOIN User ON User.id = Lecturer.user_id "
            + "WHERE Coursework.class_id = ? AND "
            + "Coursework.student_user_id = ?;"),
    STUDENT_UPDATE_COURSEWORK_IS_READ("UPDATE Coursework "
            + "SET is_read_student = ? "
            + "WHERE id = ?;"),
    LECTURER_SELECT_COURSEWORK("SELECT Coursework.id AS id, "
            + "Coursework.title AS title, "
            + "Coursework.date_submitted AS dateSubmitted, "
            + "Coursework.file AS file, "
            + "Coursework.file_extension As fileExtension, "
            + "Coursework.student_user_id AS studentUserID, "
            + "Coursework.is_read_student AS is_read_s, "
            + "Coursework.is_read_lecturer AS is_read_l, "
            + "Student.student_id AS studentID, "
            + "User.first_name AS studentFirstName, "
            + "User.last_name AS studentLastName "
            + "FROM Coursework "
            + "INNER JOIN Student ON Student.user_id = Coursework.student_user_id "
            + "INNER JOIN User ON User.id = Coursework.student_user_id "
            + "WHERE Coursework.class_id = ?;"),
    LECTURER_UPDATE_COURSEWORK_IS_READ("UPDATE Coursework "
            + "SET is_read_lecturer = ? "
            + "WHERE id = ?;"),
    SELECT_NOTE("SELECT n.id, "
            + "n.page_num, "
            + "n.date_submitted, "
            + "n.is_student_to_lecturer, "
            + "n.is_read_student, "
            + "n.is_read_lecturer "
            + "FROM Note n "
            + "WHERE n.coursework_id = ? "
            + "ORDER BY n.date_submitted ASC;"),
    SELECT_COMMENT("SELECT "
            + "Comment.message AS message, "
            + "Comment.date_submitted AS dateSubmitted, "
            + "Comment.is_student_to_lecturer AS isStudentToLecturer  "
            + "FROM Comment  "
            + "WHERE Comment.note_id = ? "
            + "ORDER BY Comment.date_submitted ASC; "),
    INSERT_NOTE("INSERT INTO Note(page_num,"
            + "date_submitted,"
            + "is_student_to_lecturer,"
            + "is_read_student,"
            + "is_read_lecturer,"
            + "coursework_id) "
            + "VALUES (?,now(),?,?,?,?);"),
    UPDATE_NOTE("UPDATE Note "
            + "SET is_read_student = ?, "
            + "is_read_lecturer = ? "
            + "WHERE id = ?;"),
    STUDENT_UPDATE_NOTE("UPDATE Note "
            + "SET is_read_student = ? "
            + "WHERE id = ?;"),
    LECTURER_UPDATE_NOTE("UPDATE Note "
            + "SET is_read_lecturer = ? "
            + "WHERE id = ?;"),
    INSERT_COMMENT("INSERT INTO Comment(message,date_submitted,"
            + "is_student_to_lecturer,note_id) "
            + "VALUES (?,now(),?,?);"),
    SELECT_USER_NOTIFICATION_UNREAD("SELECT UserNotification.id, "
            + "UserNotification.title, "
            + "UserNotification.message, "
            + "UserNotification.date_submitted, "
            + "UserNotification.is_read, "
            + "UserNotification.notification_type_id, "
            + "UserNotification.from_user_id, "
            + "User.first_name, "
            + "Coursework.id, "
            + "Coursework.title "
            + "FROM UserNotification "
            + "LEFT JOIN User "
            + "ON User.id = UserNotification.from_user_id "
            + "LEFT JOIN NotificationsCoursework "
            + "ON NotificationsCoursework.user_notification_id = UserNotification.id "
            + "LEFT JOIN Coursework "
            + "ON Coursework.id = NotificationsCoursework.coursework_id "
            + "WHERE UserNotification.to_user_id = ? AND "
            + "UserNotification.is_read = 0;"),
    SELECT_USER_NOTIFICATION_READ("SELECT UserNotification.id, "
            + "UserNotification.title, "
            + "UserNotification.message, "
            + "UserNotification.date_submitted, "
            + "UserNotification.is_read, "
            + "UserNotification.notification_type_id, "
            + "UserNotification.from_user_id, "
            + "User.first_name, "
            + "Coursework.id, "
            + "Coursework.title "
            + "FROM UserNotification "
            + "LEFT JOIN User "
            + "ON User.id = UserNotification.from_user_id "
            + "LEFT JOIN NotificationsCoursework "
            + "ON NotificationsCoursework.user_notification_id = UserNotification.id "
            + "LEFT JOIN Coursework "
            + "ON Coursework.id = NotificationsCoursework.coursework_id "
            + "WHERE UserNotification.to_user_id = ? AND "
            + "UserNotification.is_read = 1 "
            + "LIMIT 5;"),
    UPDATE_NOTIFICATION_TO_READ("UPDATE UserNotification "
            + "SET is_read = 1 "
            + "WHERE id = ?;"),
    INSERT_NOTIFICATION("INSERT INTO UserNotification("
            + "title,message,date_submitted,"
            + "is_read,notification_type_id,"
            + "to_user_id,from_user_id) "
            + "VALUES (?,?,now(),?,?,?,?);"),
    INSERT_NOTIFICATIONS_COURSEWORK("INSERT INTO NotificationsCoursework("
            + "user_notification_id,coursework_id) "
            + "VALUES (?,?);"),
    ADMIN_INSERT_ANNOUNCEMENT("INSERT INTO Announcement "
            + "(title,message,date_submitted,announcement_type_id) "
            + "VALUES (?,?,now(),1);");

    private final String query;

    private PreparedQueryStatement(String script) {
        this.query = script;
    }

    /**
     * Access the premade SQL query.
     * 
     * @return the query String value of an item 
     */
    public String getQuery() {
        return query;
    }

}
