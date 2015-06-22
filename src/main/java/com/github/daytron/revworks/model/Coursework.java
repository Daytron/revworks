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
import java.util.ArrayList;
import java.util.List;

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
    private List<Note> listOfNotes;

    public Coursework(int id, String title, LocalDateTime dateSubmitted,
            File courseworkFile, String fileExtension, ClassTable classTable, 
            StudentUser studentUser) {
        this.id = id;
        this.title = title;
        this.dateSubmitted = dateSubmitted;
        this.fileExtension = fileExtension;
        this.courseworkFile = courseworkFile;
        this.classTable = classTable;
        this.studentUser = studentUser;
        
        // Initially set to empty when coursework module view is launched
        // Then set it with new set of Note objects only when user 
        // decides to open a particular coursework, this way it saves up 
        // resources.
        this.listOfNotes = new ArrayList<>();
    }

    public ClassTable getClassTable() {
        return classTable;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getDateSubmitted() {
        return dateSubmitted;
    }

    public StudentUser getStudentUser() {
        return studentUser;
    }

    public File getCourseworkFile() {
        return courseworkFile;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public List<Note> getListOfNotes() {
        return listOfNotes;
    }

    public void setListOfNotes(List<Note> listOfNotes) {
        this.listOfNotes = listOfNotes;
    }
    
}
