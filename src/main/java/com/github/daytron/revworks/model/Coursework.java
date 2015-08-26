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

import java.io.File;
import java.time.LocalDateTime;

/**
 * Coursework model class for coursework table.
 *
 * @author Ryan Gilera
 */
@SuppressWarnings("serial")
public class Coursework {

    private final int id;
    private final String title;
    private final LocalDateTime dateSubmitted;
    private final String fileExtension;
    private final File courseworkFile;
    private final ClassTable classTable;
    private final StudentUser studentUser;
    private final Boolean readStudent;
    private final Boolean readLecturer;

    /**
     * A class constructor that takes an integer, two String objects, 
     * LocalDateTime object, a File object, a ClassTable object, a StudentUser 
     * object and two booleans.
     * 
     * @param id coursework id
     * @param title coursework title
     * @param dateSubmitted submission date of the coursework
     * @param courseworkFile file of the coursework
     * @param fileExtension file extension of the coursework file
     * @param classTable the associated class
     * @param studentUser the owner of coursework
     * @param isReadStudent boolean value if the student has read the latest 
     * comments and notes associated with the coursework. 
     * @param isReadLecturer boolean value if the lecturer has read the latest 
     * comments and notes associated with the coursework.  
     */
    public Coursework(int id, String title, LocalDateTime dateSubmitted,
            File courseworkFile, String fileExtension, ClassTable classTable, 
            StudentUser studentUser, boolean isReadStudent, boolean isReadLecturer) {
        this.id = id;
        this.title = title;
        this.dateSubmitted = dateSubmitted;
        this.fileExtension = fileExtension;
        this.courseworkFile = courseworkFile;
        this.classTable = classTable;
        this.studentUser = studentUser;
        this.readStudent = isReadStudent;
        this.readLecturer = isReadLecturer;
    }

    /**
     * Access the associated class
     * 
     * @return ClassTable object 
     */
    public ClassTable getClassTable() {
        return classTable;
    }

    /**
     * Access the coursework id.
     * 
     * @return id as an integer 
     */
    public int getId() {
        return id;
    }

    /**
     * Access the title of the coursework.
     * 
     * @return title as String 
     */
    public String getTitle() {
        return title;
    }

    /**
     * Access the submission date time of the coursework.
     * 
     * @return LocalDateTime object
     */
    public LocalDateTime getDateSubmitted() {
        return dateSubmitted;
    }

    /**
     * Access the student user object.
     * 
     * @return StudentUser object
     */
    public StudentUser getStudentUser() {
        return studentUser;
    }

    /**
     * Access the coursework file.
     * 
     * @return File object 
     */
    public File getCourseworkFile() {
        return courseworkFile;
    }

    /**
     * Access the file extension of coursework file.
     * 
     * @return file extension as String 
     */
    public String getFileExtension() {
        return fileExtension;
    }

    /**
     * Determines if the associated lecturer has read all the comments and notes.
     * 
     * @return true if the lecturer is up to date, otherwise false. 
     */
    public boolean isReadLecturer() {
        return readLecturer;
    }

    /**
     * Determines if the associated student has read all the comments and notes.
     * 
     * @return true if the student is up to date, otherwise false. 
     */
    public boolean isReadStudent() {
        return readStudent;
    }
    
}
