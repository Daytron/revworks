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
package com.github.daytron.revworks.ui;

import com.github.daytron.revworks.ui.constants.LoginString;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * The login page as the main landing page of application.
 *
 * @author Ryan Gilera
 */
public class LoginScreen extends CssLayout {

    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginButton;

    public LoginScreen() {
        buildUI();
        usernameField.focus();
    }

    /**
     * Calls createContent method, align its position and attach it to CssLayout
     */
    private void buildUI() {
        addStyleName("login-screen");

        Component loginFormComponent = createContent();

        VerticalLayout centerFormLayout = new VerticalLayout();
        centerFormLayout.setStyleName("centering-layout");
        centerFormLayout.addComponent(loginFormComponent);

        centerFormLayout.setComponentAlignment(loginFormComponent,
                Alignment.MIDDLE_CENTER);

        addComponent(centerFormLayout);
    }

    /**
     * Creates the main content of the login page.
     *
     * @return Component container that holds all the widgets
     */
    private Component createContent() {
        // Create form layout
        VerticalLayout panelLayout = new VerticalLayout();
        panelLayout.addStyleName("main-panel");
        panelLayout.setSizeUndefined();
        panelLayout.setMargin(false);

        // Generate Logo
        Label logoMainLabel = new Label("<h1>"
                + LoginString.LOGO_LABEL.getText() + "</h1>",
                ContentMode.HTML);
        logoMainLabel.addStyleName("logo-main");

        // Generate login form
        FormLayout loginFormLayout = createForm();

        // add all of them to the layout container
        panelLayout.addComponents(logoMainLabel, loginFormLayout);
        // Reposition logo
        panelLayout.setComponentAlignment(logoMainLabel, Alignment.TOP_CENTER);

        return panelLayout;
    }

    /**
     * Creates the login form with predefined CSS style.
     *
     * @return FormLayout component
     */
    private FormLayout createForm() {
        FormLayout loginFormLayout = new FormLayout();

        // Options for students or lecturers
        OptionGroup userOptionGroup = new OptionGroup(
                LoginString.FORM_OPTIONGROUP_USER.getText());
        userOptionGroup.addItems(
                LoginString.FORM_OPTION_STUDENT.getText(),
                LoginString.FORM_OPTION_LECTURER.getText());
        // Set default value
        userOptionGroup.select(LoginString.FORM_OPTION_STUDENT.getText());
        userOptionGroup.setNullSelectionAllowed(false);
        userOptionGroup.setImmediate(true);
        userOptionGroup.addStyleName("horizontal");

        // TextField for userid/email
        usernameField = new TextField(
                LoginString.FORM_USER_ID.getText());
        usernameField.setWidth(15, UNITS_EM);

        // Add listener to option group
        userOptionGroup.addValueChangeListener(new OptionChangeValueListener());

        // Password field
        passwordField = new PasswordField(
                LoginString.FORM_USER_PASSWORD.getText());
        passwordField.setWidth(15, UNITS_EM);

        // For login button
        loginButton = new Button(
                LoginString.FORM_LOGIN_BUTTON.getText());
        loginButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        loginButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);

        // Add all together
        loginFormLayout.addComponents(userOptionGroup,
                usernameField, passwordField, loginButton);

        return loginFormLayout;
    }

    /**
     * Listener inner class for custom behaviour when option group value is
     * changed. Change the username caption label depending on the type of user
     * selected in the option group.
     */
    private class OptionChangeValueListener implements ValueChangeListener {

        @Override
        public void valueChange(Property.ValueChangeEvent event) {
            // retrieve value
            String value = event.getProperty().getValue().toString();

            // Change username textfield caption according to user type
            if (value.equalsIgnoreCase(LoginString.FORM_OPTION_STUDENT.getText())) {
                usernameField.setCaption(LoginString.FORM_USER_ID.getText());
            } else {
                usernameField.setCaption(LoginString.FORM_USER_EMAIL.getText());
            }

            // Clears the textfield if option group value is changed
            usernameField.setValue("");
            // Return back the focus
            usernameField.focus();
        }

    }

}
