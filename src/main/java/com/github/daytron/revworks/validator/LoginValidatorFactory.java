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
package com.github.daytron.revworks.validator;

import com.github.daytron.revworks.data.LoginValidationMsg;
import com.github.daytron.revworks.data.LoginValidationNum;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;

/**
 * Generates validators for the Login UI.
 *
 * @author Ryan Gilera
 */
public class LoginValidatorFactory {

    private LoginValidatorFactory() {
    }

    /**
     * Builds a custom integer range validator for student ID.
     * 
     * @return The StudentIDRangeValidator object
     */
    public static StudentIDRangeValidator buildStudentIDValidator() {

        StudentIDRangeValidator studentIDRangeValidator
                = new StudentIDRangeValidator(
                        LoginValidationMsg.STUDENT_ID_INVALID.getText(),
                        LoginValidationNum.STUDENT_ID_MIN_VALUE.getValue(),
                        LoginValidationNum.STUDENT_ID_MAX_VALUE.getValue());

        return studentIDRangeValidator;
    }

    /**
     * Builds a default email validator.
     *
     * @return The EmailValidator object
     */
    public static EmailValidator buildEmailValidator() {
        return new EmailValidator(
                LoginValidationMsg.LECTURER_EMAIL_INVALID.getText());
    }

    /**
     * Builds a default string length validator for password length.
     *
     * @return The StringLengthValidator object
     */
    public static StringLengthValidator buildPasswordLengthValidator() {
        return new StringLengthValidator(
                LoginValidationMsg.PASSWORD_INVALID.getText(),
                LoginValidationNum.PASSWORD_MIN_LENGTH.getValue(),
                LoginValidationNum.PASSWORD_MAX_LENGTH.getValue(),
                false);
    }

}
