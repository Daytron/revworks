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
    public static final String CURRENT_SEMESTER
            = "Current Semester";

    private CurrentUserSession() {
    }

    /**
     * Sets the name of the current user and stores it in the current session.
     * Using a {@code null} username will remove the username from the session.
     * It also stores a list for generated files mark for deletion later on and
     * the current semester through semester id.
     *
     * @param currentUser The current user
     * @throws IllegalStateException if the current session cannot be accessed.
     */
    public static void set(Principal currentUser, String semesterID) {
        try {
            VaadinSession.getCurrent().getLockInstance().lock();

            // For user
            VaadinSession.getCurrent().setAttribute(
                    CURRENT_USER_SESSION_ATTRIBUTE_KEY, currentUser);

            // For current semester
            VaadinSession.getCurrent().setAttribute(CURRENT_SEMESTER, semesterID);

            // For file trash bin
            CopyOnWriteArrayList<File> listOfFilesToBeDeleted = new CopyOnWriteArrayList<>();
            VaadinSession.getCurrent().setAttribute(TRASH_CAN_FOR_FILES_KEY, listOfFilesToBeDeleted);
        } finally {
            VaadinSession.getCurrent().getLockInstance().unlock();
        }
    }

    /**
     * Saves a file for deletion later on when the session closes.
     *
     * @param fileToBeDeletedLater A File object marked to be deleted
     */
    public static void saveFileToBin(File fileToBeDeletedLater) {
        getFileTrashBin().add(fileToBeDeletedLater);
    }

    /**
     * Returns the list of trash can file collection for deletion.
     *
     * @return The CopyOnWriteArrayList object
     */
    public static CopyOnWriteArrayList<File> getFileTrashBin() {
        CopyOnWriteArrayList<File> listOFiles = (CopyOnWriteArrayList) VaadinSession.getCurrent()
                .getAttribute(TRASH_CAN_FOR_FILES_KEY);
        return (listOFiles == null) ? null : listOFiles;
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
        return (currentUser == null) ? null : currentUser;
    }

    /**
     * Returns the current semester id based on the current date.
     *
     * @return The semester id
     */
    public static String getCurrentSemester() {
        String currentSemester = (String) VaadinSession.getCurrent()
                .getAttribute(CURRENT_SEMESTER);
        return (currentSemester == null) ? null : currentSemester;
    }

    /**
     * Signs out the current user attached to the current session. Closes the
     * session and invalidates any bind UI to the session afterwards. It is
     * important to close the session first to trigger destroy session listener
     * before invalidation. The invalidate() method forces the Vaadin to create
     * and redirect to a new session.
     */
    public static void signOut() {
        VaadinSession.getCurrent().close();
        VaadinSession.getCurrent().getSession().invalidate();
    }
}
