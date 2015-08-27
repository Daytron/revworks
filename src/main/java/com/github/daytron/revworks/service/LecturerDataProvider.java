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
package com.github.daytron.revworks.service;

import com.github.daytron.revworks.exception.SQLErrorQueryException;
import com.github.daytron.revworks.exception.SQLErrorRetrievingConnectionAndPoolException;
import com.github.daytron.revworks.model.ClassTable;
import com.vaadin.data.util.BeanItemContainer;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The base template for all SQL data retrieval exclusive to lecturer users.
 *
 * @author Ryan Gilera
 */
public interface LecturerDataProvider {

    /**
     * Retrieves coursework data from the database for the lecturer. Returns a 
     * ConcurrentHashMap object with ClassTable as its key and BeanItemContainer 
     * as the value. Empty map is return if lecturer has no class registered. 
     * BeanItemContainer becomes empty if no coursework data is found for a 
     * particular {@link ClassTable}.
     * 
     * <p>
     * A temporary file is created to hold the stream data of coursework itself.
     * 
     * <p>
     * Throws custom exceptions for various error events.
     * 
     * @return ConcurrentHashMap with ClassTable as its key and BeanItemContainer 
     * as the value
     * @throws SQLErrorRetrievingConnectionAndPoolException thrown if error 
     * connecting to the SQL connection pool occurs
     * @throws SQLErrorQueryException occurs when SQLException is captured
     * @throws java.io.IOException occurs when writing byte stream data into the file
     */
    public ConcurrentHashMap<ClassTable,BeanItemContainer> extractCourseworkData() 
            throws 
            SQLErrorRetrievingConnectionAndPoolException, SQLErrorQueryException, 
            IOException;
    
}
