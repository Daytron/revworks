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
package com.github.daytron.revworks.authentication;

import com.github.daytron.revworks.data.UserType;
import java.security.Principal;

/**
 * The class to hold user information when login.
 *
 * @author Ryan Gilera
 */
abstract class User implements Principal {

    private final String id;
    private final String firstName;
    private final String lastName;
    private final UserType userType;

    /**
     * Only {@link UserAuthentication} may create new instances.
     *
     * @param name
     */
    User(String id, String firstname, String lastname, UserType userType) {
        this.id = id;
        this.firstName = firstname;
        this.lastName = lastname;
        this.userType = userType;
    }

    /**
     * Returns the unique identifier of a user. {@link Principal} class default
     * implementation of this method refers to the username of a user to
     * identify them uniquely but since there are two different usernames from
     * two different user types, it is better to treat it as User ID as they are
     * identified in the database.
     *
     * @return A unique ID of the user
     */
    @Override
    public String getName() {
        return this.id;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public UserType getUserType() {
        return userType;
    }

    public boolean isLecturerUser() {
        return userType == UserType.LECTURER;
    }

    public boolean isStudentUser() {
        return userType == UserType.STUDENT;
    }
    
    public boolean isAdminUser() {
        return userType == UserType.ADMIN;
    }
}
