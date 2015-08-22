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
package com.github.daytron.revworks.view.admin;

import com.github.daytron.revworks.data.FontAwesomeIcon;
import com.github.daytron.revworks.event.AppEvent;
import com.github.daytron.revworks.event.AppEventBus;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.themes.ValoTheme;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Ryan Gilera
 */
@SuppressWarnings("serial")
final class DashboardMenu extends CssLayout {
    private final Navigator navigator;
    private final CssLayout menuLayout;
    private final CssLayout menuSection;
    
    private final Map<String, Button> listOfButtons = new HashMap<>();
    
    DashboardMenu(Navigator navigator) {
        this.navigator = navigator;
        
        setPrimaryStyleName(ValoTheme.MENU_ROOT);
        this.menuSection = new CssLayout();
        this.menuSection.addStyleName(ValoTheme.MENU_PART);
        
        // The Menu header section
        final HorizontalLayout header = new HorizontalLayout();
        header.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        header.addStyleName(ValoTheme.MENU_TITLE);
        header.setSpacing(true);
        Label menuTitle = new Label("<h3>" + FontAwesomeIcon.SERVER.getLgSize()
                + "Admin</h3>");
        menuTitle.setContentMode(ContentMode.HTML);
        menuTitle.setSizeUndefined();
        header.addComponent(menuTitle);
        menuSection.addComponent(header);
        
        // The logout section
        MenuBar logoutMenuItem = new MenuBar();
        logoutMenuItem.setStyleName(ValoTheme.MENUBAR_BORDERLESS);
        logoutMenuItem.addItem("Signout", FontAwesome.SIGN_OUT, new MenuBar.Command() {

            @Override
            public void menuSelected(MenuBar.MenuItem selectedItem) {
                AppEventBus.post(new AppEvent.UserLogoutRequestEvent());
            }
        });
        
        logoutMenuItem.addStyleName("admin-logout-menu");
        menuSection.addComponent(logoutMenuItem);
        
        menuLayout = new CssLayout();
        menuLayout.setPrimaryStyleName("valo-menuitems");
        menuSection.addComponent(menuLayout);
        
        addComponent(menuSection);
    }
    
    public void addView(View view, final String name, String caption,
            Resource icon) {
        navigator.addView(name, view);
        createViewButton(name, caption, icon);
    }
    
    public void addView(Class<? extends View> viewClass, final String name,
            String caption, Resource icon) {
        navigator.addView(name, viewClass);
        createViewButton(name, caption, icon);
    }
    
    
    private void createViewButton(final String name, String caption,
            Resource icon) {
        Button viewButton = new Button(caption, new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                navigator.navigateTo(name);

            }
        });
        viewButton.setPrimaryStyleName(ValoTheme.MENU_ITEM);
        viewButton.setIcon(icon);
        menuLayout.addComponent(viewButton);
        listOfButtons.put(name, viewButton);
    }
    
    public void setActiveView(String viewName) {
        for (Button button : listOfButtons.values()) {
            button.removeStyleName("selected");
        }
        
        Button selected = listOfButtons.get(viewName);
        
        if (selected != null) {
            selected.addStyleName("selected");
        }
        
        menuSection.removeStyleName("valo-menu-visible");
    }
    
}
