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

import com.github.daytron.revworks.exception.WrongCurrentUserTypeException;
import com.github.daytron.revworks.data.UserType;
import com.github.daytron.revworks.event.AppEvent.*;

/**
 * The base interface for authentication, authorisation checks and user access
 * control.
 *
 * @author Ryan Gilera
 */
public interface AccessControl {

    /**
     * Expose authentication method call of {@link UserAuthentication} to pass
     * {@link  UserType} object, username and password arguments. As a result,
     * it accepts and returns a {@link com.github.daytron.revworks.model.User} as a Principal object.
     * 
     * @param event a custom event object defined in {@link com.github.daytron.revworks.event.AppEvent} class
     */
    public void signIn(UserLoginRequestEvent event);

    /**
     * Logs out the user and reroutes user back to the login page.
     * 
     * @param event a custom event object defined in {@link com.github.daytron.revworks.event.AppEvent} class
     */
    public void signOut(UserLogoutRequestEvent event);

    /**
     * Validates admin login form fields and calls for authentication.
     * 
     * @param event a custom event object defined in {@link com.github.daytron.revworks.event.AppEvent} class
     */
    public void signInAdmin(AdminLoginRequestEvent event);

    /**
     * Launches a new modal popup window for admin login when user click the
     * webmaster link located at the login screen footer section.
     * 
     * @param event a custom event object defined in {@link com.github.daytron.revworks.event.AppEvent} class
     */
    public void webmasterLinkOnClick(WebmasterLinkClickEvent event);

    /**
     * Applies the necessary setting for each user type. The textfield is shared
     * by student ID and lecturer's email address.
     * 
     * @param event a custom event object defined in {@link com.github.daytron.revworks.event.AppEvent} class
     */
    public void optionGroupOnChangeValue(OptionChangeValueEvent event);

    /**
     * Checks whether there is a user currently signed in.
     * 
     * @return {@code true} if a user is signed in, otherwise {@code false}
     */
    public boolean isUserSignedIn();

    /**
     * Checks whether the user is a student or not.
     * 
     * @return {@code true} if a user is a student, otherwise {@code false}
     */
    public boolean isUserAStudent();

    /**
     * Checks whether the user is a lecturer or not.
     * 
     * @return {@code true} if a user is a lecturer, otherwise {@code false}
     */
    public boolean isUserALecturer();

    /**
     * Checks whether the user is an admin or not.
     * 
     * @return {@code true} if a user is an admin, otherwise {@code false}
     */
    public boolean isUserAdmin();
    
    /**
     * Access the user id.
     * 
     * @return user id as an integer
     */
    public int getUserId();

    /**
     * Access the unique identifier of a user. Principal class default
     * implementation of this method refers to the username of a user to
     * identify them uniquely but since there are two different usernames from
     * two different user types, it is better to treat it as User ID as they are
     * identified in the database.
     * 
     * @return a user unique id as String
     */
    public String getPrincipalName();

    /**
     * Access user's first name.
     * 
     * @return user's first name as String 
     */
    public String getFirstName();

    /**
     * Access user's last name.
     * 
     * @return user's last name as String 
     */
    public String getLastName();

    /**
     * Access user's full name name, separated by a space.
     * 
     * @return user's full name name as String 
     */
    public String getFullName();

    /**
     * Access lecturer's email.
     * 
     * @return lecturer's email as String
     * @throws WrongCurrentUserTypeException if user is not a lecturer 
     */
    public String getLecturerEmail() throws WrongCurrentUserTypeException;

    /**
     * Access student's id.
     * 
     * @return student's id as String
     * @throws WrongCurrentUserTypeException if user is not a student 
     */
    public String getStudentID() throws WrongCurrentUserTypeException;

    /**
     * Access the user's type.
     * 
     * @return user's type as String 
     */
    public String getUserTypeString();

    /**
     * Access the user's type.
     * 
     * @return user's type as {@link UserType} 
     */
    public UserType getUserType();

}
