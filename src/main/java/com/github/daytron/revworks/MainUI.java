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
import com.github.daytron.revworks.model.User;
import com.github.daytron.revworks.service.CurrentUserSession;
import com.github.daytron.revworks.service.LecturerDataProviderImpl;
import com.github.daytron.revworks.service.NotificationProvider;
import com.github.daytron.revworks.service.SQLConnectionManager;
import com.github.daytron.revworks.service.StudentDataProviderImpl;
import com.github.daytron.revworks.service.admin.AdminDataInserter;
import com.github.daytron.revworks.view.LoginScreen;
import com.github.daytron.revworks.view.admin.Dashboard;
import com.github.daytron.revworks.view.main.CommentComponent;
import com.github.daytron.revworks.view.main.CourseworkView;
import com.github.daytron.revworks.view.main.HeaderComponent;
import com.github.daytron.revworks.view.main.MainView;
import com.google.common.eventbus.Subscribe;
import com.vaadin.annotations.Push;
import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
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

    // Admin stuff
    private final AdminDataInserter adminDataInserter
            = new AdminDataInserter();

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        // Set max session timeout after 20 minutes
        VaadinSession.getCurrent().getSession().setMaxInactiveInterval(1200);

        Responsive.makeResponsive(this);
        setLocale(vaadinRequest.getLocale());

        registerEventHandlers();

        getPage().setTitle("RevWorks");

        if (!accessControl.isUserSignedIn()) {
            setContent(new LoginScreen());
        } else {
            showMainScreen();
        }
    }

    /**
     * {@link  MainUI} is the entry point class for the whole application. 
     * It holds many initialised variables that requires this object to 
     * access those variables.
     * 
     * @return The one and only MainUI object exist throughout the application 
     * cycle.
     */
    public static MainUI get() {
        return (MainUI) UI.getCurrent();
    }

    /**
     * Any polymorphic derived object of {@link  AccessControl}.
     * 
     * @return AccessControl polymorphic object
     */
    public AccessControl getAccessControl() {
        return accessControl;
    }

    /**
     * {@link SQLConnectionManager} is responsible for connecting to 
     * MySQL database server.
     * 
     * @return SQLConnectionManager is only instantiated once. 
     */
    public SQLConnectionManager getConnectionManager() {
        return connectionManager;
    }

    /**
     * {@link LecturerDataProviderImpl} is responsible for providing non-static 
     * lecturer data from the database.
     * 
     * @return LecturerDataProviderImpl object
     */
    public LecturerDataProviderImpl getLecturerDataProvider() {
        return lecturerDataProvider;
    }

    /**
     * {@link StudentDataProviderImpl} is responsible for providing non-static 
     * student data from the database.
     * 
     * @return StudentDataProviderImpl object
     */
    public StudentDataProviderImpl getStudentDataProvider() {
        return studentDataProvider;
    }

    /**
     * {@link UserAuthentication} is responsible for login authentication processes.
     * 
     * @return UserAuthentication object 
     */
    public UserAuthentication getUserAuthentication() {
        return userAuthentication;
    }

    /**
     * {@link NotificationProvider} is responsible for retrieving notifications 
     * from the database.
     * 
     * @return NotificationProvider object 
     */
    public NotificationProvider getNotificationsProvider() {
        return notificationsProvider;
    }

    /**
     * Reroutes a student or lecturer user to the main view after successfully 
     * logging in.
     */
    public void showMainScreen() {
        // Register notificationsProvider first before building mainscreen and
        // before it spawn a new thread for updating user notifications
        AppEventBus.register(notificationsProvider);

        MainView mainScreen = new MainView(MainUI.this);
        mainScreen.addLayoutClickListener(new LayoutEvents.LayoutClickListener() {

            @Override
            public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                AppEventBus.post(new AppEvent.CloseNotificationWindowEvent());
            }
        });

        AppEventBus.register(mainScreen);

        setContent(mainScreen);
        getNavigator().navigateTo(getNavigator().getState());
    }

    /**
     * Reroutes an admin user to the main view after successfully 
     * logging in.
     */
    public void showAdminDashboard() {
        Dashboard dashboard = new Dashboard(MainUI.this);

        setContent(dashboard);
        getNavigator().navigateTo(getNavigator().getState());
    }

    /**
     * {@link AppEventBus} is responsible for managing application events.
     * 
     * @return AppEventBus object 
     */
    public static AppEventBus getAppEventbus() {
        return get().appEventBus;
    }

    /**
     * Registers the first set of event handlers. These event handlers are 
     * registered before user logs in.
     */
    private void registerEventHandlers() {
        AppEventBus.register(accessControl);
        AppEventBus.register(this);
    }

    /**
     * This is an external window used for displaying user notifications. Acts 
     * as a popup window.
     * 
     * @return Window object
     */
    public Window getNotificationsWindow() {
        return notificationsWindow;
    }

    /**
     * Sets a {@link Window} object.
     * 
     * @param notificationsWindow Window object 
     */
    public void setNotificationsWindow(Window notificationsWindow) {
        this.notificationsWindow = notificationsWindow;
    }

    /**
     * Process the CloseNotificationWindowEvent event triggered by AppEventBus. 
     * Closes any opened notification window and pauses the timely thread for 
     * retrieving user notifications.
     * 
     * @param event CloseNotificationWindowEvent object
     */
    @Subscribe
    public void closeNotificationWindows(AppEvent.CloseNotificationWindowEvent event) {
        if (notificationsWindow != null) {
            notificationsWindow.close();
            notificationsWindow = null;

            // Unpause the auto retrieve notification after closing window
            notificationsProvider.setPause(false);
        }
    }

    /**
     * {@link AdminDataInserter} is responsible for updating or inserting 
     * admin data in the database.
     * 
     * @return AdminDataInserter object 
     */
    public AdminDataInserter getAdminDataInserter() {
        return adminDataInserter;
    }

    // 6 minutes heartbeat interval
    @WebServlet(urlPatterns = "/*", name = "MainUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MainUI.class, productionMode = false,
            closeIdleSessions = true, heartbeatInterval = 360)
    public static class MainUIServlet extends VaadinServlet implements
            SessionDestroyListener {

        private static final ConcurrentHashMap<String, VaadinSession> 
                listOfUserSessions = new ConcurrentHashMap<>();

        /**
         * Saves the new session to the collection member,
         * ConcurrentHashmap. If the user is previously login in other session,
         * it automatically closes the previous session to prevent multi-login
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

                System.out.println("### Closing previous session with the same user: "
                        + userID + "....");
            }

            // Store new session
            listOfUserSessions.put(userID, currentSession);
            System.out.println("### Added new session: user: " + userID
                    + " - After new session is invoke.");
            printSessions("after a user: " + userID + " login");
        }

        /**
         * Returns the list of sessions for various users.
         *
         * @return {@link Map} object
         */
        public static Map<String, VaadinSession> getListOfUserSessions() {
            // Access the cache
            return listOfUserSessions;
        }

        /**
         * Simply prints the sessions for logging purposes.
         *
         * @param customeMsg A custom message is required to notify where
         * printing method is triggered.
         */
        public static void printSessions(String customeMsg) {
            System.out.println("####################");
            System.out.println("Current active sessions " + customeMsg + ":");

            for (String id : listOfUserSessions.keySet()) {
                System.out.println("Session id: " + id);
            }

            if (listOfUserSessions.isEmpty()) {
                System.out.println("No active session.");
            }

            System.out.println("####################");
        }

        /**
         * Overrides servletInitialized method from {@link VaadinServlet} to
         * attached a custom {@link SessionDestroyListener}, which is the same
         * object of this class.
         *
         * @throws ServletException for failing to initialise the servlet
         */
        @Override
        protected void servletInitialized() throws ServletException {
            super.servletInitialized();
            VaadinService.getCurrent().addSessionDestroyListener(this);
        }

        /**
         * Overrides sessionDestroy method to implement the removal of session 
         * when session is destroyed.
         *
         * @param event The event object triggered when closing a session
         */
        @Override
        public void sessionDestroy(SessionDestroyEvent event) {
            if (event.getSession() != null) {
                final String KEY = CurrentUserSession.CURRENT_USER_SESSION_ATTRIBUTE_KEY;
                Principal user = (Principal) event.getSession()
                        .getAttribute(KEY);

                if (user != null) {
                    if (!((User) user).isAdminUser()) {

                        CopyOnWriteArrayList<File> listOfFilesToDelete
                                = (CopyOnWriteArrayList<File>) event.getSession()
                                .getAttribute(CurrentUserSession.TRASH_CAN_FOR_FILES_KEY);

                        // Shutdown the remaining service threads
                        CourseworkView courseworkView
                                = (CourseworkView) event.getSession()
                                .getAttribute(
                                        CurrentUserSession.CURRENT_COURSEWORK_VIEW);
                        CommentComponent commentComponent
                                = (CommentComponent) event.getSession()
                                .getAttribute(
                                        CurrentUserSession.CURRENT_COMMENT_COMPONENT);

                        if (courseworkView != null) {
                            courseworkView.shutdownNoteExecutor();
                        }

                        if (commentComponent != null) {
                            commentComponent.shutdownCommentExecutor();
                        }

                        // Shutdown executor service for notification in
                        // the dashboard header
                        HeaderComponent dashboardHeader
                                = (HeaderComponent) event.getSession().getAttribute(
                                        CurrentUserSession.CURRENT_DASHBOARD_HEADER);
                        dashboardHeader.shutdownNotificationExecutor();

                        // Clean any uploaded or temp files through deletion
                        for (File file : listOfFilesToDelete) {
                            file.delete();
                        }
                    }

                    // Explicitly cleanup all available or reserve connections 
                    // in the connection pool to free up MYSQL connection load
                    JDBCConnectionPool jdbccp = ((JDBCConnectionPool) event.getSession()
                            .getAttribute(CurrentUserSession.JDBC_CONNECTION_POOL_KEY));

                    if (jdbccp != null) {
                        jdbccp.destroy();
                    }

                    System.out.println("Closing session for user: " + user.getName());

                    // Remove the closed session from list
                    listOfUserSessions.remove(user.getName());

                    printSessions("after user: " + user.getName() + " logout:");
                }
            }

        }

    }
}
