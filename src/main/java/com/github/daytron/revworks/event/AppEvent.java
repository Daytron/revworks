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

import com.github.daytron.revworks.model.ClassTable;
import com.github.daytron.revworks.model.Coursework;
import com.github.daytron.revworks.ui.AdminLoginPopup;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextField;
import java.io.File;

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

        public LecturerSubmitNewAnnouncementEvent(ClassTable selectedClass,
                TextField title, RichTextArea richTextArea) {
            this.selectedClass = selectedClass;
            this.title = title;
            this.richTextArea = richTextArea;
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

}
