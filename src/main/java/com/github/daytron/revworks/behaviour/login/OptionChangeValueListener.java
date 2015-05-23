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

import com.github.daytron.revworks.behaviour.validator.LoginValidatorFactory;
import com.github.daytron.revworks.data.LoginString;
import com.github.daytron.revworks.data.LoginValidationNum;
import com.vaadin.data.Property;
import com.vaadin.ui.TextField;

/**
 * The behaviour when user change the option group's current value.
 *
 * @author Ryan Gilera
 */
public class OptionChangeValueListener implements Property.ValueChangeListener {

    private final TextField usernameField;

    public OptionChangeValueListener(TextField userField) {
        this.usernameField = userField;
    }

    /**
     * Applies the necessary setting for each user type. The textfield is shared
     * by student ID and lecturer's email address.
     *
     * @param event The event object created upon user change the option group's
     * current value
     */
    @Override
    public void valueChange(Property.ValueChangeEvent event) {

        // Retrieve value
        String value = event.getProperty().getValue().toString();

        // Clear previous validators
        usernameField.removeAllValidators();

        // Clears the textfield if option group value is changed
        usernameField.clear();

        // Change username textfield caption according to user type
        if (value.equalsIgnoreCase(LoginString.FORM_OPTION_STUDENT.getText())) {
            usernameField.setCaption(LoginString.FORM_STUDENT_ID.getText());

            // Apply corresponding validator
            usernameField.addValidator(
                    LoginValidatorFactory.buildStudentIDValidator());
            // Apply max entry length for student ID
            usernameField.setMaxLength(
                    LoginValidationNum.STUDENT_ID_LENGTH.getValue());

        } else {
            usernameField.setCaption(LoginString.FORM_LECTURER_EMAIL.getText());

            usernameField.addValidator(
                    LoginValidatorFactory.buildEmailValidator());

            usernameField.setMaxLength(
                    LoginValidationNum.EMAIL_MAX_LENGTH.getValue());
        }

        // Return back the focus
        usernameField.focus();
    }

}
