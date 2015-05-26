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

import com.github.daytron.revworks.authentication.AccessControl;
import com.github.daytron.revworks.behaviour.login.LoginButtonListener;
import com.github.daytron.revworks.behaviour.login.OptionChangeValueListener;
import com.github.daytron.revworks.behaviour.login.WebmasterLinkButtonListener;
import com.github.daytron.revworks.behaviour.validator.LoginValidatorFactory;
import com.github.daytron.revworks.data.ExternalLink;
import com.github.daytron.revworks.data.LoginString;
import com.github.daytron.revworks.data.LoginValidationNum;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
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
public final class LoginScreen extends CssLayout {
    private static final long serialVersionUID = 1L;
    
    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginButton;

    private final AccessControl accessControl;

    public LoginScreen(AccessControl accessControl) {
        this.accessControl = accessControl;
        buildUI();
        this.usernameField.focus();
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
        addComponent(createFooter());
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
        userOptionGroup.setValue(LoginString.FORM_OPTION_STUDENT.getText());
        userOptionGroup.setNullSelectionAllowed(false);
        userOptionGroup.addStyleName("horizontal");

        // TextField for userid/email
        usernameField = new TextField(
                LoginString.FORM_STUDENT_ID.getText());
        usernameField.setWidth(15, UNITS_EM);
        usernameField.setMaxLength(
                LoginValidationNum.STUDENT_ID_LENGTH.getValue());
        // By default, hides validation error. Only to show later on explicitly
        // using the submit button
        usernameField.setValidationVisible(false);

        // Apply corresponding validator by default
        usernameField.addValidator(
                LoginValidatorFactory.buildStudentIDValidator());

        // Add listener to option group
        userOptionGroup.addValueChangeListener(
                new OptionChangeValueListener(usernameField));

        // Password field
        passwordField = new PasswordField(
                LoginString.FORM_USER_PASSWORD.getText());
        passwordField.setWidth(15, UNITS_EM);
        passwordField.setMaxLength(
                LoginValidationNum.PASSWORD_MAX_LENGTH.getValue());
        passwordField.addValidator(
                LoginValidatorFactory.buildPasswordLengthValidator());
        // By default, hides validation error. Only to show later on explicitly
        // using the submit button
        passwordField.setValidationVisible(false);

        // For login button
        loginButton = new Button(
                LoginString.FORM_LOGIN_BUTTON.getText());
        loginButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        loginButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        loginButton.addClickListener(new LoginButtonListener(
                usernameField, passwordField, userOptionGroup,
                accessControl));

        // Add all together
        loginFormLayout.addComponents(userOptionGroup,
                usernameField, passwordField, loginButton);

        return loginFormLayout;
    }

    /**
     * Creates the component for the footer.
     *
     * @return The generated footer component
     */
    private Component createFooter() {
        final VerticalLayout footerLayout = new VerticalLayout();
        footerLayout.addStyleName("login-footer");
        footerLayout.setHeight("60px");
        footerLayout.setWidth("100%");

        // The first row of contents which has the external links
        final HorizontalLayout footerLinksLayout = new HorizontalLayout();
        footerLinksLayout.setSizeUndefined();

        // External links to student and lecturer portal
        Link studentPortalLink = new Link(ExternalLink.STUDENT_PORTAL.getName(),
                new ExternalResource(ExternalLink.STUDENT_PORTAL.getLink()));
        Link lecturerPortalLink = new Link(ExternalLink.LECTURER_PORTAL.getName(),
                new ExternalResource(ExternalLink.LECTURER_PORTAL.getLink()));

        // The button masquerading as a link to launch admin login form 
        Button webmasterButtonLink = new Button(
                LoginString.WEBMASTER_LINK_LABEL.getText());
        webmasterButtonLink.setStyleName(ValoTheme.BUTTON_LINK);
        webmasterButtonLink.setSizeUndefined();
        webmasterButtonLink.addClickListener(
                new WebmasterLinkButtonListener(accessControl));

        // Add these links and button together
        footerLinksLayout.addComponents(studentPortalLink,
                lecturerPortalLink, webmasterButtonLink);
        footerLinksLayout.setSpacing(true);

        // The final row of the footer
        final HorizontalLayout footerBottomLayout = new HorizontalLayout();
        Label allRightsReservedLabel = new Label("All Rights Reserved 2015. Ryan Gilera");
        footerBottomLayout.addComponent(allRightsReservedLabel);
        footerBottomLayout.setSizeUndefined();

        // Bring all of them together
        footerLayout.addComponents(footerLinksLayout, footerBottomLayout);
        // Sets positions for the two rows
        footerLayout.setComponentAlignment(footerLinksLayout, Alignment.MIDDLE_CENTER);
        footerLayout.setComponentAlignment(footerBottomLayout, Alignment.BOTTOM_CENTER);

        return footerLayout;
    }

}
