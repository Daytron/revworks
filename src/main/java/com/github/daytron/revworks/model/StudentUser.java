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
 * The child class of {@link User} for Students user role.
 *
 * @author Ryan Gilera
 */
@SuppressWarnings("serial")
public class StudentUser extends User {

    private final String studentID;

    /**
     * Creates a new StudentUser object with parameter values namely the user
     * id, student id, first name and last name.
     *
     * @param id The identifying id for a user
     * @param studentId The id assigned by the College which acts as a username
     * @param firstname The first name of the lecturer
     * @param lastname The last name of the lecturer
     */
    public StudentUser(int id, String studentId, String firstname,
            String lastname) {
        super(id, firstname, lastname, UserType.STUDENT);
        this.studentID = studentId;
    }

    /**
     * Access the student's id code.
     *
     * @return String object for student id information
     */
    public String getStudentID() {
        return studentID;
    }

}
