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

import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

/**
 * The login behaviour upon user click the login button.
 *
 * @author Ryan Gilera
 */
public class LoginButtonListener implements Button.ClickListener {

    private final TextField userField;
    private final PasswordField passwordField;

    public LoginButtonListener(TextField textField, PasswordField passwordField) {
        this.userField = textField;
        this.passwordField = passwordField;
    }

    /**
     * Validates the userField and passwordField.
     *
     * @param event The event object created when the button is clicked
     */
    @Override
    public void buttonClick(Button.ClickEvent event) {
        Button loginButton = event.getButton();
        loginButton.setEnabled(false);

        try {
            userField.validate();
            passwordField.validate();

            Notification.show("Valid");
        } catch (Exception e) {
            userField.setValidationVisible(true);
            passwordField.setValidationVisible(true);

            loginButton.setEnabled(true);
        }

    }

}
