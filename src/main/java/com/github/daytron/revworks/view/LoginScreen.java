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

import com.github.daytron.revworks.MainUI;
import com.github.daytron.revworks.validator.LoginValidatorFactory;
import com.github.daytron.revworks.data.ExternalLink;
import com.github.daytron.revworks.data.LoginString;
import com.github.daytron.revworks.data.LoginValidationNum;
import com.github.daytron.revworks.event.AppEvent;
import com.github.daytron.revworks.event.AppEventBus;
import com.vaadin.data.Property;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Sizeable;
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
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import elemental.events.KeyboardEvent;

/**
 * The login page as the main landing page of application.
 *
 * @author Ryan Gilera
 */
@SuppressWarnings("serial")
public final class LoginScreen extends CssLayout {

    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginButton;

    public LoginScreen() {
        buildUI();
        this.usernameField.focus();
    }

    /**
     * Calls createContent method, align its position and attach it to CssLayout
     */
    private void buildUI() {
        addStyleName("login-screen");

        Component loginFormComponent = createContent();

        VerticalLayout contentLayout = new VerticalLayout();
        contentLayout.setStyleName("centering-layout");
        contentLayout.addComponent(loginFormComponent);

        contentLayout.setComponentAlignment(loginFormComponent,
                Alignment.MIDDLE_CENTER);

        addComponent(contentLayout);
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
        final OptionGroup userOptionGroup = new OptionGroup(
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
        usernameField.setWidth(16.4f, UNITS_EM);
        usernameField.setMaxLength(
                LoginValidationNum.STUDENT_ID_LENGTH.getValue());
        // By default, hides validation error. Only to show later on explicitly
        // using the submit button
        usernameField.setValidationVisible(false);
        usernameField.setIcon(FontAwesome.USER);

        // Apply corresponding validator by default
        usernameField.addValidator(
                LoginValidatorFactory.buildStudentIDValidator());

        // Add listener to option group
        userOptionGroup.addValueChangeListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                AppEventBus.post(new AppEvent.OptionChangeValueEvent(
                        usernameField, event.getProperty().getValue().toString()));
            }
        });

        // Password field
        passwordField = new PasswordField(
                LoginString.FORM_USER_PASSWORD.getText());
        passwordField.setWidth(16.4f, UNITS_EM);
        passwordField.setMaxLength(
                LoginValidationNum.PASSWORD_MAX_LENGTH.getValue());
        passwordField.setIcon(FontAwesome.LOCK);
        passwordField.addValidator(
                LoginValidatorFactory.buildPasswordLengthValidator());
        // By default, hides validation error. Only to show later on explicitly
        // using the submit button
        passwordField.setValidationVisible(false);

        // Horizontal button bar
        HorizontalLayout buttonBar = new HorizontalLayout();
        buttonBar.setWidth("100%");
        buttonBar.setSpacing(true);

        // For login button
        loginButton = new Button(
                LoginString.FORM_LOGIN_BUTTON.getText());
        loginButton.setDescription("Sign in");
        loginButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        loginButton.addStyleName(ValoTheme.BUTTON_PRIMARY);

        loginButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                AppEventBus.post(new AppEvent.UserLoginRequestEvent(
                        usernameField, passwordField, userOptionGroup));
            }
        });

        // Not yet register button
        Button notYetRegisterButton = new Button("Not yet registered?");
        notYetRegisterButton.setDescription("If you're not yet registered "
                + "click here for instructtion.");
        notYetRegisterButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        notYetRegisterButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                Window noteWindow = new Window();
                noteWindow.setClosable(true);
                noteWindow.setModal(true);
                noteWindow.setResizable(true);
                noteWindow.setDraggable(false);
                noteWindow.setCloseShortcut(KeyboardEvent.KeyCode.ESC, null);
                
                noteWindow.setWidth(300.0f, Sizeable.Unit.PIXELS);
                
                VerticalLayout content = new VerticalLayout();
                content.setWidth("100%");
                content.setMargin(true);
                content.setSpacing(true);
                
                Label titleLabel = new Label("Registration Notice");
                titleLabel.addStyleName(ValoTheme.LABEL_BOLD);
                
                Label noteLabel = new Label("If you would like to join to the "
                        + "ongoing user test pilot, then send me an email to "
                        + "ryangilera@gmail.com with your:");
                
                
                Label listLabel = new Label();
                listLabel.setContentMode(ContentMode.HTML);
                listLabel.setValue("<ul>"
                        + "<li>GSM student id</li>"
                        + "<li>Your first and last name</li>"
                        + "<li>Your module id and name</li>"
                        + "<li>Your complete course name</li>"
                        + "<li>Lecturer name</li>"
                        + "<li>Desired password</li>"
                        + "</ul>");
                
                Label lastNoteLabel = new Label("I'll contact you as "
                        + "soon as possible. You may close this window "
                        + "to return back to the login area. Thank you.");
                
                content.addComponents(titleLabel,noteLabel,listLabel,
                        lastNoteLabel);
                noteWindow.setContent(content);
                
                MainUI.get().addWindow(noteWindow);
            }
        });

        Label expandingGap = new Label();
        expandingGap.setWidth("100%");
        buttonBar.addComponents(loginButton, notYetRegisterButton,expandingGap);
        buttonBar.setExpandRatio(expandingGap, 1);

        // Add all together
        loginFormLayout.addComponents(userOptionGroup,
                usernameField, passwordField, buttonBar);

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

        Link gsmLearnLinkButton = new Link(ExternalLink.GSM_LEARN.getName(),
                new ExternalResource(ExternalLink.GSM_LEARN.getLink()));

        // The button masquerading as a link to launch admin login form 
        Button webmasterButtonLink = new Button(
                LoginString.WEBMASTER_LINK_LABEL.getText());
        webmasterButtonLink.setStyleName(ValoTheme.BUTTON_LINK);
        webmasterButtonLink.setSizeUndefined();
        webmasterButtonLink.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                AppEventBus.post(new AppEvent.WebmasterLinkClickEvent());
            }
        });

        // Add these links and button together
        footerLinksLayout.addComponents(studentPortalLink,
                lecturerPortalLink, gsmLearnLinkButton, webmasterButtonLink);
        footerLinksLayout.setSpacing(true);

        // The final row of the footer
        final HorizontalLayout footerBottomLayout = new HorizontalLayout();
        Label allRightsReservedLabel = new Label("All Rights Reserved 2015. "
                + "Created by Ryan Gilera [");
        footerBottomLayout.addComponent(allRightsReservedLabel);
        footerBottomLayout.setSizeUndefined();

        Link githubLink = new Link(ExternalLink.MY_GITHUB_PAGE.getName(),
                new ExternalResource(ExternalLink.MY_GITHUB_PAGE.getLink()));
        githubLink.setTargetName("_blank");
        githubLink.setSizeUndefined();
        footerBottomLayout.addComponent(githubLink);

        Label midBracketLabel = new Label("] [");
        midBracketLabel.setSizeUndefined();
        footerBottomLayout.addComponent(midBracketLabel);

        Link linkedinLink = new Link(ExternalLink.MY_LINKEDIN_PAGE.getName(),
                new ExternalResource(ExternalLink.MY_LINKEDIN_PAGE.getLink()));
        linkedinLink.setTargetName("_blank");
        linkedinLink.setSizeUndefined();
        footerBottomLayout.addComponent(linkedinLink);

        Label endOfLabel = new Label("]");
        endOfLabel.setSizeUndefined();
        footerBottomLayout.addComponent(endOfLabel);

        // Bring all of them together
        footerLayout.addComponents(footerLinksLayout, footerBottomLayout);
        // Sets positions for the two rows
        footerLayout.setComponentAlignment(footerLinksLayout, Alignment.MIDDLE_CENTER);
        footerLayout.setComponentAlignment(footerBottomLayout, Alignment.BOTTOM_CENTER);

        return footerLayout;
    }

}
