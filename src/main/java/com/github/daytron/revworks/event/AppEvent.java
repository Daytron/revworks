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
 * An abstract class that holds custom events for Guava's EventBus as inner 
 * classes.
 *
 * @author Ryan Gilera
 */
@SuppressWarnings("serial")
public abstract class AppEvent {

    /**
     * Custom event class for student and lecturer login request.
     */
    public static final class UserLoginRequestEvent {

        private final OptionGroup userTypeOptionGroup;
        private final TextField userField;
        private final PasswordField passwordField;

        /**
         * Class constructor that takes a TextField object for username, 
         * a PasswordField object for password, and OptionGroup object for 
         * user type.
         * 
         * @param userField TextField object for username
         * @param passwordField PasswordField object for password
         * @param userType OptionGroup object for user type
         */
        public UserLoginRequestEvent(TextField userField,
                PasswordField passwordField,
                OptionGroup userType) {
            this.userTypeOptionGroup = userType;
            this.userField = userField;
            this.passwordField = passwordField;
        }

        /**
         * Access the OptionGroup member from the login view.
         * 
         * @return OptionGroup object
         */
        public OptionGroup getUserTypeOptionGroup() {
            return userTypeOptionGroup;
        }

        /**
         * Access the TextField member for username from the login view.
         * 
         * @return TextField object
         */
        public TextField getUserField() {
            return userField;
        }

        /**
         * Access the PasswordField member for password from the login view.
         * 
         * @return PasswordField object
         */
        public PasswordField getPasswordField() {
            return passwordField;
        }

    }

    /**
     * Custom event class for administrator login request.
     */
    public static final class AdminLoginRequestEvent {

        private final TextField userField;
        private final PasswordField passwordField;
        private final AdminLoginPopup adminLoginPopup;

        /**
         * Class constructor that takes a TextField object for an Admin 
         * username, an Admin PasswordField object and the AdminLoginPopup 
         * object.
         * 
         * @param userField component for the admin username
         * @param passwordField component for admin password
         * @param adminLoginPopup the popup window for admin login view
         */
        public AdminLoginRequestEvent(TextField userField,
                PasswordField passwordField,
                AdminLoginPopup adminLoginPopup) {
            this.userField = userField;
            this.passwordField = passwordField;
            this.adminLoginPopup = adminLoginPopup;
        }

        /**
         * Access the PasswordField object from the Admin popup window.
         * 
         * @return PasswordField object
         */
        public PasswordField getPasswordField() {
            return passwordField;
        }

        /**
         * Access the TextField object for username from the Admin popup 
         * window.
         * 
         * @return TextField object
         */
        public TextField getUserField() {
            return userField;
        }

        /**
         * Access the Admin popup window. This is used for closing the 
         * window itself after a successful login authentication.
         * 
         * @return AdminLoginPopup object
         */
        public AdminLoginPopup getAdminLoginPopup() {
            return adminLoginPopup;
        }

    }

    /**
     * Custom event class for logout request.
     */
    public static final class UserLogoutRequestEvent {
    }

    /**
     * Custom event class for launching Admin login popup window.
     */
    public static final class WebmasterLinkClickEvent {
    }

    /**
     * Custom event class for changing the option value in the OptionGroup 
     * object from the login view.
     */
    public static final class OptionChangeValueEvent {

        private final TextField usernameField;
        private final String value;

        /**
         * A class constructor that takes a TextField object and a String value.
         * 
         * @param userField TextField object for username
         * @param value a String value for the option selected
         */
        public OptionChangeValueEvent(TextField userField, String value) {
            this.usernameField = userField;
            this.value = value;
        }

        /**
         * Access the TextField object for username.
         * 
         * @return TextField object
         */
        public TextField getUsernameField() {
            return usernameField;
        }

        /**
         * Access the selected option from the OptionGroup
         * 
         * @return selected option as String
         */
        public String getValue() {
            return value;
        }

    }

    /**
     * Custom event class for submitting new announcement from the lecturer.
     */
    public static final class LecturerSubmitNewAnnouncementEvent {

        private final ClassTable selectedClass;
        private final TextField title;
        private final RichTextArea richTextArea;
        private final Button submitButton;

        /**
         * A class constructor that takes a ClassTable object, a TextField 
         * object, a RichTextArea object and a Button object.
         * 
         * @param selectedClass the school class the announcement is for.
         * @param title TextField component for the announcement title
         * @param richTextArea RichTextArea for the announcement message
         * @param submitButton the button used for triggering the submission.
         */
        public LecturerSubmitNewAnnouncementEvent(ClassTable selectedClass,
                TextField title, RichTextArea richTextArea, Button submitButton) {
            this.selectedClass = selectedClass;
            this.title = title;
            this.richTextArea = richTextArea;
            this.submitButton = submitButton;
        }

        /**
         * Access the ClassTable object.
         * 
         * @return ClassTable object
         */
        public ClassTable getSelectedClass() {
            return selectedClass;
        }

        /**
         * Access the TextField object.
         * 
         * @return TextField object
         */
        public TextField getTitle() {
            return title;
        }

        /**
         * Access the RichTextArea object.
         * 
         * @return RichTextArea object
         */
        public RichTextArea getRichTextArea() {
            return richTextArea;
        }

        /**
         * Access the submission Button object.
         * 
         * @return Button object
         */
        public Button getSubmitButton() {
            return submitButton;
        }

    }

    /**
     * Custom event class for submitting new Coursework from the students.
     */
    public static final class StudentSubmitCourseworkEvent {

        private final File courseworkFile;
        private final ClassTable classTable;
        private final String title;

        /**
         * A class constructor that takes a File object, a Classtable object, 
         * and a String object.
         * 
         * @param courseworkFile the coursework file submitted.
         * @param classTable the school class associated with the coursework
         * @param title the title of the coursework
         */
        public StudentSubmitCourseworkEvent(File courseworkFile,
                ClassTable classTable, String title) {
            this.courseworkFile = courseworkFile;
            this.classTable = classTable;
            this.title = title;
        }

        /**
         * Access the coursework File object.
         * 
         * @return File object
         */
        public File getCourseworkFile() {
            return courseworkFile;
        }

        /**
         * Access the associated class with the coursework
         * 
         * @return ClassTable object
         */
        public ClassTable getClassTable() {
            return classTable;
        }

        /**
         * Access the title of the coursework submitted.
         * 
         * @return String object
         */
        public String getTitle() {
            return title;
        }

    }

    /**
     * Custom event class for launching the coursework view for the lecturers.
     */
    public static final class LecturerViewCourseworkEvent {

        private final Coursework coursework;

        /**
         * Class constructor that takes a Coursework object.
         * 
         * @param coursework the coursework that the lecturer is about to view
         */
        public LecturerViewCourseworkEvent(Coursework coursework) {
            this.coursework = coursework;
        }

        /**
         * Access the Coursework object.
         * 
         * @return Coursework object
         */
        public Coursework getCoursework() {
            return coursework;
        }
    }

    /**
     * Custom event class for launching the coursework view for the students.
     */
    public static final class StudentViewCourseworkEvent {

        private final Coursework coursework;

        /**
         * Class constructor that takes a Coursework object.
         * 
         * @param coursework the coursework that the student is about to view
         */
        public StudentViewCourseworkEvent(Coursework coursework) {
            this.coursework = coursework;
        }

        /**
         * Access the Coursework object.
         * 
         * @return Coursework object
         */
        public Coursework getCoursework() {
            return coursework;
        }
    }

    /**
     * Custom event class for toggling the maximise/minimise button in the 
     * coursework view for maximising or minimising the view.
     */
    public static final class ToggleCourseworkViewEvent {

        private final VerticalLayout contentLayout;
        private final boolean isToggled;

        /**
         * A class constructor that takes VerticalLayout object.
         * 
         * @param contentLayout The view for maximising and minimising display
         * @param isToggled toggled value from the user.
         */
        public ToggleCourseworkViewEvent(VerticalLayout contentLayout, 
                boolean isToggled) {
            this.contentLayout = contentLayout;
            this.isToggled = isToggled;
        }

        /**
         * Access the toggled value.
         * 
         * @return true for maximising the view or false for minimising the view
         */
        public boolean isToggled() {
            return isToggled;
        }

        /**
         * Access the view that is about to be maximised or minimised depending 
         * on the isToggled value.
         * 
         * @return VerticalLayout object
         */
        public VerticalLayout getContentLayout() {
            return contentLayout;
        }

    }

    /**
     * Custom event class for submitting new note from the Notes panel in the 
     * coursework view.
     */
    public static final class SubmitNewNoteEvent {

        private final Coursework coursework;
        private final int pageNumber;
        private final String message;
        private final CourseworkView courseworkView;

        /**
         * The class constructor that takes a Coursework object, an integer, 
         * a String object, and CourseworkView object.
         * 
         * @param coursework the coursework associated with the note
         * @param pageNumber the page of the coursework associated with the note
         * @param message the message in the first comment of the note.
         * @param courseworkView a reference to the CourseworkView
         */
        public SubmitNewNoteEvent(Coursework coursework, int pageNumber,
                String message, CourseworkView courseworkView) {
            this.coursework = coursework;
            this.pageNumber = pageNumber;
            this.message = message;
            this.courseworkView = courseworkView;
        }

        /**
         * Access the Coursework object.
         * 
         * @return Coursework object
         */
        public Coursework getCoursework() {
            return coursework;
        }

        /**
         * Access the page number
         * 
         * @return an integer as the page number
         */
        public int getPageNumber() {
            return pageNumber;
        }

        /**
         * Access the message in the first comment when creating a note.
         * 
         * @return String value of the message
         */
        public String getMessage() {
            return message;
        }

        /**
         * Access the CourseworkView object which displays the page
         * 
         * @return CourseworkView object
         */
        public CourseworkView getCourseworkView() {
            return courseworkView;
        }
        
    }

    /**
     * Custom event class for submitting new comment from the Comments panel 
     * in the coursework view.
     */
    public static final class SubmitNewCommentEvent {

        private final int noteId;
        private final String message;
        private final Coursework coursework;

        /**
         * The class constructor that takes a Coursework object, an integer and 
         * a String object.
         * 
         * @param coursework The coursework associated with the comment
         * @param noteID The note id associated with the comment
         * @param message The message itself
         */
        public SubmitNewCommentEvent(Coursework coursework, 
                int noteID, String message) {
            this.noteId = noteID;
            this.message = message;
            this.coursework = coursework;
        }

        /**
         * Access the Coursework object.
         * 
         * @return Coursework object
         */
        public Coursework getCoursework() {
            return coursework;
        }

        /**
         * Access the note id.
         * 
         * @return an integer value as note id.
         */
        public int getNoteId() {
            return noteId;
        }

        /**
         * Access the message of the comment.
         * 
         * @return String value of the message
         */
        public String getMessage() {
            return message;
        }
        
    }
    
    /**
     * Custom event class for updating note's status when its button is clicked 
     * in the Notes panel in the coursework view. 
     */
    public static final class UpdateNoteIsReadWhenClickEvent {
        
        private final int noteId;
        private final Button button;

        /**
         * The class constructor that takes an integer and a Button.
         * 
         * @param noteId The note id of the button clicked.
         * @param button The button that is clicked.
         */
        public UpdateNoteIsReadWhenClickEvent(int noteId, Button button) {
            this.noteId = noteId;
            this.button = button;
        }

        /**
         * Access the note id.
         * 
         * @return an integer value as the note id
         */
        public int getNoteId() {
            return noteId;
        }

        /**
         * Access the button that was clicked.
         * 
         * @return Button object
         */
        public Button getButton() {
            return button;
        }
        
    }
    
    /**
     * Custom event class for updating the total unread user notifications badge 
     * count.
     */
    public static final class UpdateNotificationButtonBadgeCountEvent {
        
        private final int count;

        /**
         * The class constructor that takes an integer.
         * 
         * @param count total unread user notifications
         */
        public UpdateNotificationButtonBadgeCountEvent(int count) {
            this.count = count;
        }

        /**
         * Access the total count of unread user notifications.
         * 
         * @return an integer as the total count
         */
        public int getCount() {
            return count;
        }
        
    }
    
    /**
     * Custom event class for updating opened notification's status to read. 
     * When user clicks the notification button to open the notification window, 
     * all opened notifications' statuses are updated. 
     */
    public static final class UpdateNotificationToReadEvent {
        
        private final CopyOnWriteArrayList<UserNotification> listOfUserNotifications;

        /**
         * The class constructor that takes a CopyOnWriteArrayList object.
         * 
         * @param listOfUserNotifications the list of UserNotification objects
         */
        public UpdateNotificationToReadEvent(CopyOnWriteArrayList<UserNotification> 
                listOfUserNotifications) {
            this.listOfUserNotifications = listOfUserNotifications;
        }

        /**
         * Access the list of UserNotification objects.
         * 
         * @return CopyOnWriteArrayList object
         */
        public CopyOnWriteArrayList<UserNotification> getListOfUserNotifications() {
            return listOfUserNotifications;
        }
        
    }
    
    /**
     * Custom event class for creating new user notification. Triggers insertion 
     * of the said note to the database.
     */
    public static final class InsertNotificationEvent {
        
        private final String title;
        private final String message;
        private final UserNotificationType userNotificationType;
        private final int toUserId;
        private final int courseworkId;

        /**
         * The class constructor that takes two String objects, a 
         * UserNotificationType object and two integers.
         * 
         * @param title The title for the user notification
         * @param message The notification message
         * @param userNotificationType The type of notification
         * @param toUserId The userId of the receiving user
         * @param courseworkId The coursework id associated to the new note
         */
        public InsertNotificationEvent(String title, String message,
                UserNotificationType userNotificationType, 
                int toUserId, int courseworkId) {
            this.title = title;
            this.message = message;
            this.userNotificationType = userNotificationType;
            this.toUserId = toUserId;
            this.courseworkId = courseworkId;
        }

        /**
         * Access the title of the user notification.
         * 
         * @return String value of title
         */
        public String getTitle() {
            return title;
        }

        /**
         * Access the message of the user notification
         * 
         * @return String value of the message
         */
        public String getMessage() {
            return message;
        }

        /**
         * Access the notification type of the user notification
         * 
         * @return UserNotificationType enum item
         */
        public UserNotificationType getUserNotificationType() {
            return userNotificationType;
        }

        /**
         * Access the user id of the receiving user.
         * 
         * @return an integer value of the id
         */
        public int getToUserId() {
            return toUserId;
        }

        /**
         * Access the coursework id associated with the new note.
         * 
         * @return and integer value of the id
         */
        public int getCourseworkId() {
            return courseworkId;
        }
        
    }
    
    /**
     * Custom event class for closing the popup notification window.
     */
    public static final class CloseNotificationWindowEvent {
    }
    
    /**
     * Custom event class for submitting new announcement from the Admin.
     */
    public static final class AdminSubmitNewAnnouncementEvent {

        private final TextField title;
        private final RichTextArea richTextArea;
        private final Button submitButton;

        /**
         * The class constructor takes a TextField object, a RichTextArea and 
         * a Button object.
         * 
         * @param title title of the new announcement from the admin
         * @param richTextArea the component that holds the announcement message
         * @param submitButton the button for submission.
         */
        public AdminSubmitNewAnnouncementEvent(
                TextField title, RichTextArea richTextArea, Button submitButton) {
            this.title = title;
            this.richTextArea = richTextArea;
            this.submitButton = submitButton;
        }
        
        /**
         * Access the component for the title of the announcement.
         * 
         * @return TextField object
         */
        public TextField getTitle() {
            return title;
        }

        /**
         * Access the component for the message of the announcement.
         * 
         * @return RichTextArea object
         */
        public RichTextArea getRichTextArea() {
            return richTextArea;
        }

        /**
         * Access the button used for submitting the announcement. This reference
         * is required for disabling the button when clicked once.
         * 
         * @return Button object
         */
        public Button getSubmitButton() {
            return submitButton;
        }

    }
    
}
