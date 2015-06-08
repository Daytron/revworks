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
 * Collection of Login validation messages.
 *
 * @author Ryan Gilera
 */
public enum LoginValidationMsg {

    STUDENT_ID_INVALID("Student ID must be 6 numbers (was {0})"),
    STUDENT_ID_EMPTY("Student ID is missing"),
    LECTURER_EMAIL_INVALID("Invalid email"),
    LECTURER_EMAIL_EMPTY("Email is missing"),
    PASSWORD_INVALID("Password must be between 6 to 16 characters (was {0})"),
    PASSWORD_EMPTY("Password is missing");
    
    private final String text;

    private LoginValidationMsg(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

}
