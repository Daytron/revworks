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

import com.github.daytron.revworks.exception.AuthenticationException;
import com.github.daytron.revworks.model.LecturerUser;
import com.github.daytron.revworks.model.StudentUser;
import com.github.daytron.revworks.model.User;
import com.github.daytron.revworks.MainUI;
import com.github.daytron.revworks.validator.LoginValidatorFactory;
import com.github.daytron.revworks.data.ErrorMsg;
import com.github.daytron.revworks.service.CurrentUserSession;
import com.github.daytron.revworks.exception.WrongCurrentUserTypeException;
import com.github.daytron.revworks.data.ExceptionMsg;
import com.github.daytron.revworks.data.FontAwesomeIcon;
import com.github.daytron.revworks.data.LoginString;
import com.github.daytron.revworks.data.LoginValidationNum;
import com.github.daytron.revworks.data.UserType;
import com.github.daytron.revworks.event.AppEvent;
import com.github.daytron.revworks.event.AppEventBus;
import com.github.daytron.revworks.service.LecturerDataInserterImpl;
import com.github.daytron.revworks.service.StudentDataInserterImpl;
import com.github.daytron.revworks.ui.AdminDashboardScreen;
import com.github.daytron.revworks.ui.AdminLoginPopup;
import com.github.daytron.revworks.util.NotificationUtil;
import com.google.common.eventbus.Subscribe;
import com.vaadin.data.Validator;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default implementation class of {@link AccessControl} interface. This
 * implementation accepts {@link  UserType} object and any strings as username
 * and a password, and manage user access control and information.
 *
 * @author Ryan Gilera
 */
@SuppressWarnings("serial")
public class UserAccessControl implements AccessControl {

    /**
     * Expose authentication method call of {@link UserAuthentication} to pass
     * {@link  UserType} object, username and password arguments. As a result,
     * it accepts and returns a {@link User} as a Principal object.
     *
     * @param event
     */
    @Subscribe
    @Override
    public void signIn(final AppEvent.UserLoginRequestEvent event) {

        try {
            event.getUserField().validate();
            event.getPasswordField().validate();

            authenticateUserLoginRequest(event);
        } catch (Validator.InvalidValueException e) {
            event.getUserField().setValidationVisible(true);
            event.getPasswordField().setValidationVisible(true);

            Logger.getLogger(UserAccessControl.class.getName())
                    .log(Level.SEVERE, null, e);
            NotificationUtil.showError(
                    ErrorMsg.INVALID_INPUT_CAPTION.getText());
        }

    }

    /**
     * Authenticates the user login credentials. When deem successful, a new
     * User object is created pulled from the user info from the database and
     * save the object to the current session.
     *
     * @param event The custom event for regular user sign-in event
     */
    private void authenticateUserLoginRequest(
            final AppEvent.UserLoginRequestEvent event) {
        String userTypeString = (String) event.getUserTypeOptionGroup().getValue();

        UserType userType = (userTypeString.equalsIgnoreCase(UserType.STUDENT.getText()))
                ? UserType.STUDENT : UserType.LECTURER;

        String userName = event.getUserField().getValue();
        String password = event.getPasswordField().getValue();

        Principal user;
        try {
            String semesterID = UserAuthentication.getInstance()
                    .verifyCurrentDateWithinASemester();
            
            // In an event the student or lecturer signin on date 
            // outside of semester dates will block access to the application
            if (semesterID.isEmpty()) {
                NotificationUtil.showError(
                        ErrorMsg.NO_CURRENT_SEMESTER.getText(), 
                        ErrorMsg.CONSULT_YOUR_ADMIN.getText());
                return;
            }
            
            user = UserAuthentication.getInstance()
                    .authenticate(userType, userName, password);
            CurrentUserSession.set(user, semesterID);

            // Verifies if this is the only login session for the current user
            // If this is second login session made by the user,
            // it terminates the old session and continue with this new session
            // created. Afterwards this session is recorded in the servlet.
            // See MainUIServlet documentation
            MainUI.MainUIServlet.saveUserSessionInfo(getPrincipalName(),
                    VaadinSession.getCurrent());
            MainUI.MainUIServlet.printSessions("user login");

            if (isUserALecturer()) {
                AppEventBus.register(new LecturerDataInserterImpl());
            } else {
                // Student otherwise
                AppEventBus.register(new StudentDataInserterImpl());
            }
            
            MainUI.get().showDashboardScreen();
            
            NotificationUtil.showInformation(
                    FontAwesomeIcon.THUMBS_O_UP.getLgSize(),
                    "Welcome " + getFirstName() + "!", "");
            
        } catch (AuthenticationException ex) {
            Logger.getLogger(UserAccessControl.class.getName()).log(Level.SEVERE, null, ex);
            NotificationUtil.showError(
                    ErrorMsg.SIGNIN_FAILED_CAPTION.getText(),
                    ErrorMsg.CONSULT_YOUR_ADMIN.getText());
        }

    }

    /**
     * Validates admin login form fields and call sign-in authentication method.
     *
     * @param event The custom event for admin sign-in event
     */
    @Subscribe
    @Override
    public void signInAdmin(final AppEvent.AdminLoginRequestEvent event) {
        try {
            event.getUserField().validate();
            event.getPasswordField().validate();

            authenticateAdminLoginRequest(event);
        } catch (Exception e) {
            event.getUserField().setValidationVisible(true);
            event.getPasswordField().setValidationVisible(true);

            Logger.getLogger(UserAccessControl.class.getName())
                    .log(Level.SEVERE, null, e);
            NotificationUtil.showError(
                    ErrorMsg.INVALID_INPUT_CAPTION.getText());
        }
    }

    /**
     * Authenticates the admin login credentials. When deem successful, a new
     * User object is created pulled from the user info from the database and
     * save the object to the current session.
     *
     * @param event The custom event for admin sign-in event
     */
    private void authenticateAdminLoginRequest(
            final AppEvent.AdminLoginRequestEvent event) {
        UserType userType = UserType.ADMIN;
        String userName = event.getUserField().getValue();
        String password = event.getPasswordField().getValue();

        Principal adminUser;

        try {
            String semesterID = UserAuthentication.getInstance().verifyCurrentDateWithinASemester();
            
            adminUser = UserAuthentication.getInstance()
                    .authenticate(userType, userName, password);
            
            // Note that for admin access, if sign-in on a date 
            // that there is no ongoing semester the semesterID 
            // variable is empty String.
            // This allows all year round access for administrators
            CurrentUserSession.set(adminUser, semesterID);

            // Verifies if this is the only login session for the current user
            // If this is second login session made by the user,
            // it terminates the old session and continue with this new session
            // created. Afterwards this session is recorded in the servlet.
            // See MainUIServlet documentation
            MainUI.MainUIServlet.saveUserSessionInfo(
                    getPrincipalName(),
                    VaadinSession.getCurrent());
            MainUI.MainUIServlet.printSessions("admin login");

            event.getAdminLoginPopup().close();

            AdminDashboardScreen adminDashboard = new AdminDashboardScreen();
            UI.getCurrent().setContent(adminDashboard);

            Notification.show("Welcome "
                    + getFirstName()
                    + "!",
                    Notification.Type.TRAY_NOTIFICATION);
        } catch (AuthenticationException ex) {
            Logger.getLogger(UserAccessControl.class.getName())
                    .log(Level.SEVERE, null, ex);
            NotificationUtil.showError(
                    ErrorMsg.SIGNIN_FAILED_CAPTION.getText(),
                    ErrorMsg.CONSULT_YOUR_ADMIN.getText());
        }

    }

    /**
     * Launches a new modal popup window for admin login when user click the
     * webmaster link located at the login screen footer section.
     *
     * @param event The custom event for webmaster link click event
     */
    @Subscribe
    @Override
    public void webmasterLinkOnClick(
            final AppEvent.WebmasterLinkClickEvent event) {
        AdminLoginPopup adminLoginPopup = new AdminLoginPopup();

        adminLoginPopup.setSizeUndefined();
        adminLoginPopup.center();

        UI.getCurrent().addWindow(adminLoginPopup);
    }

    /**
     * Applies the necessary setting for each user type. The textfield is shared
     * by student ID and lecturer's email address.
     *
     * @param event The custom event for OptionGroup's value change event
     */
    @Subscribe
    @Override
    public void optionGroupOnChangeValue(final AppEvent.OptionChangeValueEvent event) {
        // Retrieve component and new value
        final TextField usernameField = event.getUsernameField();
        String value = event.getValue();

        // Clear previous validators
        usernameField.removeAllValidators();

        // Clears the textfield if option group value is changed
        usernameField.clear();

        // Change username textfield caption according to user type
        if (value.equalsIgnoreCase(LoginString.FORM_OPTION_STUDENT.getText())) {
            usernameField.setCaption(LoginString.FORM_STUDENT_ID.getText());

            // Apply corresponding validator
            usernameField.addValidator(
                    LoginValidatorFactory.buildStudentIDValidator());
            // Apply max entry length for student ID
            usernameField.setMaxLength(
                    LoginValidationNum.STUDENT_ID_LENGTH.getValue());

        } else {
            usernameField.setCaption(LoginString.FORM_LECTURER_EMAIL.getText());

            usernameField.addValidator(
                    LoginValidatorFactory.buildEmailValidator());

            usernameField.setMaxLength(
                    LoginValidationNum.EMAIL_MAX_LENGTH.getValue());
        }

        // Return back the focus
        usernameField.focus();
    }

    @Override
    public boolean isUserSignedIn() {
        return CurrentUserSession.get() != null;
    }

    @Override
    public boolean isUserAStudent() {
        return ((User) CurrentUserSession.get()).isStudentUser();
    }

    @Override
    public boolean isUserALecturer() {
        return ((User) CurrentUserSession.get()).isLecturerUser();
    }

    @Override
    public boolean isUserAdmin() {
        return ((User) CurrentUserSession.get()).isAdminUser();
    }

    @Override
    public String getPrincipalName() {
        return CurrentUserSession.get().getName();
    }

    @Override
    public String getFirstName() {
        return ((User) CurrentUserSession.get()).getFirstName();
    }

    @Override
    public String getLastName() {
        return ((User) CurrentUserSession.get()).getLastName();
    }

    @Override
    public String getFullName() {
        return ((User) CurrentUserSession.get()).getFullName();
    }

    @Override
    public String getLecturerEmail() throws WrongCurrentUserTypeException {
        if (((User) CurrentUserSession.get()).isLecturerUser()) {
            return ((LecturerUser) (CurrentUserSession.get())).getEmail();
        } else {
            throw new WrongCurrentUserTypeException(
                    ExceptionMsg.WRONG_CURRENT_USER_TYPE_EXCEPTION.getMsg());
        }
    }

    @Override
    public String getStudentID() throws WrongCurrentUserTypeException {
        if (((User) CurrentUserSession.get()).isLecturerUser()) {
            return ((StudentUser) (CurrentUserSession.get())).getStudentID();
        } else {
            throw new WrongCurrentUserTypeException(
                    ExceptionMsg.WRONG_CURRENT_USER_TYPE_EXCEPTION.getMsg());
        }
    }

    @Subscribe
    @Override
    public void signOut(final AppEvent.UserLogoutRequestEvent event) {
        CurrentUserSession.signOut();

        // Reloads the page which will point back to login page
        Page.getCurrent().reload();
    }

    @Override
    public String getUserTypeString() {
        return ((User) CurrentUserSession.get()).getUserType().getText();
    }

    @Override
    public UserType getUserType() {
        return ((User) CurrentUserSession.get()).getUserType();
    }

    @Override
    public int getUserId() {
        return ((User) CurrentUserSession.get()).getId();
    }

}
