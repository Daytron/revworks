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
package com.github.daytron.revworks.behaviour.validator;

import com.vaadin.data.validator.AbstractValidator;
import com.vaadin.data.validator.IntegerRangeValidator;

/**
 * Custom validator for student ID field.
 *
 * @author Ryan Gilera
 */
public class StudentIDRangeValidator extends AbstractValidator<String> {

    private static final long serialVersionUID = 1L;
    private final IntegerRangeValidator integerRangeValidator;

    public StudentIDRangeValidator(String errorMessage, Integer minimumValue,
            Integer maximumValue) {
        super(errorMessage);
        this.integerRangeValidator = new IntegerRangeValidator(errorMessage, minimumValue, maximumValue);

    }

    /**
     * Parse and validates input string as student ID.
     *
     * @param value The String input
     * @return True if valid, otherwise false
     */
    @Override
    protected boolean isValidValue(String value) {
        try {
            Integer resultValue = Integer.parseInt(value);
            integerRangeValidator.validate(resultValue);
            return true;
        } catch (NumberFormatException numFormEx) {
            // TODO: handle exception
            System.out.println("Cannot be parsed as Integer: " + numFormEx);
            return false;
        } catch (InvalidValueException ex) {
            // TODO: handle exception
            System.out.println("Unknown exception: " + ex);
            return false;
        }
    }

    /**
     * Returns the type of input
     *
     * @return The String class as type
     */
    @Override
    public Class<String> getType() {
        return String.class;
    }

}
