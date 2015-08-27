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
package com.github.daytron.revworks.view;

import com.github.daytron.revworks.validator.LoginValidatorFactory;
import com.github.daytron.revworks.data.LoginString;
import com.github.daytron.revworks.data.LoginValidationNum;
import com.github.daytron.revworks.event.AppEvent;
import com.github.daytron.revworks.event.AppEventBus;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/**
 * The popup modal window for Admin login form.
 *
 * @author Ryan Gilera
 */
@SuppressWarnings("serial")
public final class AdminLoginPopup extends Window {
    
    private static final long serialVersionUID = 1L;
    private TextField userField;
    
    /**
     * A class constructor that builds its UI components upon object creation. 
     * Sets initial focus to the username field.
     */
    public AdminLoginPopup() {
        super(LoginString.FORM_ADMIN_WINDOW_LABEL.getText()); // Window label

        VerticalLayout popupLayout = new VerticalLayout();
        popupLayout.setMargin(true);
        popupLayout.setSizeUndefined();
        
        setSizeUndefined();
        setContent(popupLayout);
        
        setClosable(true);
        setModal(true);
        
        final Label welcomeLabel = new Label();
        welcomeLabel.setCaption(LoginString.FORM_ADMIN_LABEL.getText());
        
        popupLayout.addComponent(welcomeLabel);
        popupLayout.addComponent(buildLoginForm());
        
        // Sets default focus on username field
        userField.focus();
    }

    /**
     * Creates the admin login form.
     *
     * @return A FormLayout object
     */
    private FormLayout buildLoginForm() {
        // Username field
        userField = new TextField(
                LoginString.FORM_ADMIN_EMAIL.getText());
        userField.setWidth(15, UNITS_EM);
        userField.setMaxLength(
                LoginValidationNum.EMAIL_MAX_LENGTH.getValue());
        userField.addValidator(
                LoginValidatorFactory.buildEmailValidator());
        userField.setValidationVisible(false);

        // Password field
        final PasswordField passwordField = new PasswordField(
                LoginString.FORM_USER_PASSWORD.getText());
        passwordField.setWidth(15, UNITS_EM);
        passwordField.setMaxLength(
                LoginValidationNum.PASSWORD_MAX_LENGTH.getValue());
        passwordField.addValidator(
                LoginValidatorFactory.buildPasswordLengthValidator());
        passwordField.setValidationVisible(false);

        // Login Button
        Button loginButton = new Button(
                LoginString.FORM_LOGIN_BUTTON.getText());
        loginButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        loginButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        loginButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                AppEventBus.post(new AppEvent.AdminLoginRequestEvent(
                        userField, passwordField, AdminLoginPopup.this));
            }
        });
        
        final FormLayout loginFormLayout = new FormLayout(userField, passwordField,
                loginButton);
        
        return loginFormLayout;
    }
    
}
