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
 * Review model class for Review table.
 * 
 * @author Ryan Gilera
 */
public class Review {
    private final int id;
    private final int pageNumber;
    private final LocalDateTime dateSubmitted;

    public Review(int id, int pageNum, LocalDateTime dateSubmitted) {
        this.id = id;
        this.pageNumber = pageNum;
        this.dateSubmitted = dateSubmitted;
    }

    public int getId() {
        return id;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public LocalDateTime getDateSubmitted() {
        return dateSubmitted;
    }
    
}
