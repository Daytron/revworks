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
 * String constants used in Login UI
 * 
 * @author Ryan Gilera
 */
public enum LoginString {
    // Logo 
    LOGO_LABEL("RevWorks"),
   
    // Login Form
    FORM_OPTIONGROUP_USER("Select login type"),
    FORM_OPTION_STUDENT("Student"),
    FORM_OPTION_LECTURER("Lecturer"),
    
    FORM_STUDENT_ID("Student ID"),
    FORM_LECTURER_EMAIL("Lecturer Email"),
    
    FORM_USER_PASSWORD("Password"),
    
    FORM_LOGIN_BUTTON("Login");

    private final String text;
    private LoginString(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
    
    
    
    
}
