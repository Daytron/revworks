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
package com.github.daytron.revworks.model;

import com.github.daytron.revworks.data.UserType;

/**
 * The child class of {@link User} for Lecturers user role.
 *
 * @author Ryan Gilera
 */
@SuppressWarnings("serial")
public class LecturerUser extends User {

    private final String email;

    /**
     * Creates a new LecturerUser object with parameter values namely the user
     * id, lecturer id, lecturer email, first name and last name.
     *
     * @param userId The identifying id for a user
     * @param email The email assigned by the College which acts as a username
     * @param firstname The first name of the lecturer
     * @param lastname The last name of the lecturer
     */
    public LecturerUser(int userId, String email,
            String firstname, String lastname) {
        super(userId, firstname, lastname, UserType.LECTURER);
        this.email = email;
    }

    /**
     * Access the lecturer's email address.
     *
     * @return String object for lecturer's email information
     */
    public String getEmail() {
        return email;
    }
}
