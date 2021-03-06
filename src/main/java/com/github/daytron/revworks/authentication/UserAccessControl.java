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
import com.github.daytron.revworks.event.AppEvent.*;
import com.github.daytron.revworks.event.AppEventBus;
import com.github.daytron.revworks.exception.SQLErrorQueryException;
import com.github.daytron.revworks.exception.SQLErrorRetrievingConnectionAndPoolException;
import com.github.daytron.revworks.model.ClassTable;
import com.github.daytron.revworks.service.LecturerDataInserterImpl;
import com.github.daytron.revworks.service.NotificationInserter;
import com.github.daytron.revworks.service.StudentDataInserterImpl;
import com.github.daytron.revworks.view.AdminLoginPopup;
import com.github.daytron.revworks.util.NotificationUtil;
import com.google.common.eventbus.Subscribe;
import com.vaadin.data.Validator;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import java.security.Principal;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An implementation class of {@link AccessControl} interface. The entry point 
 * for all authorisations, checks and validations in the login process for all 
 * users. 
 *
 * @author Ryan Gilera
 */
@SuppressWarnings("serial")
public class UserAccessControl implements AccessControl {

    /**
     * {@inheritDoc}
     */
    @Subscribe
    @Override
    public void signIn(final UserLoginRequestEvent event) {

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
     * user is retrieved, pulled from the user info from the database and
     * save the object to the current session.
     *
     * @param event The custom event for regular user sign-in event
     */
    private void authenticateUserLoginRequest(final UserLoginRequestEvent event) {
        String userTypeString = (String) event.getUserTypeOptionGroup().getValue();

        UserType userType = (userTypeString.equalsIgnoreCase(UserType.STUDENT.getText()))
                ? UserType.STUDENT : UserType.LECTURER;

        String userName = event.getUserField().getValue();
        String password = event.getPasswordField().getValue();

        Principal user;
        
        try {
            UserAuthentication userAuthentication = MainUI.get().getUserAuthentication();
            String semesterID = userAuthentication.verifyCurrentDateWithinASemester();

            user = userAuthentication.authenticate(userType, userName, password);

            CopyOnWriteArrayList<ClassTable> listOfClassTable
                    = userAuthentication.extractClassTables(userType, user,
                            semesterID);

            JDBCConnectionPool connectionPool = userAuthentication.getConnectionPool();
            
            // Saves data to the current session
            // if sign-in on a date 
            // that there is no ongoing semester the semesterID 
            // variable is empty String.
            // if no class found, listOfClassTable is empty too
            CurrentUserSession.set(user, semesterID, listOfClassTable,
                    connectionPool);
            
            // Verifies if this is the only login session for the current user
            // If this is second login session made by the user,
            // it terminates the old session and continue with this new session
            // created. Afterwards this session is recorded in the servlet.
            // See MainUIServlet documentation
            MainUI.MainUIServlet.saveUserSessionInfo(getPrincipalName(),
                    VaadinSession.getCurrent());
            
            if (userType == UserType.LECTURER) {
                AppEventBus.register(new LecturerDataInserterImpl());
                AppEventBus.register(MainUI.get().getLecturerDataProvider());
            } else {
                // Student otherwise
                AppEventBus.register(new StudentDataInserterImpl());
                AppEventBus.register(MainUI.get().getStudentDataProvider());
            }
            
            AppEventBus.register(new NotificationInserter());
            
            MainUI.get().showMainScreen();

            NotificationUtil.showInformation(
                    FontAwesomeIcon.THUMBS_O_UP.getLgSize(),
                    "Welcome " + getFirstName() + "!", "");

        } catch (AuthenticationException 
                | SQLErrorRetrievingConnectionAndPoolException 
                | SQLErrorQueryException ex) {
            Logger.getLogger(UserAccessControl.class.getName()).log(Level
                    .SEVERE, null, ex);
            NotificationUtil.showError(
                    ErrorMsg.SIGNIN_FAILED_CAPTION.getText(),
                    ErrorMsg.CONSULT_YOUR_ADMIN.getText());
        }

    }

    /**
     * {@inheritDoc}
     */
    @Subscribe
    @Override
    public void signInAdmin(final AdminLoginRequestEvent event) {
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
     * user is retrieved, pulled from the user info from the database and
     * save the object to the current session.
     *
     * @param event The custom event for admin sign-in event
     */
    private void authenticateAdminLoginRequest(final AdminLoginRequestEvent event) {
        UserType userType = UserType.ADMIN;
        String userName = event.getUserField().getValue();
        String password = event.getPasswordField().getValue();

        Principal adminUser;

        try {
            UserAuthentication userAuthentication = MainUI.get().getUserAuthentication();
            String semesterID = userAuthentication.verifyCurrentDateWithinASemester();

            adminUser = userAuthentication.authenticate(userType, userName, password);

            // Saves data to the current session
            // Note that for admin access, if sign-in on a date 
            // that there is no ongoing semester the semesterID 
            // variable is empty String.
            // Default list of classes is empty as well
            CurrentUserSession.set(adminUser, semesterID,
                    new CopyOnWriteArrayList<>(), 
                    userAuthentication.getConnectionPool());

            // Verifies if this is the only login session for the current user
            // If this is second login session made by the user,
            // it terminates the old session and continue with this new session
            // created. Afterwards this session is recorded in the servlet.
            // See MainUIServlet documentation
            MainUI.MainUIServlet.saveUserSessionInfo(
                    getPrincipalName(),
                    VaadinSession.getCurrent());
            System.out.println("Admin login to the system..");

            event.getAdminLoginPopup().close();

            // Register handlers
            AppEventBus.register(MainUI.get().getAdminDataInserter());
            
            MainUI.get().showAdminDashboard();

            Notification.show("Welcome "
                    + getFirstName()
                    + "!",
                    Notification.Type.TRAY_NOTIFICATION);
        } catch (AuthenticationException 
                | SQLErrorRetrievingConnectionAndPoolException ex) {
            Logger.getLogger(UserAccessControl.class.getName())
                    .log(Level.SEVERE, null, ex);
            NotificationUtil.showError(
                    ErrorMsg.SIGNIN_FAILED_CAPTION.getText(),
                    ErrorMsg.CONSULT_YOUR_ADMIN.getText());
        }

    }

    /**
     * {@inheritDoc}
     */
    @Subscribe
    @Override
    public void webmasterLinkOnClick(final WebmasterLinkClickEvent event) {
        AdminLoginPopup adminLoginPopup = new AdminLoginPopup();

        adminLoginPopup.setSizeUndefined();
        adminLoginPopup.center();

        UI.getCurrent().addWindow(adminLoginPopup);
    }

    /**
     * {@inheritDoc}
     */
    @Subscribe
    @Override
    public void optionGroupOnChangeValue(final OptionChangeValueEvent event) {
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUserSignedIn() {
        return CurrentUserSession.getPrincipal() != null;
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public boolean isUserAStudent() {
        return ((User) CurrentUserSession.getPrincipal()).isStudentUser();
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public boolean isUserALecturer() {
        return ((User) CurrentUserSession.getPrincipal()).isLecturerUser();
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public boolean isUserAdmin() {
        return ((User) CurrentUserSession.getPrincipal()).isAdminUser();
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public String getPrincipalName() {
        return CurrentUserSession.getPrincipal().getName();
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public String getFirstName() {
        return ((User) CurrentUserSession.getPrincipal()).getFirstName();
    }

    @Override
    public String getLastName() {
        return ((User) CurrentUserSession.getPrincipal()).getLastName();
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public String getFullName() {
        return ((User) CurrentUserSession.getPrincipal()).getFullName();
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public String getLecturerEmail() throws WrongCurrentUserTypeException {
        if (((User) CurrentUserSession.getPrincipal()).isLecturerUser()) {
            return ((LecturerUser) (CurrentUserSession.getPrincipal())).getEmail();
        } else {
            throw new WrongCurrentUserTypeException(
                    ExceptionMsg.WRONG_CURRENT_USER_TYPE_EXCEPTION.getMsg());
        }
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public String getStudentID() throws WrongCurrentUserTypeException {
        if (((User) CurrentUserSession.getPrincipal()).isLecturerUser()) {
            return ((StudentUser) (CurrentUserSession.getPrincipal())).getStudentID();
        } else {
            throw new WrongCurrentUserTypeException(
                    ExceptionMsg.WRONG_CURRENT_USER_TYPE_EXCEPTION.getMsg());
        }
    }

    /**
     * {@inheritDoc} 
     */
    @Subscribe
    @Override
    public void signOut(final UserLogoutRequestEvent event) {
        // Set landing page after signout 
        Page.getCurrent().setLocation(VaadinServlet.getCurrent().getServletConfig()
                .getServletContext().getContextPath());

        CurrentUserSession.signOut();
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public String getUserTypeString() {
        return ((User) CurrentUserSession.getPrincipal()).getUserType().getText();
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public UserType getUserType() {
        return ((User) CurrentUserSession.getPrincipal()).getUserType();
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public int getUserId() {
        return ((User) CurrentUserSession.getPrincipal()).getId();
    }

}
