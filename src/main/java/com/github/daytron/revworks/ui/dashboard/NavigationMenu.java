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

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.server.Resource;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;

/**
 * The ui component for dashboard's main menu. It also acts as a wrapper for
 * {@link Navigator} class method, addView() to create menu items and sets their
 * corresponding views.
 *
 * @author Ryan Gilera
 */
public class NavigationMenu extends HorizontalLayout {

    private final Navigator navigator;
    private final MenuBar menuBar;

    public NavigationMenu(Navigator navigator) {
        this.navigator = navigator;

        setMargin(true);
        setSpacing(true);

        this.menuBar = new MenuBar();
        menuBar.setWidth("100%");

        addComponent(menuBar);
    }

    /**
     * Registers a pre-made view instance object in the navigation menu and in
     * the {@link Navigator}.
     *
     * @see Navigator#addView(String, View)
     *
     * @param view View instance to register
     * @param viewName View name
     * @param viewCaption View caption in the menu
     * @param icon View icon in the menu
     */
    public void addView(View view, final String viewName,
            final String viewCaption, Resource icon) {
        navigator.addView(viewName, view);
        addMenuItem(viewName, viewCaption, icon);
    }

    /**
     * Creates a new menu item and attach a click behaviour to it.
     *
     * @param name View name
     * @param caption View caption in the menu
     * @param icon View icon in the menu
     */
    private void addMenuItem(final String name, String caption,
            Resource icon) {
        Command click = new MenuItemClickBehaviour(name);

        final MenuBar.MenuItem homeMenuItem = menuBar.addItem(caption, click);
        homeMenuItem.setIcon(icon);

    }

    /**
     * Registers a view in the navigation menu and in the {@link Navigator}
     * based on a view class.
     *
     * @see Navigator#addView(String, Class)
     *
     * @param viewClass Class of the views to create
     * @param viewName View name
     * @param caption View caption in the menu
     * @param icon View icon in the menu
     */
    public void addView(Class<? extends View> viewClass, final String viewName,
            String caption, Resource icon) {
        navigator.addView(viewName, viewClass);
        addMenuItem(viewName, caption, icon);
    }

    /**
     * Inner class that implements {@link Command} interface to define the menu
     * item's click behaviour.
     */
    class MenuItemClickBehaviour implements Command {

        private final String viewName;

        public MenuItemClickBehaviour(String viewName) {
            this.viewName = viewName;
        }

        @Override
        public void menuSelected(MenuBar.MenuItem selectedItem) {
            navigator.navigateTo(viewName);
        }

    }

}
