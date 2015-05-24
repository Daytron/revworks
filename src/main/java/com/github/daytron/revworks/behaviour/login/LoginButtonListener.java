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
package com.github.daytron.revworks.behaviour.login;

import com.github.daytron.revworks.MainUI;
import com.github.daytron.revworks.authentication.AccessControl;
import com.github.daytron.revworks.authentication.AuthenticationException;
import com.github.daytron.revworks.data.ErrorMsg;
import com.github.daytron.revworks.service.NoCurrentUserException;
import com.github.daytron.revworks.data.UserType;
import com.github.daytron.revworks.service.CurrentUserSession;
import com.github.daytron.revworks.ui.DashboardScreen;
import com.github.daytron.revworks.util.NotificationUtil;
import com.vaadin.data.Validator;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The login behaviour upon user click the login button.
 *
 * @author Ryan Gilera
 */
public class LoginButtonListener implements Button.ClickListener {

    private static final long serialVersionUID = 1L;

    private final OptionGroup userTypeOptionGroup;
    private final TextField userField;
    private final PasswordField passwordField;
    private UserType userType;

    private final AccessControl userAccessControl;

    public LoginButtonListener(TextField textField, PasswordField passwordField,
            OptionGroup userType, AccessControl accessControl) {
        this.userTypeOptionGroup = userType;
        this.userField = textField;
        this.passwordField = passwordField;

        this.userAccessControl = accessControl;
    }

    /**
     * Validates the userField and passwordField.
     *
     * @param event The event object created when the button is clicked
     */
    @Override
    public void buttonClick(Button.ClickEvent event) {

        try {
            userField.validate();
            passwordField.validate();

            verifyUserCredentials();
        } catch (Validator.InvalidValueException e) {
            userField.setValidationVisible(true);
            passwordField.setValidationVisible(true);
            
            Logger.getLogger(LoginButtonListener.class.getName()).log(Level.SEVERE, null, e);
            NotificationUtil.showError(
                    ErrorMsg.INVALID_INPUT_CAPTION.getText());
        }

    }

    private void verifyUserCredentials() {
        determinUserType();

        String userName = userField.getValue();
        String password = passwordField.getValue();

        try {
            // Retrieve the user upon authentication
            userAccessControl.signIn(userType, userName, password);
            
            
            // Verifies if this is the only login session for the current user
            // If this is second login session made by the user,
            // it terminates the old session and continue with this new session
            // created. Afterwards this session is recorded in the servlet.
            // See MainUIServlet documentation
            MainUI.MainUIServlet.saveUserSessionInfo(
                    userAccessControl.getPrincipalName(), 
                    VaadinSession.getCurrent());
            MainUI.MainUIServlet.printSessions("user login");
            
            DashboardScreen tempUI = new DashboardScreen();
            UI.getCurrent().setContent(tempUI);
            
            Notification.show("Welcome "
                    + userAccessControl.getFirstName()
                    + "!",
                    Notification.Type.TRAY_NOTIFICATION);
                   

        } catch (AuthenticationException ex) {
            Logger.getLogger(LoginButtonListener.class.getName()).log(Level.SEVERE, null, ex);
            NotificationUtil.showError(
                    ErrorMsg.SIGNIN_FAILED_CAPTION.getText(), 
                    ex.getMessage());
        } catch (NoCurrentUserException ex) {
            Logger.getLogger(LoginButtonListener.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void determinUserType() {
        String userTypeString = (String) userTypeOptionGroup.getValue();

        userType = (userTypeString.equalsIgnoreCase(UserType.STUDENT.getText()))
                ? UserType.STUDENT : UserType.LECTURER;
    }

}
