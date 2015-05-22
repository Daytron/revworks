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

import com.github.daytron.revworks.authentication.AccessControl;
import com.github.daytron.revworks.authentication.AuthenticationException;
import com.github.daytron.revworks.authentication.User;
import com.github.daytron.revworks.service.CurrentUserSession;
import com.github.daytron.revworks.authentication.UserAccessControl;
import com.github.daytron.revworks.ui.constants.UserType;
import com.vaadin.data.Validator;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import java.security.Principal;
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
        Button loginButton = event.getButton();

        try {
            userField.validate();
            passwordField.validate();

            verifyUserCredentials();
        } catch (Validator.InvalidValueException e) {
            userField.setValidationVisible(true);
            passwordField.setValidationVisible(true);
             Logger.getLogger(LoginButtonListener.class.getName()).log(Level.SEVERE, null, e);
            Notification.show("Exception at Button listener " + 
                    e.toString(), Notification.Type.ERROR_MESSAGE);
        }

    }

    private void verifyUserCredentials() {
        determinUserType();

        String userName = userField.getValue();
        String password = passwordField.getValue();
        
        if (userField == null) {
            System.out.println("Userfield is null!!!!!!!!!!");
        }
        
        if (passwordField == null) {
            System.out.println("Password field is null!!!!!!!!");
        }
        
        if (userType == null) {
            System.out.println("UserType is null!!!!!!!!!!!!!!");
        }
        
        if (userAccessControl == null) {
            System.out.println("userAccessControl is null!!!!!!!!!");
        }

        try {
            // Retrieve the user upon authentication
            Principal user = userAccessControl.authenticate(
                    userType, userName, password);

            User userObject = (User) user;
            
            if (user != null) {
                // Set the user as current user in the current session
                CurrentUserSession.set(user);
                
                Notification.show("User:\n" +
                        userObject.getId() +
                        "Name: " +
                        userObject.getName() +
                        "User type: " +
                        userObject.getUserType().getText(), 
                        Notification.Type.TRAY_NOTIFICATION);
            } else {
                System.out.println("Principal is null");
            }

        } catch (AuthenticationException ex) {
            Notification.show("Verification exception: " + 
                    ex.toString(), Notification.Type.ERROR_MESSAGE);
            Logger.getLogger(LoginButtonListener.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    private void determinUserType() {
        String userTypeString = (String) userTypeOptionGroup.getValue();

        userType = (userTypeString.equalsIgnoreCase(UserType.STUDENT.getText()))
                ? UserType.STUDENT : UserType.LECTURER;
    }

}
