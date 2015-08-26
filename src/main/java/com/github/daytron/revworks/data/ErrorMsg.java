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
package com.github.daytron.revworks.data;

/**
 * Collection of error messages.
 *
 * @author Ryan Gilera
 */
public enum ErrorMsg {

    NO_CURRENT_SEMESTER("There is no ongoing semester."),
    CONSULT_YOUR_ADMIN("Please consult your administrator for more information."),
    INVALID_INPUT_CAPTION("Invalid input!"),
    SIGNIN_FAILED_CAPTION("Sign-In failed!"),
    NO_USER_SIGNIN("No login user found. The session has been reset. "),
    DATA_FETCH_ERROR("Failed retrieving user data."),
    DATA_SEND_ERROR("Failed to send data."),
    DATA_UPDATE_ERROR("Failed to update data."),
    
    // For coursework upload
    STUDENT_FAILED_UPLOAD_COURSEWORK("Failed upload."),
    STUDENT_REACHED_MAX_FILE_SIZE("Your file must not exceed 10 MB."),
    STUDENT_WRONG_FILE_TYPE_UPLOAD("Invalid filetype. Only accepts pdf."),
    STUDENT_NO_FILE_UPLOAD("No file selected."),
    EMPTY_TITLE_FIELD("Empty title field."),
    FILE_IS_NOT_YET_UPLOADED("Your file is not yet uploaded."),
    TERMS_NOT_ACCEPTED("Please accept the terms and condition by ticking the "
            + "tick box."),
    
    // For lecturer coursework module view
    LECTURER_NO_CLASS_FOUND("No class found.");

    private final String text;

    private ErrorMsg(String text) {
        this.text = text;
    }

    /**
     * Access the String value.
     * 
     * @return text stored in an item. 
     */
    public String getText() {
        return text;
    }

}
