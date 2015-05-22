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
package com.github.daytron.revworks.ui.constants;

/**
 *
 * @author Ryan Gilera
 */
public enum LoginValidationNum {
    STUDENT_ID_MIN_VALUE(10000),
    STUDENT_ID_MAX_VALUE(99999),
    STUDENT_ID_LENGTH(5),
    EMAIL_MAX_LENGTH(254),
    PASSWORD_MAX_LENGTH(16),
    PASSWORD_MIN_LENGTH(6)
    ;
    private final int value;

    private LoginValidationNum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
