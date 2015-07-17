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
import com.github.daytron.revworks.authentication.UserAuthentication;
import com.github.daytron.revworks.event.AppEvent;
import com.github.daytron.revworks.event.AppEventBus;
import com.github.daytron.revworks.service.CurrentUserSession;
import com.github.daytron.revworks.service.LecturerDataProviderImpl;
import com.github.daytron.revworks.service.NotificationProvider;
import com.github.daytron.revworks.service.SQLConnectionManager;
import com.github.daytron.revworks.service.StudentDataProviderImpl;
import com.github.daytron.revworks.view.LoginScreen;
import com.github.daytron.revworks.view.dashboard.CommentComponent;
import com.github.daytron.revworks.view.dashboard.CourseworkView;
import com.github.daytron.revworks.view.dashboard.DashboardHeader;
import com.github.daytron.revworks.view.dashboard.DashboardScreen;
import com.google.common.eventbus.Subscribe;
import com.vaadin.annotations.Push;
import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.event.Action;
import com.vaadin.event.LayoutEvents;
import com.vaadin.server.Responsive;
import com.vaadin.server.SessionDestroyEvent;
import com.vaadin.server.SessionDestroyListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import java.io.File;
import java.security.Principal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.servlet.ServletException;

/**
 * The main class to launch the web application.
 */
@Push
@Theme("mytheme")
@SuppressWarnings("serial")
@Widgetset("com.github.daytron.revworks.MyAppWidgetset")
public class MainUI extends UI {

    private final AccessControl accessControl
            = new UserAccessControl();
    private final AppEventBus appEventBus
            = new AppEventBus();
    private final SQLConnectionManager connectionManager
            = new SQLConnectionManager();
    private final LecturerDataProviderImpl lecturerDataProvider
            = new LecturerDataProviderImpl();
    private final StudentDataProviderImpl studentDataProvider
            = new StudentDataProviderImpl();
    private final UserAuthentication userAuthentication
            = new UserAuthentication();
    private final NotificationProvider notificationsProvider
            = new NotificationProvider();
    private Window notificationsWindow = null;
    
    @Override
    protected void init(VaadinRequest vaadinRequest) {
        // Set max session timeout after 10 minutes
        VaadinSession.getCurrent().getSession().setMaxInactiveInterval(300);

        Responsive.makeResponsive(this);
        setLocale(vaadinRequest.getLocale());

        registerEventHandlers();

        getPage().setTitle("RevWorks");

        if (!accessControl.isUserSignedIn()) {
            setContent(new LoginScreen());
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

    public SQLConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public LecturerDataProviderImpl getLecturerDataProvider() {
        return lecturerDataProvider;
    }

    public StudentDataProviderImpl getStudentDataProvider() {
        return studentDataProvider;
    }

    public UserAuthentication getUserAuthentication() {
        return userAuthentication;
    }

    public NotificationProvider getNotificationsProvider() {
        return notificationsProvider;
    }

    public void showDashboardScreen() {
        // Register it first before building dashboardscreen
        // Before it runs a new thread for updating user notifications
        AppEventBus.register(notificationsProvider);

        DashboardScreen dashboardScreen = new DashboardScreen(MainUI.this);
        dashboardScreen.addLayoutClickListener(new LayoutEvents.LayoutClickListener() {

            @Override
            public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                
                AppEventBus.post(new AppEvent.CloseNotificationWindowEvent());
                
                
            }
        });
        AppEventBus.register(dashboardScreen);

        setContent(dashboardScreen);
        getNavigator().navigateTo(getNavigator().getState());
    }

    public static AppEventBus getAppEventbus() {
        return get().appEventBus;
    }

    private void registerEventHandlers() {
        AppEventBus.register(accessControl);
        AppEventBus.register(this);
    }

    public Window getNotificationsWindow() {
        return notificationsWindow;
    }

    public void setNotificationsWindow(Window notificationsWindow) {
        this.notificationsWindow = notificationsWindow;
    }

    @Subscribe
    public void closeNotificationWindows(AppEvent.CloseNotificationWindowEvent event) {
        
        if (notificationsWindow != null) {
            notificationsWindow.close();
            notificationsWindow = null;

            // Unpause the auto retrieve notification after closing window
            notificationsProvider.setPause(false);
        }
    }

    @WebServlet(urlPatterns = "/*", name = "MainUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MainUI.class, productionMode = false,
            closeIdleSessions = true, heartbeatInterval = 90)
    public static class MainUIServlet extends VaadinServlet implements
            SessionDestroyListener {

        private static final ConcurrentHashMap<String, VaadinSession> listOfUserSessions = new ConcurrentHashMap<>();

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
         * Overrides sessionDestroy method to implement removal of session when
         * session is destroyed.
         *
         * @param event The event object triggered when closing a session
         */
        @Override
        public void sessionDestroy(SessionDestroyEvent event) {
            if (event.getSession() == null) {
                System.out.println("Session is null!!!");
                printSessions("With session null");
            } else {
                final String KEY = CurrentUserSession.CURRENT_USER_SESSION_ATTRIBUTE_KEY;
                Principal user = (Principal) event.getSession()
                        .getAttribute(KEY);

                if (user == null) {
                    System.out.println("User in session is null!!");
                } else {

                    CopyOnWriteArrayList<File> listOfFilesToDelete
                            = (CopyOnWriteArrayList<File>) event.getSession()
                            .getAttribute(CurrentUserSession.TRASH_CAN_FOR_FILES_KEY);
                    // Explicitly cleanup all available or reserve connections 
                    // in the connection pool to free up MYSQL connection load
                    JDBCConnectionPool jdbccp = ((JDBCConnectionPool) event.getSession()
                            .getAttribute(CurrentUserSession.JDBC_CONNECTION_POOL_KEY));
                    if (jdbccp != null) {
                        jdbccp.destroy();
                    }

                    // Shutdown the remaining service threads
                    CourseworkView courseworkView = (CourseworkView) event.getSession().getAttribute(CurrentUserSession.CURRENT_COURSEWORK_VIEW);
                    CommentComponent commentComponent = (CommentComponent) event.getSession().getAttribute(CurrentUserSession.CURRENT_COMMENT_COMPONENT);
                    if (courseworkView != null) {
                        courseworkView.shutdownNoteExecutor();
                    }
                    if (commentComponent != null) {
                        commentComponent.shutdownCommentExecutor();
                    }

                    // Shutdown executor service for notification in
                    // the dashboard header
                    DashboardHeader dashboardHeader
                            = (DashboardHeader) event.getSession().getAttribute(
                                    CurrentUserSession.CURRENT_DASHBOARD_HEADER);
                    dashboardHeader.shutdownNotificationExecutor();

                    // Remove the closed session from list
                    listOfUserSessions.remove(user.getName());

                    // Clean any uploaded or temp files through deletion
                    for (File file : listOfFilesToDelete) {
                        file.delete();
                    }

                    printSessions("Session destroyed with session");
                }
            }

        }

    }
}
