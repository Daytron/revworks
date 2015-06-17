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

import com.github.daytron.revworks.ui.dashboard.lecturer.LecturerCommentComponent;
import com.github.daytron.revworks.ui.dashboard.lecturer.LecturerCourseworkView;
import com.vaadin.ui.Button;
import java.util.Map;

/**
 *
 * @author Ryan Gilera
 */
public class ReviewButtonListener implements Button.ClickListener {

    private final LecturerCourseworkView courseworkView;
    private final int associatedPage;

    public ReviewButtonListener(LecturerCourseworkView courseworkView, 
            int associatedPage) {
        this.courseworkView = courseworkView;
        this.associatedPage = associatedPage;
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        int reviewId = 0;
        for (Map.Entry<Integer, Button> entry
                : courseworkView.getListOfReviewButtons().entrySet()) {
            if (entry.getValue() == event.getButton()) {
                reviewId = entry.getKey();
                break;
            }
        }

        // Do nothing if review comment is already shown or reviewId is zero
        if (reviewId == 0) {
            return;
        }

                // Skip new comment layout generation if it is the current 
        // stored commentLayout and show it if hidden
        if (reviewId == courseworkView.getCommentLayout().getReviewId()) {
            courseworkView.getCommentLayout().setVisible(true);
            return;
        }

                //Otherwise begin New comment component generation
        // But first shutdown the old componentLayout executor service
        courseworkView.getCommentLayout().shutdownScheduler();

        courseworkView.getCommentLayout().setVisible(true);
        LecturerCommentComponent lcc
                = new LecturerCommentComponent(courseworkView.getCoursework(),
                        false, courseworkView.getCurrentPage(),
                        reviewId, courseworkView);

        courseworkView.getCoreContentLayout().replaceComponent(
                courseworkView.getCommentLayout(), lcc);
        courseworkView.setCommentLayout(lcc);
        
        courseworkView.flipToPage(associatedPage);
        
    }

}
