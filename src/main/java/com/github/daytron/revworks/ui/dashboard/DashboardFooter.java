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
package com.github.daytron.revworks.ui.dashboard;

import com.github.daytron.revworks.data.ExternalLink;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

/**
 * Ui widget component for dashboard footer.
 *
 * @author Ryan Gilera
 */
@SuppressWarnings("serial")
public class DashboardFooter extends VerticalLayout {

    public DashboardFooter() {
        setWidth("100%");
        setSpacing(true);
        setStyleName("dashboard-footer");

        addComponent(createPrimaryLinks());
        addComponent(createBottomRow());
    }

    private HorizontalLayout createPrimaryLinks() {
        final HorizontalLayout linkLayout = new HorizontalLayout();
        linkLayout.setWidth("100%");
        linkLayout.setSpacing(true);

        Label leftSpaceLabel = new Label(" ");
        leftSpaceLabel.setSizeFull();
        linkLayout.addComponent(leftSpaceLabel);

        Link gsmLondonMainLinkButton = new Link(ExternalLink.GSM_LONDON.getName(),
                new ExternalResource(ExternalLink.GSM_LONDON.getLink()));
        gsmLondonMainLinkButton.setTargetName("_blank");
        gsmLondonMainLinkButton.setSizeUndefined();

        Link studentPortalLinkButton = new Link(ExternalLink.STUDENT_PORTAL.getName(),
                new ExternalResource(ExternalLink.STUDENT_PORTAL.getLink()));
        studentPortalLinkButton.setTargetName("_blank");
        studentPortalLinkButton.setSizeUndefined();

        Link lecturerPortalLinkButton = new Link(ExternalLink.LECTURER_PORTAL.getName(),
                new ExternalResource(ExternalLink.LECTURER_PORTAL.getLink()));
        lecturerPortalLinkButton.setTargetName("_blank");
        lecturerPortalLinkButton.setSizeUndefined();

        Link gsmLearnLinkButton = new Link(ExternalLink.GSM_LEARN.getName(),
                new ExternalResource(ExternalLink.GSM_LEARN.getLink()));
        gsmLearnLinkButton.setTargetName("_blank");
        gsmLearnLinkButton.setSizeUndefined();

        linkLayout.addComponents(gsmLondonMainLinkButton, studentPortalLinkButton,
                lecturerPortalLinkButton, gsmLearnLinkButton);

        Label rightSpaceLabel = new Label(" ");
        rightSpaceLabel.setSizeFull();
        linkLayout.addComponent(rightSpaceLabel);

        linkLayout.setExpandRatio(leftSpaceLabel, 1);
        linkLayout.setExpandRatio(rightSpaceLabel, 1);

        return linkLayout;
    }

    private HorizontalLayout createBottomRow() {
        final HorizontalLayout bottomRowLayout = new HorizontalLayout();
        bottomRowLayout.setWidth("100%");

        Label firstSpace = new Label(" ");
        firstSpace.setSizeFull();

        Label allRightsReservedLabel = new Label();
        allRightsReservedLabel.setValue("All Rights Reserved 2015. Created by Ryan Gilera [");
        allRightsReservedLabel.setSizeUndefined();

        Link githubLink = new Link(ExternalLink.MY_GITHUB_PAGE.getName(),
                new ExternalResource(ExternalLink.MY_GITHUB_PAGE.getLink()));
        githubLink.setTargetName("_blank");
        githubLink.setSizeUndefined();

        Label midBracketLabel = new Label("] [");
        midBracketLabel.setSizeUndefined();

        Link linkedinLink = new Link(ExternalLink.MY_LINKEDIN_PAGE.getName(),
                new ExternalResource(ExternalLink.MY_LINKEDIN_PAGE.getLink()));
        linkedinLink.setTargetName("_blank");
        linkedinLink.setSizeUndefined();

        Label endOfLabel = new Label("]");
        endOfLabel.setSizeFull();

        bottomRowLayout.addComponents(firstSpace, allRightsReservedLabel,
                githubLink, midBracketLabel, linkedinLink, endOfLabel);
        bottomRowLayout.setExpandRatio(firstSpace, 1);
        bottomRowLayout.setExpandRatio(endOfLabel, 1);
        return bottomRowLayout;

    }

}
