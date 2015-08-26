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
package com.github.daytron.revworks.presenter;

import com.github.daytron.revworks.event.AppEvent;
import com.github.daytron.revworks.event.AppEventBus;
import com.github.daytron.revworks.service.CurrentUserSession;
import com.github.daytron.revworks.view.main.CommentComponent;
import com.github.daytron.revworks.view.main.CourseworkView;
import com.vaadin.ui.Button;
import java.util.Map;

/**
 * Event handler for clicking note button.
 * 
 * @author Ryan Gilera
 */
public class NoteButtonListener implements Button.ClickListener {

    private final CourseworkView courseworkView;
    private final int associatedPage;
    
    /**
     * A class constructor that takes a CourseworkView object.
     * 
     * @param courseworkView the view for displaying coursework
     * @param associatedPage the associated page as integer
     */
    public NoteButtonListener(CourseworkView courseworkView,
            int associatedPage) {
        this.courseworkView = courseworkView;
        this.associatedPage = associatedPage;
    }

    /**
     * Verifies the button is registered in the CourseworkView. If no noteId is 
     * found, skip the remaining statements. If a comment 
     * component is already opened skips new comment component generation, 
     * otherwise generate a new comment component. This also clears previous 
     * clicked style applied to the buttons.
     * 
     * @param event button click event
     */
    @Override
    public void buttonClick(Button.ClickEvent event) {
        int noteId = 0;
        for (Map.Entry<Integer, Button> entry
                : courseworkView.getListOfNoteButtons().entrySet()) {
            if (entry.getValue() == event.getButton()) {
                noteId = entry.getKey();
                break;
            }
        }

        // if no button found skip
        if (noteId == 0) {
            return;
        }
        
        // Clear first previous clicked style to all note buttons
        synchronized (this) {
            for (Map.Entry<Integer, Button> entry
                    : courseworkView.getListOfNoteButtons().entrySet()) {
                entry.getValue().removeStyleName("note-clicked");
            }
        }

        // Skip new comment layout generation if there is a current 
        // stored commentLayout and show it if it is hidden
        if (noteId == courseworkView.getCommentLayout().getNoteId()) {
            courseworkView.getCommentLayout().setVisible(true);
            
            // Also return back the click style
            event.getButton().addStyleName("note-clicked");

            // Also point to the associated page if somehow not in the 
            // targeted page
            if (courseworkView.getCurrentPage() != associatedPage) {
                courseworkView.flipToPage(associatedPage);
            }
            
            return;
        }

        //Otherwise begin New comment component generation
        // But first shutdown the old componentLayout executor service
        // Also flip the page viewer to the correspoding note associated page
        courseworkView.getCommentLayout().shutdownCommentExecutor();

        courseworkView.getCommentLayout().setVisible(true);
        CommentComponent lcc
                = new CommentComponent(courseworkView.getCoursework(),
                        false, courseworkView.getCurrentPage(),
                        noteId, courseworkView);
        lcc.setVisible(true);

        courseworkView.getCoreContentLayout().replaceComponent(
                courseworkView.getCommentLayout(), lcc);
        courseworkView.setCommentLayout(lcc);

        // Store new comment component to this session
        CurrentUserSession.setCurrentCommentComponent(lcc);

        // Only flip page is the current page is not the associated page
        if (courseworkView.getCurrentPage() != associatedPage) {
            courseworkView.flipToPage(associatedPage);
        }

        // Trigger update note is_read fields via event bus
        AppEventBus.post(new AppEvent.UpdateNoteIsReadWhenClickEvent(noteId,
                event.getButton()));
    }

}
