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
package com.github.daytron.revworks.model;

import java.time.LocalDateTime;

/**
 * Comment model class for Comment table.
 * 
 * @author Ryan Gilera
 */
public class Comment {
    private final int id;
    private final User sender;
    private final User receiver;
    private final String message;
    private final LocalDateTime dateSubmitted;

    public Comment(int id, User sender, User receiver, String message, 
            LocalDateTime dateSubmitted) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.dateSubmitted = dateSubmitted;
    }

    public User getSender() {
        return sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getDateSubmitted() {
        return dateSubmitted;
    }

    public int getId() {
        return id;
    }
    
}
