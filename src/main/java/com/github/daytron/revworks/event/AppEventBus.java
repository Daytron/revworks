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
package com.github.daytron.revworks.event;

import com.github.daytron.revworks.MainUI;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Wrapper for Guava's EventBus. Defines static methods for EventBus actions.
 * Handles all subscriber exceptions.
 *
 * @author Ryan Gilera
 */
public class AppEventBus implements SubscriberExceptionHandler {

    private final EventBus eventBus = new EventBus(this);

    public static void post(final Object event) {
        MainUI.getAppEventbus().eventBus.post(event);
    }

    public static void register(final Object object) {
        MainUI.getAppEventbus().eventBus.register(object);
    }

    public static void unregister(final Object object) {
        MainUI.getAppEventbus().eventBus.unregister(object);
    }

    @Override
    public void handleException(Throwable exception, SubscriberExceptionContext context) {
        Logger.getLogger(AppEventBus.class.getName())
                .log(Level.SEVERE, null, exception);
    }

}
