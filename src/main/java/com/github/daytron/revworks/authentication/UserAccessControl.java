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

import com.github.daytron.revworks.service.CurrentUserSession;
import com.github.daytron.revworks.service.NoCurrentUserException;
import com.github.daytron.revworks.service.WrongCurrentUserTypeException;
import com.github.daytron.revworks.data.ExceptionMsg;
import com.github.daytron.revworks.data.UserType;
import com.vaadin.server.Page;
import java.security.Principal;

/**
 * Default implementation class of {@link AccessControl} interface. This
 * implementation accepts {@link  UserType} object and any strings as username
 * and a password, and manage user access control and information.
 *
 * @author Ryan Gilera
 */
public class UserAccessControl implements AccessControl {

    private static final long serialVersionUID = 1L;

    /**
     * Expose authentication method call of {@link UserAuthentication} to pass
     * {@link  UserType} object, username and password arguments. As a result,
     * it accepts and returns a {@link User} as a Principal object.
     *
     * @param userType A {@link UserType} object
     * @param userfield The username either and email for the lecturers and
     * student id for students
     * @param password The password as String
     * @throws AuthenticationException
     */
    @Override
    public void signIn(UserType userType, String userfield,
            String password) throws AuthenticationException {
        Principal user = UserAuthentication.getInstance()
                .authenticate(userType, userfield, password);

        CurrentUserSession.set(user);
    }

    @Override
    public boolean isUserSignedIn() {
        return CurrentUserSession.get() != null;
    }

    @Override
    public boolean isUserAStudent() throws NoCurrentUserException {

        if (!isUserSignedIn()) {
            return ((User) CurrentUserSession.get()).isStudentUser();
        } else {
            throw new NoCurrentUserException(
                    ExceptionMsg.NO_CURRENT_USER_EXCEPTION.getMsg());
        }
    }

    @Override
    public boolean isUserALecturer() throws NoCurrentUserException {
        if (!isUserSignedIn()) {
            return ((User) CurrentUserSession.get()).isLecturerUser();
        } else {
            throw new NoCurrentUserException(
                    ExceptionMsg.NO_CURRENT_USER_EXCEPTION.getMsg());
        }
    }

    @Override
    public String getPrincipalName() {
        return CurrentUserSession.get().getName();
    }

    @Override
    public String getFirstName() {
        return ((User) CurrentUserSession.get()).getFirstName();
    }

    @Override
    public String getLastName() {
        return ((User) CurrentUserSession.get()).getLastName();
    }

    @Override
    public String getFullName() {
        return ((User) CurrentUserSession.get()).getFullName();
    }

    @Override
    public String getLecturerEmail() throws WrongCurrentUserTypeException {
        if (((User) CurrentUserSession.get()).isLecturerUser()) {
            return ((LecturerUser) (CurrentUserSession.get())).getEmail();
        } else {
            throw new WrongCurrentUserTypeException(
                    ExceptionMsg.WRONG_CURRENT_USER_TYPE_EXCEPTION.getMsg());
        }
    }

    @Override
    public String getStudentID() throws WrongCurrentUserTypeException {
        if (((User) CurrentUserSession.get()).isLecturerUser()) {
            return ((StudentUser) (CurrentUserSession.get())).getStudentID();
        } else {
            throw new WrongCurrentUserTypeException(
                    ExceptionMsg.WRONG_CURRENT_USER_TYPE_EXCEPTION.getMsg());
        }
    }

    @Override
    public void signOut() {
        CurrentUserSession.signOut();

        // Reloads the page which will point back to login page
        Page.getCurrent().reload();
    }

    @Override
    public String getUserTypeString() {
        return ((User) CurrentUserSession.get()).getUserType().getText();
    }

    @Override
    public UserType getUserType() {
        return ((User) CurrentUserSession.get()).getUserType();
    }

}
