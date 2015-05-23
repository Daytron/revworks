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

/**
 *
 * @author Ryan Gilera
 */
class StudentUser extends User {

    private final String studentID;

    public StudentUser(String id, String studentID, String firstname, 
            String lastname, UserType userType) {
        super(id, firstname, lastname, userType);
        this.studentID = studentID;
    }

    public String getStudentID() {
        return studentID;
    }
    
    

}
