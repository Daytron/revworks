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
import com.github.daytron.revworks.model.ClassTable;
import com.github.daytron.revworks.view.dashboard.CommentComponent;
import com.github.daytron.revworks.view.dashboard.CourseworkView;
import com.github.daytron.revworks.view.dashboard.DashboardHeader;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import java.io.File;
import java.security.Principal;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A static class for handling user in a session.
 *
 * @author Ryan Gilera
 */
public class CurrentUserSession {

    /**
     * The attribute key used to store the username in the session.
     */
    public static final String CURRENT_USER_SESSION_ATTRIBUTE_KEY
            = CurrentUserSession.class.getCanonicalName();
    /**
     * The attribute key used to store the List for files
     */
    public static final String TRASH_CAN_FOR_FILES_KEY
            = "Trashcan";
    /**
     * The attribute key used to store the current semester id
     */
    public static final String CURRENT_SEMESTER_KEY = "Current Semester";

    public static final String JDBC_CONNECTION_POOL_KEY = "JDBC POOL";
    /**
     * The attribute key used to store the current classes associated with the
     * current user
     */
    public static final String CURRENT_CLASSES = "Current Classes";

    /**
     * The attribute key used to store the current executor service for
     * comments.
     */
    public static final String CURRENT_COMMENT_COMPONENT = "CommentExecutor";

    /**
     * The attribute key used to store the current executor service for notes.
     */
    public static final String CURRENT_COURSEWORK_VIEW = "NoteExecutor";
    
    /**
     * The attribute key used for to store dashboard header view
     */
    public static final String CURRENT_DASHBOARD_HEADER = "NotificationExecutor";

    private CurrentUserSession() {
    }

    /**
     * Sets the name of the current user and stores it in the current session.
     * Using a {@code null} username will remove the username from the session.
     * It also stores a list for generated files mark for deletion later on, the
     * current semester through semester id and the SimpleJDBCConnectionPool
     * object used for this session.
     *
     * @param currentUser The current user
     * @param semesterID The current semester
     * @param listOfClassTables The list of classes associated with the user
     * @param jbdcConnectionPool The SimpleJDBCConnectionPool object
     * @throws IllegalStateException if the current session cannot be accessed.
     */
    public static void set(Principal currentUser, String semesterID,
            CopyOnWriteArrayList<ClassTable> listOfClassTables,
            JDBCConnectionPool jbdcConnectionPool) {
        try {
            VaadinSession vaadinSession = VaadinSession.getCurrent();

            vaadinSession.getLockInstance().lock();

            // For user
            vaadinSession.setAttribute(
                    CURRENT_USER_SESSION_ATTRIBUTE_KEY, currentUser);

            // For current semester
            vaadinSession.setAttribute(CURRENT_SEMESTER_KEY, semesterID);

            // For list of classes
            vaadinSession.setAttribute(CURRENT_CLASSES, listOfClassTables);

            // For file trash bin
            CopyOnWriteArrayList<File> listOfFilesToBeDeleted = new CopyOnWriteArrayList<>();
            vaadinSession.setAttribute(TRASH_CAN_FOR_FILES_KEY, listOfFilesToBeDeleted);

            // For JDBC connection pool
            vaadinSession.setAttribute(JDBC_CONNECTION_POOL_KEY, jbdcConnectionPool);

        } finally {
            VaadinSession.getCurrent().getLockInstance().unlock();
        }
    }

    /**
     * Saves a file for deletion later on when the session closes.
     *
     * @param fileToBeDeletedLater A File object marked to be deleted
     */
    public static void markForGarbageCollection(File fileToBeDeletedLater) {
        getFileTrashBin().add(fileToBeDeletedLater);
    }

    public static void setCurrentCommentComponent(CommentComponent commentComponent) {
        // shutdown previous if not null
        shutdownCommentExectorService();

        try {
            VaadinSession vaadinSession = VaadinSession.getCurrent();
            vaadinSession.getLockInstance().lock();
            vaadinSession.setAttribute(CURRENT_COMMENT_COMPONENT, commentComponent);

        } finally {
            VaadinSession.getCurrent().getLockInstance().unlock();
        }
    }

    public static CommentComponent getCurrentCommentComponent() {
        CommentComponent commentComponent = (CommentComponent) VaadinSession
                .getCurrent().getAttribute(CURRENT_COMMENT_COMPONENT);
        return (commentComponent == null) ? null : commentComponent;
    }

    public static void shutdownCommentExectorService() {
        CommentComponent oldCommentComponent = getCurrentCommentComponent();
        if (oldCommentComponent != null) {
            oldCommentComponent.shutdownCommentExecutor();
        }
    }

    public static void setCurrentCourseworkView(CourseworkView courseworkView) {
        // shutdown previous if not null
        shutdownCourseworkViewExecutorService();
        
        try {
            VaadinSession vaadinSession = VaadinSession.getCurrent();
            vaadinSession.getLockInstance().lock();
            vaadinSession.setAttribute(CURRENT_COURSEWORK_VIEW, courseworkView);

        } finally {
            VaadinSession.getCurrent().getLockInstance().unlock();
        }
    }

    public static CourseworkView getCurrentCourseworkView() {
        CourseworkView courseworkView = (CourseworkView) VaadinSession
                .getCurrent().getAttribute(CURRENT_COURSEWORK_VIEW);
        return courseworkView;
    }

    public static void shutdownCourseworkViewExecutorService() {
        CourseworkView oldCourseworkView = getCurrentCourseworkView();
        if (oldCourseworkView != null) {
            oldCourseworkView.shutdownNoteExecutor();
        }
    }
    
    public static void setDashboardHeader(DashboardHeader dashboardHeader) {        
        try {
            VaadinSession vaadinSession = VaadinSession.getCurrent();
            vaadinSession.getLockInstance().lock();
            vaadinSession.setAttribute(CURRENT_DASHBOARD_HEADER, dashboardHeader);

        } finally {
            VaadinSession.getCurrent().getLockInstance().unlock();
        }
    }
    
    public static DashboardHeader getDashboardHeader() {
        DashboardHeader dashboardHeader = (DashboardHeader) VaadinSession
                .getCurrent().getAttribute(CURRENT_DASHBOARD_HEADER);
        return dashboardHeader;
    }
    
    public static void shutdownDashboardHeaderExecutorService() {
        DashboardHeader dashboardHeader = getDashboardHeader();
        if (dashboardHeader != null) {
            dashboardHeader.shutdownNotificationExecutor();
        }
    }

    /**
     * Returns the list of trash can file collection for deletion.
     *
     * @return The CopyOnWriteArrayList object
     */
    public static CopyOnWriteArrayList<File> getFileTrashBin() {
        CopyOnWriteArrayList<File> listOFiles = (CopyOnWriteArrayList) VaadinSession
                .getCurrent().getAttribute(TRASH_CAN_FOR_FILES_KEY);
        return listOFiles;
    }

    /**
     * Returns the name of the current user stored in the current session, or
     * null if no user name is stored.
     *
     * @return The user object
     * @throws IllegalStateException if the current session cannot be accessed.
     */
    public static Principal getPrincipal() {
        Principal currentUser = (Principal) VaadinSession.getCurrent()
                .getAttribute(CURRENT_USER_SESSION_ATTRIBUTE_KEY);
        return currentUser;
    }

    /**
     * Returns the current semester id based on the current date.
     *
     * @return The semester id
     */
    public static String getCurrentSemester() {
        String currentSemester = (String) VaadinSession.getCurrent()
                .getAttribute(CURRENT_SEMESTER_KEY);
        return currentSemester;
    }

    /**
     * Returns list of classes associated with the current user. For admin user
     * the list is empty by default.
     *
     * @return CopyOnWriteArrayList object
     */
    public static CopyOnWriteArrayList<ClassTable> getCurrentClassTables() {
        CopyOnWriteArrayList<ClassTable> listOfClasses
                = (CopyOnWriteArrayList<ClassTable>) VaadinSession.getCurrent()
                .getAttribute(CURRENT_CLASSES);
        return listOfClasses;
    }

    /**
     * Returns the simple jdbc connection pool object that is used throughout
     * the session.
     *
     * @return SimpleJDBCConnectionPool object
     */
    public static JDBCConnectionPool getJDBCConnectionPool() {
        SimpleJDBCConnectionPool simpleJDBCConnectionPool
                = (SimpleJDBCConnectionPool) VaadinSession.getCurrent()
                .getAttribute(JDBC_CONNECTION_POOL_KEY);
        return simpleJDBCConnectionPool;
    }

    /**
     * Signs out the current user attached to the current session. Closes the
     * session and invalidates any bind UI to the session afterwards. It is
     * important to close the session first to trigger destroy session listener
     * before invalidation. The invalidate() method forces the Vaadin to create
     * and redirect to a new session.
     */
    public static void signOut() {
        MainUI.get().close();
        VaadinSession.getCurrent().close();
        VaadinService.getCurrentRequest().getWrappedSession().invalidate();
        //VaadinSession.getCurrent().getSession().invalidate();
    }
}
