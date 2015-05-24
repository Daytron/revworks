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
package com.github.daytron.revworks.service;

import com.github.daytron.revworks.MainUI;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;
import java.security.Principal;

/**
 *
 * @author Ryan Gilera
 */
public class CurrentUserSession {
    /**
     * The attribute key used to store the username in the session.
     */
    public static final String CURRENT_USER_SESSION_ATTRIBUTE_KEY = 
            CurrentUserSession.class.getCanonicalName();
    
    private CurrentUserSession(){}
    
    /**
     * Sets the name of the current user and stores it in the current session.
     * Using a {@code null} username will remove the username from the session.
     * 
     * @param currentUser The current user
     * @throws IllegalStateException
     *             if the current session cannot be accessed.
     */
    public static void set(Principal currentUser) {
        VaadinSession.getCurrent().setAttribute(
                CURRENT_USER_SESSION_ATTRIBUTE_KEY, currentUser);
    }
    
    /**
     * Returns the name of the current user stored in the current session, or an
     * empty string if no user name is stored.
     * 
     * @return The user object
     * @throws IllegalStateException
     *             if the current session cannot be accessed.
     */
    public static Principal get() {
        Principal currentUser = (Principal) VaadinSession.getCurrent()
                .getAttribute(CURRENT_USER_SESSION_ATTRIBUTE_KEY);
        return (currentUser == null) ? null : currentUser;
    }
    
    
    
    public static void signOut() {
//        MainUI.MainUIServlet.removeSessonUponLogout(
//                get().getName());
        
        VaadinSession.getCurrent().close();
        VaadinSession.getCurrent().getSession().invalidate();
    }
}
