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
package com.github.daytron.revworks.event;

import com.github.daytron.revworks.data.UserNotificationType;
import com.github.daytron.revworks.model.ClassTable;
import com.github.daytron.revworks.model.Coursework;
import com.github.daytron.revworks.model.UserNotification;
import com.github.daytron.revworks.view.AdminLoginPopup;
import com.github.daytron.revworks.view.main.CourseworkView;
import com.vaadin.ui.Button;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import java.io.File;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Collection events for Guava's EventBus as inner classes
 *
 * @author Ryan Gilera
 */
@SuppressWarnings("serial")
public abstract class AppEvent {

    public static final class UserLoginRequestEvent {

        private final OptionGroup userTypeOptionGroup;
        private final TextField userField;
        private final PasswordField passwordField;

        public UserLoginRequestEvent(TextField userField,
                PasswordField passwordField,
                OptionGroup userType) {
            this.userTypeOptionGroup = userType;
            this.userField = userField;
            this.passwordField = passwordField;
        }

        public OptionGroup getUserTypeOptionGroup() {
            return userTypeOptionGroup;
        }

        public TextField getUserField() {
            return userField;
        }

        public PasswordField getPasswordField() {
            return passwordField;
        }

    }

    public static final class AdminLoginRequestEvent {

        private final TextField userField;
        private final PasswordField passwordField;
        private final AdminLoginPopup adminLoginPopup;

        public AdminLoginRequestEvent(TextField userField,
                PasswordField passwordField,
                AdminLoginPopup adminLoginPopup) {
            this.userField = userField;
            this.passwordField = passwordField;
            this.adminLoginPopup = adminLoginPopup;
        }

        public PasswordField getPasswordField() {
            return passwordField;
        }

        public TextField getUserField() {
            return userField;
        }

        public AdminLoginPopup getAdminLoginPopup() {
            return adminLoginPopup;
        }

    }

    public static final class UserLogoutRequestEvent {
    }

    public static final class WebmasterLinkClickEvent {
    }

    public static final class OptionChangeValueEvent {

        private final TextField usernameField;
        private final String value;

        public OptionChangeValueEvent(TextField userField, String value) {
            this.usernameField = userField;
            this.value = value;
        }

        public TextField getUsernameField() {
            return usernameField;
        }

        public String getValue() {
            return value;
        }

    }

    public static final class LecturerSubmitNewAnnouncementEvent {

        private final ClassTable selectedClass;
        private final TextField title;
        private final RichTextArea richTextArea;
        private final Button submitButton;

        public LecturerSubmitNewAnnouncementEvent(ClassTable selectedClass,
                TextField title, RichTextArea richTextArea, Button submitButton) {
            this.selectedClass = selectedClass;
            this.title = title;
            this.richTextArea = richTextArea;
            this.submitButton = submitButton;
        }

        public ClassTable getSelectedClass() {
            return selectedClass;
        }

        public TextField getTitle() {
            return title;
        }

        public RichTextArea getRichTextArea() {
            return richTextArea;
        }

        public Button getSubmitButton() {
            return submitButton;
        }

    }

    public static final class StudentSubmitCourseworkEvent {

        private final File courseworkFile;
        private final ClassTable classTable;
        private final String title;

        public StudentSubmitCourseworkEvent(File courseworkFile,
                ClassTable classTable, String title) {
            this.courseworkFile = courseworkFile;
            this.classTable = classTable;
            this.title = title;
        }

        public File getCourseworkFile() {
            return courseworkFile;
        }

        public ClassTable getClassTable() {
            return classTable;
        }

        public String getTitle() {
            return title;
        }

    }

    public static final class LecturerViewCourseworkEvent {

        private final Coursework coursework;

        public LecturerViewCourseworkEvent(Coursework coursework) {
            this.coursework = coursework;
        }

        public Coursework getCoursework() {
            return coursework;
        }
    }

    public static final class StudentViewCourseworkEvent {

        private final Coursework coursework;

        public StudentViewCourseworkEvent(Coursework coursework) {
            this.coursework = coursework;
        }

        public Coursework getCoursework() {
            return coursework;
        }
    }

    public static final class ToggleCourseworkViewEvent {

        private final VerticalLayout contentLayout;
        private final boolean isToggled;

        public ToggleCourseworkViewEvent(VerticalLayout contentLayout, boolean isToggled) {
            this.contentLayout = contentLayout;
            this.isToggled = isToggled;
        }

        public boolean isToggled() {
            return isToggled;
        }

        public VerticalLayout getContentLayout() {
            return contentLayout;
        }

    }

    public static final class SubmitNewNoteEvent {

        private final Coursework coursework;
        private final int pageNumber;
        private final String message;
        private final CourseworkView courseworkView;

        public SubmitNewNoteEvent(Coursework coursework, int pageNumber,
                String message, CourseworkView courseworkView) {
            this.coursework = coursework;
            this.pageNumber = pageNumber;
            this.message = message;
            this.courseworkView = courseworkView;
        }

        public Coursework getCoursework() {
            return coursework;
        }

        public int getPageNumber() {
            return pageNumber;
        }

        public String getMessage() {
            return message;
        }

        public CourseworkView getCourseworkView() {
            return courseworkView;
        }
        
        
    }

    public static final class SubmitNewCommentEvent {

        private final int noteId;
        private final String message;
        private final Coursework coursework;

        public SubmitNewCommentEvent(Coursework coursework, 
                int noteID, String message) {
            this.noteId = noteID;
            this.message = message;
            this.coursework = coursework;
        }

        public Coursework getCoursework() {
            return coursework;
        }

        public int getNoteId() {
            return noteId;
        }

        public String getMessage() {
            return message;
        }
        
    }
    
    public static final class UpdateNoteIsReadWhenClickEvent {
        private final int noteId;
        private final Button button;

        public UpdateNoteIsReadWhenClickEvent(int noteId, Button button) {
            this.noteId = noteId;
            this.button = button;
        }

        public int getNoteId() {
            return noteId;
        }

        public Button getButton() {
            return button;
        }
        
    }
    
    public static final class UpdateNotificationButtonBadgeCountEvent {
        private final int count;

        public UpdateNotificationButtonBadgeCountEvent(int count) {
            this.count = count;
        }

        public int getCount() {
            return count;
        }
        
    }
    
    public static final class UpdateNotificationToReadEvent {
        private final CopyOnWriteArrayList<UserNotification> listOfUserNotifications;

        public UpdateNotificationToReadEvent(CopyOnWriteArrayList<UserNotification> 
                listOfUserNotifications) {
            this.listOfUserNotifications = listOfUserNotifications;
        }

        public CopyOnWriteArrayList<UserNotification> getListOfUserNotifications() {
            return listOfUserNotifications;
        }
        
    }
    
    public static final class InsertNotificationNewNoteEvent {
        
        private final String title;
        private final String message;
        private final UserNotificationType userNotificationType;
        private final int toUserId;
        private final int courseworkId;

        public InsertNotificationNewNoteEvent(String title, String message,
                UserNotificationType userNotificationType, 
                int toUserId, int courseworkId) {
            this.title = title;
            this.message = message;
            this.userNotificationType = userNotificationType;
            this.toUserId = toUserId;
            this.courseworkId = courseworkId;
        }

        public String getTitle() {
            return title;
        }

        public String getMessage() {
            return message;
        }

        public UserNotificationType getUserNotificationType() {
            return userNotificationType;
        }

        public int getToUserId() {
            return toUserId;
        }

        public int getCourseworkId() {
            return courseworkId;
        }
        
        
    }
    
    public static final class CloseNotificationWindowEvent {
    }
    
    public static final class AdminSubmitNewAnnouncementEvent {

        private final TextField title;
        private final RichTextArea richTextArea;
        private final Button submitButton;

        public AdminSubmitNewAnnouncementEvent(
                TextField title, RichTextArea richTextArea, Button submitButton) {
            this.title = title;
            this.richTextArea = richTextArea;
            this.submitButton = submitButton;
        }
        
        public TextField getTitle() {
            return title;
        }

        public RichTextArea getRichTextArea() {
            return richTextArea;
        }

        public Button getSubmitButton() {
            return submitButton;
        }

    }
}
