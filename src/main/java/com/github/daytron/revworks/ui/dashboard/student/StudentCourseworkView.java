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
package com.github.daytron.revworks.ui.dashboard.student;

import com.github.daytron.revworks.MainUI;
import com.github.daytron.revworks.data.ErrorMsg;
import com.github.daytron.revworks.event.AppEvent;
import com.github.daytron.revworks.event.AppEventBus;
import com.github.daytron.revworks.exception.SQLErrorQueryException;
import com.github.daytron.revworks.exception.SQLErrorRetrievingConnectionAndPoolException;
import com.github.daytron.revworks.model.Coursework;
import com.github.daytron.revworks.model.Review;
import com.github.daytron.revworks.presenter.StudentReviewButtonListener;
import com.github.daytron.revworks.util.NotificationUtil;
import com.github.daytron.revworks.util.PdfRenderer;
import com.vaadin.data.Property;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.odftoolkit.odfdom.converter.core.ODFConverterException;

/**
 *
 * @author Ryan Gilera
 */
@SuppressWarnings("serial")
public class StudentCourseworkView extends VerticalLayout implements View {

    public static final String VIEW_NAME = "CourseworkView";
    public static final String VIEW_TITLE = "Coursework Display View";
    
    private boolean isInitialised = false;
    private Coursework coursework;
    private List<File> listOfPdfPages;
    private Panel courseworkPagePanel;
    private int currentPage;
    private final TextField pageField;
    private VerticalLayout scrollReviewLayout;
     private StudentCommentComponent commentLayout;
    private HorizontalLayout coreContentLayout;

    private Map<Integer, Button> listOfReviewButtons;
    
    public StudentCourseworkView() {
        this.pageField = new TextField();
    }
    
    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        if (!isInitialised) {
            try {
                listOfReviewButtons = new HashMap<>();
                coursework = MainUI.get().getStudentDataProvider()
                        .extractReviews();
                
                try {
                    listOfPdfPages = new ArrayList<>();
                    initView();
                } catch (Exception ex) {
                    Logger.getLogger(StudentCourseworkView.class.getName())
                            .log(Level.SEVERE, null, ex);
                    NotificationUtil.showError(
                            ErrorMsg.DATA_FETCH_ERROR.getText(),
                            ErrorMsg.CONSULT_YOUR_ADMIN.getText());
                    return;
                }
                
                isInitialised = true;
            } catch (SQLErrorRetrievingConnectionAndPoolException | 
                    SQLErrorQueryException ex) {
                Logger.getLogger(StudentCourseworkView.class.getName())
                        .log(Level.SEVERE, null, ex);
                NotificationUtil.showError(
                        ErrorMsg.DATA_FETCH_ERROR.getText(),
                        ErrorMsg.CONSULT_YOUR_ADMIN.getText());
            }
        }
    }
    
    private void initView() throws ODFConverterException, Exception {
        final CssLayout expanderWraperLayout = new CssLayout();
        expanderWraperLayout.setWidth("100%");
        expanderWraperLayout.addStyleName(".wrapper-layout");

        CssLayout contentLayout = new CssLayout();
        contentLayout.setSizeFull();
        contentLayout.addStyleName(ValoTheme.LAYOUT_CARD);

        contentLayout.addComponent(createHeaderView(expanderWraperLayout));
        contentLayout.addComponent(createContentLayout());
        expanderWraperLayout.addComponent(contentLayout);

        addComponent(expanderWraperLayout);
    }
    
    
    private HorizontalLayout createHeaderView(final CssLayout expanderLayout) {
        HorizontalLayout toolbarLayout = new HorizontalLayout();
        toolbarLayout.addStyleName("content-toolbar");
        toolbarLayout.setWidth("100%");

        Label contentLabel = new Label(VIEW_TITLE);
        contentLabel.addStyleName(ValoTheme.LABEL_H4);
        contentLabel.setSizeFull();
        toolbarLayout.addComponent(contentLabel);

        final Button expanderButton = new Button();
        expanderButton.setIcon(FontAwesome.EXPAND);
        expanderButton.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        expanderButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        expanderButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (!expanderLayout.getStyleName().contains("max")) {
                    event.getButton().setIcon(FontAwesome.COMPRESS);
                    AppEventBus.post(new AppEvent.ToggleCourseworkViewEvent(
                            expanderLayout, true));
                } else {
                    expanderLayout.removeStyleName("max");
                    event.getButton().setIcon(FontAwesome.EXPAND);
                    AppEventBus.post(new AppEvent.ToggleCourseworkViewEvent(
                            expanderLayout, false));
                }
            }
        });
        toolbarLayout.addComponent(expanderButton);

        toolbarLayout.setExpandRatio(contentLabel, 1);
        return toolbarLayout;
    }
    private VerticalLayout createContentLayout() throws IOException,
            ODFConverterException, Exception {
        final VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setWidth("100%");

        coreContentLayout = new HorizontalLayout();
        coreContentLayout.setWidth("100%");
        coreContentLayout.setHeight("600px");
        coreContentLayout.setSpacing(true);

        // Coursework display viewer
        CssLayout viewerLayout = createCourseworkViewer();

        // Scroll Layout
        CssLayout scrollLayout = createScrollComponent();

        // Comment Layout
        // By default it is a placeholder hidden with reviewId 0
        commentLayout
                = new StudentCommentComponent(coursework, 0);
        commentLayout.setVisible(false);

        coreContentLayout.addComponent(viewerLayout);
        coreContentLayout.addComponent(scrollLayout);
        coreContentLayout.addComponent(commentLayout);
        coreContentLayout.setExpandRatio(viewerLayout, 7);
        coreContentLayout.setExpandRatio(scrollLayout, 1);
        coreContentLayout.setExpandRatio(commentLayout, 2);

        verticalLayout.addComponent(coreContentLayout);
        verticalLayout.addComponent(new Label("Document ready."));

        return verticalLayout;

    }
    
    private CssLayout createCourseworkViewer() throws IOException, ODFConverterException, Exception {
        final CssLayout viewerLayout = new CssLayout();
        viewerLayout.setWidth("100%");
        viewerLayout.setStyleName(ValoTheme.LAYOUT_CARD);

        // Header
        final HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.addStyleName("v-panel-caption");
        headerLayout.setWidth("100%");
        headerLayout.setSpacing(true);

        Label titleLabel = new Label("Coursework Viewer");
        titleLabel.setStyleName(ValoTheme.LABEL_BOLD);
        titleLabel.setSizeFull();
        headerLayout.addComponent(titleLabel);

        
        pageField.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        pageField.setWidth(3, Unit.EM);
        pageField.setValue("1");
        pageField.setImmediate(true);
        pageField.addValueChangeListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                try {
                    int page = Integer.parseInt(pageField.getValue());

                    if (page == currentPage) {
                        return;
                    }

                    try {
                        FileResource fileResource
                                = new FileResource(listOfPdfPages.get(page - 1));
                        Image image = new Image(null, fileResource);
                        image.setSizeUndefined();

                        courseworkPagePanel.setContent(image);
                        currentPage = page;
                    } catch (Exception e) {
                        NotificationUtil.showError(
                        ErrorMsg.DATA_FETCH_ERROR.getText(),
                        ErrorMsg.CONSULT_YOUR_ADMIN.getText());
                    }
                } catch (NumberFormatException e) {
                }
            }
        });

        Button leftButton = new Button();
        leftButton.setIcon(FontAwesome.ARROW_LEFT);
        leftButton.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        leftButton.addStyleName(ValoTheme.BUTTON_SMALL);
        leftButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        leftButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (currentPage > 1) {
                    try {
                        FileResource fileResource
                                = new FileResource(listOfPdfPages.get(currentPage - 2));
                        Image image = new Image(null, fileResource);
                        image.setWidth("100%");

                        courseworkPagePanel.setContent(image);
                        currentPage = currentPage - 1;
                        pageField.setValue("" + currentPage);
                    } catch (Exception e) {
                        NotificationUtil.showError(
                        ErrorMsg.DATA_FETCH_ERROR.getText(),
                        ErrorMsg.CONSULT_YOUR_ADMIN.getText());
                    }
                }
            }
        });

        Button rightButton = new Button();
        rightButton.setIcon(FontAwesome.ARROW_RIGHT);
        rightButton.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        rightButton.addStyleName(ValoTheme.BUTTON_SMALL);
        rightButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        rightButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (currentPage < listOfPdfPages.size()) {
                    try {
                        FileResource fileResource
                                = new FileResource(listOfPdfPages.get(currentPage));
                        Image image = new Image(null, fileResource);
                        image.setWidth("100%");

                        courseworkPagePanel.setContent(image);
                        currentPage = currentPage + 1;
                        pageField.setValue("" + currentPage);
                    } catch (Exception e) {
                        NotificationUtil.showError(
                        ErrorMsg.DATA_FETCH_ERROR.getText(),
                        ErrorMsg.CONSULT_YOUR_ADMIN.getText());
                    }
                }
            }
        });

        // Content
        courseworkPagePanel = new Panel();
        courseworkPagePanel.setWidth("100%");
        courseworkPagePanel.setHeight("565px");

        listOfPdfPages = PdfRenderer.extractPages(coursework);

        // Label for total of pages next to the arrows in header
        Label totalPagesLabel = new Label("/" + listOfPdfPages.size());
        totalPagesLabel.setSizeUndefined();

        Image image = new Image(null, new FileResource(listOfPdfPages.get(0)));
        image.setWidth("100%");
        courseworkPagePanel.setContent(image);
        currentPage = 1;

        headerLayout.addComponent(leftButton);
        headerLayout.addComponent(pageField);
        headerLayout.addComponent(totalPagesLabel);
        headerLayout.addComponent(rightButton);

        headerLayout.setExpandRatio(titleLabel, 1);

        viewerLayout.addComponent(headerLayout);
        viewerLayout.addComponent(courseworkPagePanel);

        return viewerLayout;
    }
    
    
    private CssLayout createScrollComponent() {
        final CssLayout scrollLayout = new CssLayout();
        scrollLayout.setWidth("100%");
        scrollLayout.setStyleName(ValoTheme.LAYOUT_CARD);

        // Header
        final HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.addStyleName("v-panel-caption");
        headerLayout.setWidth("100%");

        Label titleLabel = new Label("Review");
        titleLabel.setStyleName(ValoTheme.LABEL_BOLD);
        titleLabel.setSizeFull();
        headerLayout.addComponent(titleLabel);

        scrollLayout.addComponent(headerLayout);

        // Content
        Panel scrollPanel = new Panel();
        scrollPanel.setWidth("100%");
        scrollPanel.setHeight("565px");

        scrollReviewLayout = new VerticalLayout();
        scrollReviewLayout.setWidth("100%");
        scrollReviewLayout.setHeight(null);

        for (Review review : coursework.getListOfReviews()) {
            Button reviewButton = new Button("p" + review.getPageNumber());
            reviewButton.setSizeFull();

            listOfReviewButtons.put(review.getId(), reviewButton);
            scrollReviewLayout.addComponent(reviewButton);

            reviewButton.addClickListener(new StudentReviewButtonListener(this, 
            review.getPageNumber()));
        }
        scrollPanel.setContent(scrollReviewLayout);
        scrollLayout.addComponent(scrollPanel);

        return scrollLayout;

    }

    public StudentCommentComponent getCommentLayout() {
        return commentLayout;
    }

    public Map<Integer, Button> getListOfReviewButtons() {
        return listOfReviewButtons;
    }

    public Coursework getCoursework() {
        return coursework;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public HorizontalLayout getCoreContentLayout() {
        return coreContentLayout;
    }

    public void setCommentLayout(StudentCommentComponent commentLayout) {
        this.commentLayout = commentLayout;
    }
    
    public void flipToPage(int pageNumber) {
        FileResource fileResource
                = new FileResource(listOfPdfPages.get(pageNumber - 1));
        Image image = new Image(null, fileResource);
        image.setWidth("100%");

        courseworkPagePanel.setContent(image);
        currentPage = pageNumber;
        pageField.setValue("" + currentPage);
    }
    

}
