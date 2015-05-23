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

import com.github.daytron.revworks.ui.constants.UserType;
import java.security.Principal;

/**
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

    @Override
    public String getName() {
        return this.firstName + " " + this.lastName;
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

    public String getId() {
        return id;
    }
    
    public boolean isLecturerUser() {
        return userType == UserType.LECTURER;
    }
    
    public boolean isStudentUser() {
        return userType == UserType.STUDENT;
    }
}
