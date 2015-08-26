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
import com.github.daytron.revworks.model.Coursework;
import com.vaadin.data.util.BeanItemContainer;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * The base template for all SQL data retrieval exclusive to student users.
 *
 * @author Ryan Gilera
 */
public interface StudentDataProvider {

    /**
     * Retrieves coursework data from the database for the student. Returns a 
     * BeanItemContainer object containing {@link ClassTable} objects. Empty bean 
     * container is return if student has no class registered.
     * 
     * <p>
     * A temporary file is created to hold the stream data of coursework itself.
     * 
     * <p>
     * Throws custom exceptions for various error events.
     * 
     * @return BeanItemContainer object containing {@link ClassTable} objects
     * @throws SQLErrorRetrievingConnectionAndPoolException thrown if error 
     * connecting to the SQL connection pool occurs
     * @throws SQLErrorQueryException occurs when SQLException is captured
     * @throws FileNotFoundException occurs  occurs if no file is found
     * @throws IOException occurs when writing byte stream data into the file 
     * fails
     */
    public BeanItemContainer<Coursework> extractCourseworkData()
            throws SQLErrorRetrievingConnectionAndPoolException,
            SQLErrorQueryException, FileNotFoundException, IOException;
    
}
