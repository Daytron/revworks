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
package com.github.daytron.revworks;

import com.github.daytron.revworks.authentication.AccessControl;
import com.github.daytron.revworks.authentication.UserAccessControl;
import com.github.daytron.revworks.service.CurrentUserSession;
import com.github.daytron.revworks.ui.LoginScreen;
import com.github.daytron.revworks.ui.DashboardScreen;
import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.Responsive;
import com.vaadin.server.SessionDestroyEvent;
import com.vaadin.server.SessionDestroyListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;
import java.security.Principal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletException;

/**
 *
 */
@Theme("mytheme")
@Widgetset("com.github.daytron.revworks.MyAppWidgetset")
public class MainUI extends UI {

    private static final long serialVersionUID = 1L;
    private final AccessControl accessControl = new UserAccessControl();

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        Responsive.makeResponsive(this);
        setLocale(vaadinRequest.getLocale());
        getPage().setTitle("RevWorks");

        if (!accessControl.isUserSignedIn()) {
            setContent(new LoginScreen(accessControl));
        } else {
            showDashboardScreen();
        }
    }

    public static MainUI get() {
        return (MainUI) UI.getCurrent();
    }

    public AccessControl getAccessControl() {
        return accessControl;
    }

    public void showDashboardScreen() {
        setContent(new DashboardScreen());
    }

    @WebServlet(urlPatterns = "/*", name = "MainUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MainUI.class, productionMode = false)
    public static class MainUIServlet extends VaadinServlet implements
            SessionDestroyListener {

        private static final ConcurrentHashMap<String, VaadinSession> 
                listOfUserSessions = new ConcurrentHashMap<>();

        /**
         * Saves the new login session to the collection member,
         * ConcurrentHashmap. If user is previously login from other session, it
         * automatically close that previous session, to prevent multi-login
         * sessions for a single user.
         *
         * @param userID The id of the user
         * @param currentSession The session that is about to be recorded.
         */
        public static void saveUserSessionInfo(String userID,
                VaadinSession currentSession) {
            VaadinSession previousSession = listOfUserSessions.get(userID);
            if (previousSession != null) {

                // Close the previous session
                previousSession.close();
                // Then invalidate the wrapped session to unbind remaining UI
                previousSession.getSession().invalidate();

                printSessions("After session.close() is invoked.");
            }
            // Store new session
            listOfUserSessions.put(userID, currentSession);
            printSessions("After new session is invoke.");
        }

        /**
         * Returns the list of sessions for different users.
         *
         * @return {@link Map} object
         */
        public static Map<String, VaadinSession> getListOfUserSessions() {
            // Access the cache
            return listOfUserSessions;
        }

        /**
         * Simply prints the current sessions for logging purposes.
         *
         * @param customeMsg A custom message is required to notify where
         * printing method is triggered.
         */
        public static void printSessions(String customeMsg) {
            System.out.println("VAADIN SESSIONS: " + customeMsg);
            for (String id : listOfUserSessions.keySet()) {
                System.out.println("Session id: " + id);
            }

            if (listOfUserSessions.isEmpty()) {
                System.out.println("WARNING EMPTY LIST OF SESSIONS!!!!");
            }
        }

        /**
         * Overrides servletInitialized method from {@link VaadinServlet} to
         * attached a custom {@link SessionDestroyListener}, which is the same
         * object of this class.
         *
         * @throws ServletException
         */
        @Override
        protected void servletInitialized() throws ServletException {
            super.servletInitialized();
            VaadinService.getCurrent().addSessionDestroyListener(this);
        }

        /**
         * Overrides sessionDestroy method to implement removal of session 
         * when session is destroyed.
         * 
         * @param event The event object triggered when closing a session 
         */
        @Override
        public void sessionDestroy(SessionDestroyEvent event) {
            if (event.getSession() == null) {
                System.out.println("Session is null!!!");
                printSessions("With session null");
            } else {
                final String KEY = CurrentUserSession.class.getCanonicalName();
                Principal user = (Principal) event.getSession()
                        .getAttribute(KEY);
                if (user == null) {
                    System.out.println("User in session is null!!");
                } else {
                    listOfUserSessions.remove(user.getName());
                    printSessions("Session destroyed with session");
                }
            }

        }

    }
}
