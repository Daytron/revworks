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

import com.github.daytron.revworks.event.AppEvent;

/**
 * The base template for common SQL data insertion and update process for users.
 *
 * @author Ryan Gilera
 */
public interface DataInserter {
    
    /**
     * Inserts new comment into the database. If successfully inserted a new row, 
     * a new notification based on the previous event is created. This method is 
     * triggered by the AppEventBus. An error is displayed to the user when an 
     * SQLException occurs.
     * 
     * @param event a custom event object defined in {@link com.github.daytron.revworks.event.AppEvent} class
     */
    public void insertNewComment(final AppEvent.SubmitNewCommentEvent event);

    /**
     * Inserts new note associated comment into the database. If successfully 
     * inserted a new row, new notification based on the previous event is 
     * created. This method is triggered by the AppEventBus. An error is 
     * displayed to the user when an SQLException occurs.
     * 
     * @param event a custom event object defined in {@link com.github.daytron.revworks.event.AppEvent} class
     */
    public void insertNewNote(AppEvent.SubmitNewNoteEvent event);

    /**
     * Updates the note status when clicked depending on the user type. This 
     * method is triggered by the AppEventBus. An error is displayed to the 
     * user when an SQLException occurs.
     * 
     * @param event a custom event object defined in {@link com.github.daytron.revworks.event.AppEvent} class
     */
    public void updateNoteIsReadWhenClicked(
            AppEvent.UpdateNoteIsReadWhenClickEvent event);
    
}
