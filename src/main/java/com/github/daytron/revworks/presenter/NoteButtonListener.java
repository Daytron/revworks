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
import com.github.daytron.revworks.view.dashboard.CommentComponent;
import com.github.daytron.revworks.view.dashboard.CourseworkView;
import com.vaadin.ui.Button;
import java.util.Map;

/**
 *
 * @author Ryan Gilera
 */
public class NoteButtonListener implements Button.ClickListener {

    private final CourseworkView courseworkView;
    private final int associatedPage;

    public NoteButtonListener(CourseworkView courseworkView, 
            int associatedPage) {
        this.courseworkView = courseworkView;
        this.associatedPage = associatedPage;
    }

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

        // Skip new comment layout generation if it is the current 
        // stored commentLayout and show it if hidden
        if (noteId == courseworkView.getCommentLayout().getNoteId()) {
            courseworkView.getCommentLayout().setVisible(true);
          
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

        courseworkView.getCoreContentLayout().replaceComponent(
                courseworkView.getCommentLayout(), lcc);
        courseworkView.setCommentLayout(lcc);
        
        // Store new comment component to this session
        CurrentUserSession.setCurrentCommentComponent(lcc);
        
        courseworkView.flipToPage(associatedPage);
        
        // Clear previous clicked style to note buttons
        for (Map.Entry<Integer, Button> entry
                : courseworkView.getListOfNoteButtons().entrySet()) {
            entry.getValue().removeStyleName("note-clicked");
        }
        // Trigger update note is_read fields via event bus
        AppEventBus.post(new AppEvent.UpdateNoteIsReadWhenClickEvent(noteId,
            event.getButton()));
    }

}
