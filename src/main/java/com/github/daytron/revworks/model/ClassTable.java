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

/**
 * A model class for Class table.
 *
 * @author Ryan Gilera
 */
@SuppressWarnings("serial")
public class ClassTable {

    private final int id;
    private final String moduleId;
    private final String moduleName;
    private final LecturerUser lecturerUser;

    /**
     * A class constructor that takes an integer, two Strings, and a LecturerUser 
     * object.
     * 
     * @param id the id of the class
     * @param moduleId the module id associated with the class
     * @param moduleName module name associated with the class
     * @param lecturerUser The lecturer user object associated with the class
     */
    public ClassTable(int id, String moduleId, String moduleName, 
            LecturerUser lecturerUser) {
        this.id = id;
        this.moduleId = moduleId;
        this.moduleName = moduleName;
        this.lecturerUser = lecturerUser;
    }

    /**
     * Access the lecturer user object.
     * 
     * @return LecturerUser object
     */
    public LecturerUser getLecturerUser() {
        return lecturerUser;
    }

    /**
     * Access the class id.
     * 
     * @return class id as integer 
     */
    public int getId() {
        return id;
    }

    /**
     * Access module id.
     * 
     * @return module id as String 
     */
    public String getModuleId() {
        return moduleId;
    }

    /**
     * Access the module name.
     * 
     * @return module name as String 
     */
    public String getModuleName() {
        return moduleName;
    }

}
