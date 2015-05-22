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
import com.github.daytron.revworks.ui.constants.UserType;
import java.security.Principal;

/**
 *
 * @author Ryan Gilera
 */
public class UserAccessControl implements AccessControl {
    private static final long serialVersionUID = 1L;
    
    @Override
    public Principal authenticate(UserType userType, String userfield, 
            String password) throws AuthenticationException {
        Principal user = UserAuthentication.getInstance()
                .authenticate(userType, userfield, password);
        
        return user;
    }

    @Override
    public boolean isUserSignedIn() {
        return CurrentUserSession.get() != null;
    }

    @Override
    public boolean isUserAStudent(UserType role) {
        return role == UserType.STUDENT;
    }
    
    @Override
    public boolean isUserALecturer(UserType role) {
        return role == UserType.LECTURER;
    }

    @Override
    public String getPrincipalName() {
        return CurrentUserSession.get().getName();
    }

    
    
}
