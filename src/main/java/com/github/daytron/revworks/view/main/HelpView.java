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
package com.github.daytron.revworks.view.main;

import com.github.daytron.revworks.data.FontAwesomeIcon;
import com.github.daytron.revworks.service.CurrentUserSession;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * UI widget component for help view page.
 * 
 * @author Ryan Gilera
 */
@SuppressWarnings("serial")
public class HelpView extends Panel implements View {

    public static final String VIEW_NAME = "HelpView";
    public static final String VIEW_CAPTION = "Help Guide";
    public static final String VIEW_TITLE = "Help Guide For Students and Lecturers";

    private boolean isInitialised = false;

    /**
     * The entry point for all derived classes of View. If not currently 
     * initialised, then builds the UI components.
     * 
     * @param event ViewChangeEvent object
     */
    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        // Important!!
        // shutdown and cleanup any previous threads created
        // from coursework view and comment component if possible
        CurrentUserSession.shutdownCourseworkViewExecutorService();
        CurrentUserSession.shutdownCommentExectorService();

        if (!isInitialised) {
            initView();
            isInitialised = true;
        }

    }

    /**
     * Creates the main UI components for this view.
     */
    private void initView() {
        setSizeFull();

        VerticalLayout wrapperLayout = new VerticalLayout();
        wrapperLayout.setWidth("100%");
        wrapperLayout.setHeightUndefined();

        wrapperLayout.setMargin(true);

        Label viewTitleLabel = new Label(VIEW_TITLE);
        viewTitleLabel.setStyleName(ValoTheme.LABEL_H2);
        viewTitleLabel.addStyleName(ValoTheme.LABEL_BOLD);
        wrapperLayout.addComponent(viewTitleLabel);

        Component contentComponent = createContent();
        wrapperLayout.addComponent(contentComponent);

        wrapperLayout.setExpandRatio(contentComponent, 1);

        setContent(wrapperLayout);
    }

    /**
     * Creates the core content of this view.
     * 
     * @return Component object
     */
    private Component createContent() {
        final VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);

        Label intro = new Label();
        intro.setContentMode(ContentMode.HTML);

        intro.setValue("<p>In this section, I have provided answers to commonly "
                + "asked questions. If none of these answers help you, please "
                + "feel free to email me "
                + "at <b>ryangilera@gmail.com</b>. I am also online most of time on "
                + "Google hangouts (same email) as well, "
                + "if you prefer such method of communication. To begin, click "
                + "on any of the question you would like to know the answer.</p>");
        layout.addComponent(intro);
        

        Accordion accordion = new Accordion();
        accordion.setSizeFull();

        // 1st Content
        VerticalLayout content1 = new VerticalLayout();
        content1.setWidth("100%");
        content1.setMargin(true);
        
        Label content1Label1 = new Label();
        content1Label1.setContentMode(ContentMode.HTML);
        content1Label1.setValue("<p>First look up to the menu bar and click <b>Submit "
                + "Coursework</b> button. This will point you to the coursework "
                + "submission page. </p>");
        content1.addComponent(content1Label1);

        Label content1Label2 = new Label();
        content1Label2.setContentMode(ContentMode.HTML);
        content1Label2.setValue("<ul>"
                + "<li>Select the appropriate class.</li>"
                + "<li>Add a title, preferably, the title of your "
                + "coursework.</li>"
                + "<li>Click <b>choose file</b> button. This opens your local "
                + "file chooser "
                + "window. Make sure your document is of pdf file format.</li>"
                + "<li>Browse and select your coursework.</li>"
                + "<li>Click upload button and wait for the progress bar to "
                + "reach the end.</li>"
                + "<li>Tick the user agreements.</li>"
                + "<li>Click <b>send</b> button.</li>"
                + "</ul>");
        content1.addComponent(content1Label2);

        content1.setExpandRatio(content1Label1, 1);
        content1.setExpandRatio(content1Label2, 1);

        accordion.addTab(content1, "How do I submit my coursework?");

        // 2nd Content
        VerticalLayout content2 = new VerticalLayout();
        content2.setWidth("100%");
        content2.setMargin(true);
        
        Label content2Label1 = new Label();
        content2Label1.setContentMode(ContentMode.HTML);
        content2Label1.setValue("<p>Error messages are notifications that "
                + "require the highest user attention, with alert colors, "
                + "and they require the user to click the message to dismiss"
                + " it. Although there is a close box in the right corner "
                + "to indicate visually that the user has to click it, "
                + "you can click anywhere on the error box to dismiss it. "
                + "Unlike with other notifications, the user can not interact "
                + "with the application while the error message is displayed.</p>");
        content2.addComponent(content2Label1);

        accordion.addTab(content2, "Why can't I click anywhere when there is "
                + "an error notification on top of the page?");

        // 3rd Content
        VerticalLayout content3 = new VerticalLayout();
        content3.setWidth("100%");
        content3.setMargin(true);
        
        Label content3Label1 = new Label();
        content3Label1.setContentMode(ContentMode.HTML);
        content3Label1.setValue("<p>Idle time is 20 minutes at most before the "
                + "session closes. If such event occured, simply reload the page "
                + "or click the notification box to launch the login page.</p>");
        content3.addComponent(content3Label1);

        accordion.addTab(content3, "How long is the idle time before it "
                + "automatically ends my current session?");
        
        // 4th Content
        VerticalLayout content4 = new VerticalLayout();
        content4.setWidth("100%");
        content4.setMargin(true);
        
        Label content4Label1 = new Label();
        content4Label1.setContentMode(ContentMode.HTML);
        content4Label1.setValue("<p>Portable Document Format (PDF) is the file "
                + "standard for the electronic exchange of documents. PDF "
                + "ensures your printed or viewed file retains the formatting "
                + "that you intended. PDF file format is the defacto document "
                + "standard that is widely compatible to all OS platforms and "
                + "web browsers.</p>");
        content4.addComponent(content4Label1);
        
        Label content4Label2 = new Label();
        content4Label2.setContentMode(ContentMode.HTML);
        content4Label2.setValue("<p>There are multiple ways to convert your "
                + "document to PDF file format. If you're using Microsoft's "
                + "Office Word, you can easily convert the document by saving it "
                + "as PDF (SAVE AS > PDF). Alternatively, there are lots of "
                + "online document PDF converter out there, you may start your "
                + "search <a href=\"http://lmgtfy.com/?q=convert+to+pdf+online\">here"
                + "&nbsp;" + FontAwesomeIcon.EXTERNAL_LINK.getLgSizeWithNoSpace()
                + "</a>.</p>");
        content4.addComponent(content4Label2);

        accordion.addTab(content4, "Why PDF? and How can I convert my document to PDF?");

        // 5th Content
        VerticalLayout content5 = new VerticalLayout();
        content5.setWidth("100%");
        content5.setMargin(true);
        
        Label content5Label1 = new Label();
        content5Label1.setContentMode(ContentMode.HTML);
        content5Label1.setValue("<ul>"
                + "<li>Click on <b>My Courseworks</b> button from the "
                + "menu bar on top of the page, to load the "
                + "list of courseworks in table view.</li>"
                + "<li>After which, the table loads. The default file selection "
                + "is on the first item. You may click any item/file you wish "
                + "to view.</li>"
                + "<li>Then click <b>View Coursework</b> button "
                + "located on top-right area of this table. This will transport "
                + "you to coursework view.</li>"
                + "</ul>");
        content5.addComponent(content5Label1);
        
        Label content5Label2 = new Label();
        content5Label2.setContentMode(ContentMode.HTML);
        content5Label2.setValue("<p style=\"color:red;font-size:1.1rem;\">"
                + "PLEASE be aware, depending on the number of pages, loading "
                + "time may take awhile. This is due to hardware limitation of "
                + "my small rented cloud server. Please just be patient. "
                + "If there is no progress bar (BLUE) on top blinking, you may "
                + "sign out and start again. If you would like to contact me, "
                + "please see my email above. Thank you.</p>");
        content5.addComponent(content5Label2);
        
        accordion.addTab(content5, "How can I view my coursework?");
        
        // 6th Content
        VerticalLayout content6 = new VerticalLayout();
        content6.setWidth("100%");
        content6.setMargin(true);
        
        Label content6Label1 = new Label();
        content6Label1.setContentMode(ContentMode.HTML);
        content6Label1.setValue("<p>Click the expand button located on "
                + "top right area of the coursework view (above the comment "
                + "panel).</p>");
        content6.addComponent(content6Label1);

        accordion.addTab(content6, "How can I expand/maximise my coursework view?");
        
        // 7th Content
        VerticalLayout content7 = new VerticalLayout();
        content7.setWidth("100%");
        content7.setMargin(true);
        
        Label content7Label1 = new Label();
        content7Label1.setContentMode(ContentMode.HTML);
        content7Label1.setValue("<p>On top where the header section of the comment panel "
                + "(where it says \"Comments\") is located, "
                + "click the <b>x</b> button (alongside) to close this panel.</p>");
        content7.addComponent(content7Label1);

        accordion.addTab(content7, "How can I close temporarily the comment panel "
                + "in the coursework view?");
        
        // 8th Content
        VerticalLayout content8 = new VerticalLayout();
        content8.setWidth("100%");
        content8.setMargin(true);
        
        Label content8Label1 = new Label();
        content8Label1.setContentMode(ContentMode.HTML);
        content8Label1.setValue("<p>Locate the Notes section and "
                + "click the appropriate note button you would like to "
                + "view, then it will display its corresponding comments.</p>");
        content8.addComponent(content8Label1);

        accordion.addTab(content8, "How can I view the comments?");
        
        // 9th Content
        VerticalLayout content9 = new VerticalLayout();
        content9.setWidth("100%");
        content9.setMargin(true);
        
        Label content9Label1 = new Label();
        content9Label1.setContentMode(ContentMode.HTML);
        content9Label1.setValue("<p>Locate the Notes section and "
                + "on top lies the header (where it says Notes). "
                + "Right next to the label, click the PLUS (+) symbol button "
                + "to launch an empty comment panel. Then start typing and click"
                + " send to submit "
                + "your first comment. This will create a new note. "
                + "To cancel, simply <b>don't click</b> the send button and close the "
                + "comment panel.</p>");
        content9.addComponent(content9Label1);

        accordion.addTab(content9, "How can I create a new note?");
        
        // 10th Content
        VerticalLayout content10 = new VerticalLayout();
        content10.setWidth("100%");
        content10.setMargin(true);
        
        Label content10Label1 = new Label();
        content10Label1.setContentMode(ContentMode.HTML);
        content10Label1.setValue("<p>In the coursework viewer, "
                + "on top lies the header (where it says Coursework Viewer). "
                + "Right next to the label, there are various componets you can "
                + "use/click. The left and right arrows pertains to the "
                + "direction you wish to turn the "
                + "page (one page at a time). If you want to jump to a "
                + "custom page, simply enter the page number on the small text "
                + "field and press enter key.</p>");
        content10.addComponent(content10Label1);

        accordion.addTab(content10, "How can I turn to another page of a "
                + "coursework in "
                + "the coursework viewer?");
        
        // Creates a panel for FAQs
        Panel panel = new Panel("Frequently Asked Questions");
        panel.addStyleName("help-view-main-panel");
        panel.setSizeFull();
        panel.setContent(accordion);

        layout.addComponent(panel);
        
        layout.setExpandRatio(panel, 1);

        return layout;
    }
    
}
