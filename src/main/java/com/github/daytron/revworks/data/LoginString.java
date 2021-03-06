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
package com.github.daytron.revworks.data;

/**
 * Collection of String constants used in Login screen.
 *
 * @author Ryan Gilera
 */
public enum LoginString {

    // Logo 
    LOGO_LABEL("RevWorks"),
    
    // Admin login
    FORM_ADMIN_WINDOW_LABEL("Admin Login"),
    FORM_ADMIN_LABEL("For administrators only. Enter your credentials."),
    FORM_ADMIN_EMAIL("Username"),
    FORM_CANCEL_BUTTON("Cancel"),
    
    // Login Form
    FORM_OPTIONGROUP_USER("Select login type"),
    FORM_OPTION_STUDENT("Student"),
    FORM_OPTION_LECTURER("Lecturer"),
    FORM_STUDENT_ID("Student ID"),
    FORM_LECTURER_EMAIL("Email"),
    FORM_USER_PASSWORD("Password"),
    FORM_LOGIN_BUTTON("Login"),
    WEBMASTER_LINK_LABEL("Webmaster");

    private final String text;

    private LoginString(String text) {
        this.text = text;
    }

    /**
     * Access the String value for login view.
     * 
     * @return the text value of an item 
     */
    public String getText() {
        return text;
    }

}
