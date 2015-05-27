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
import com.github.daytron.revworks.data.UserType;
import com.github.daytron.revworks.service.NoCurrentUserException;
import com.github.daytron.revworks.util.NotificationUtil;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The login behaviour for the {@link AdminLoginPopup}.
 *
 * @author Ryan Gilera
 */
public class AdminLoginButtonListener implements Button.ClickListener {

    private static final long serialVersionUID = 1L;

    private final TextField userField;
    private final PasswordField passwordField;
    private final AccessControl userAccessControl;

    public AdminLoginButtonListener(TextField userField,
            PasswordField passwordField) {
        this.userField = userField;
        this.passwordField = passwordField;
        this.userAccessControl = MainUI.get().getAccessControl();
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        try {
            userField.validate();
            passwordField.validate();
        } catch (Exception e) {
            userField.setValidationVisible(true);
            passwordField.setValidationVisible(true);

            Logger.getLogger(AdminLoginButtonListener.class.getName())
                    .log(Level.SEVERE, null, e);
            NotificationUtil.showError(
                    ErrorMsg.INVALID_INPUT_CAPTION.getText());
        }
    }

    private void verifyUserCredentials() {
        UserType userType = UserType.ADMIN;
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
            MainUI.MainUIServlet.printSessions("admin login");

        } catch (AuthenticationException ex) {
            Logger.getLogger(AdminLoginButtonListener.class.getName()).log(Level.SEVERE, null, ex);
            NotificationUtil.showError(
                    ErrorMsg.SIGNIN_FAILED_CAPTION.getText(),
                    ex.getMessage());
        } catch (NoCurrentUserException ex) {
            Logger.getLogger(AdminLoginButtonListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
